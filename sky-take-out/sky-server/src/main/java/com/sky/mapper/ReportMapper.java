package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface ReportMapper {
    /**
     * 按时间区间统计营业额
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 动态统计每天新增员工人数以及总人数
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
