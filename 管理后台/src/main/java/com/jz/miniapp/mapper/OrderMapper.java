package com.jz.miniapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jz.miniapp.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper 接口
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
