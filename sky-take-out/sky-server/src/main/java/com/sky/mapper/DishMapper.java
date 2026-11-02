package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    /**
     * 菜品起售停售
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE)
    @Update("update dish set status = #{status} where id = #{id}")
    void startOrStop(Dish dish);

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品
     * @param dish
     */

    @AutoFill(OperationType.INSERT)
    @Insert("insert into Dish(name, category_id, price, image, description, create_time, update_time, create_user, update_user) " +
            "value " +
            "(#{name},#{categoryId},#{price},#{image},#{description},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Dish dish);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 查询菜品数据
     * @param ids
     * @return
     */
    List<Dish> getByIds(List<Long> ids);

    /**
     * 根据id删除菜品
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据ID查询菜品数据
     * @param id
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     *修改菜品细信息
     * @param dish
     */
    void update(Dish dish);

    /**
     * 根据分类ID查询菜品数据
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);

    /**
     * 根据套餐id查询菜品
     * @param setmealId
     * @return
     */
    @Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

}
