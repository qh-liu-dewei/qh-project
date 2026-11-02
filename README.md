# 食光外卖（Take-out）

一套前后端分离的外卖订餐系统，包含 **微信小程序用户端** 和 **Web 商家管理端**。

后端基于 Spring Boot + MyBatis + MySQL，使用 Redis 做缓存、JWT 做双端鉴权、WebSocket 推送来单提醒；管理端使用 Vue 2 + Element UI，用户端为 uni-app 编译的微信小程序。

![管理端登录](docs/screenshots/login.jpg)

## 功能

### 用户端（微信小程序）

- 店铺信息、菜品/套餐浏览、分类切换、规格选择
- 购物车、下单、支付、订单详情与催单
- 历史订单、再来一单、地址管理、订单备注

### 管理端（Web）

- **工作台**：今日营业额、有效订单、订单完成率、客单价、新增用户
- **订单管理**：接单、派送、完成、取消、条件查询
- **菜品 / 套餐 / 分类 / 员工管理**：增删改查、起售停售、图片上传
- **数据统计**：营业额、用户、订单、销量 Top10，支持 Excel 导出
- **营业状态**切换、**WebSocket** 来单 / 催单实时提醒（含语音播报）

### 菜品示例

![菜品](docs/screenshots/dishes.jpg)

## 技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | Java 8+、Spring Boot 2.7、Spring MVC、MyBatis、PageHelper、MySQL 8、Druid |
| 缓存 / 鉴权 | Redis（菜品、套餐、店铺状态缓存）、JWT + 拦截器（管理端 / 用户端双端鉴权） |
| 实时 / 任务 | WebSocket（来单、催单推送）、Spring Task 定时任务（超时订单取消、派送单自动完成） |
| 其他 | AOP 公共字段自动填充、全局异常处理、Knife4j 接口文档、阿里云 OSS、POI 报表导出 |
| 管理端 | Vue 2、Vue Router、Vuex、Element UI、ECharts、Axios |
| 用户端 | uni-app（Vue 2 语法）、Vuex、uni-ui、微信小程序原生 API |
| 部署 | Nginx（静态站点 + 反向代理）、Spring Boot 内嵌 Tomcat |

## 项目结构

```
takeout/
├── sky-take-out/               后端（Maven 多模块）
│   ├── sky-common/             公共工具、常量、异常
│   ├── sky-pojo/               DTO / VO / Entity
│   └── sky-server/             Controller、Service、Mapper、配置
├── mp-weixin/                  微信小程序（uni-app 编译产物）
├── nginx-1.20.2/
│   ├── conf/nginx.conf         反向代理配置
│   └── html/sky/               管理端页面 + 菜品图片（/food/）
├── db/sky_take_out.sql         数据库初始化脚本（表结构 + 基础数据）
└── docs/screenshots/           README 截图
```

## 快速开始

### 1. 环境

JDK 8+、Maven、MySQL 8、Redis、Nginx、微信开发者工具

### 2. 初始化数据库

```bash
mysql -uroot -p < db/sky_take_out.sql
```

脚本会创建 `sky_take_out` 库，导入表结构以及分类、菜品、套餐、员工等基础数据（不含订单等测试数据）。

### 3. 启动后端

```bash
cd sky-take-out/sky-server/src/main/resources
# 复制配置模板，填入自己的数据库 / Redis 信息
copy application-dev.example.yml application-dev.yml

cd sky-take-out
mvn clean package -DskipTests
java -jar sky-server/target/sky-server-1.0-SNAPSHOT.jar
```

后端默认端口 `8080`。

### 4. 启动管理端

```bash
# 在 nginx-1.20.2 目录下启动 nginx（配置已包含 /api、/user、/ws 反向代理）
nginx.exe
```

浏览器访问 `http://localhost`，默认账号：**admin / 123456**

### 5. 运行小程序

用微信开发者工具导入 `mp-weixin` 目录，把 `common/vendor.js` 里的

```js
var baseUrl = 'http://localhost:8080';
```

改成后端地址，编译即可（项目已默认关闭域名校验）。

## 配置说明

- `application-dev.yml` 含数据库、Redis、微信等本地配置，**不入库**，请从 `application-dev.example.yml` 复制后填写。
- 阿里云 OSS 通过环境变量配置：`ALIYUN_OSS_ENDPOINT`、`ALIYUN_OSS_ACCESS_KEY_ID`、`ALIYUN_OSS_ACCESS_KEY_SECRET`、`ALIYUN_OSS_BUCKET`，不配置只影响后台图片上传，不影响其他功能。
- 演示环境使用**模拟支付**：小程序点「确认支付」会直接调用 `/notify/mockPaySuccess` 把订单置为已支付；接入真实微信支付需配置商户号、证书和公网 `notifyUrl`。
- 菜品图片存放在 `nginx-1.20.2/html/sky/food/`，数据库中的图片地址为 `http://localhost/food/<菜品id>.jpg`，由 Nginx 直接提供。

## 说明

- 本项目为课程项目的二次开发：重新设计了管理端 / 小程序界面，整理了品牌与部署方式。
- 菜品图片来自公开图片搜索，仅用于课程演示，正式上线请替换为自有素材。
