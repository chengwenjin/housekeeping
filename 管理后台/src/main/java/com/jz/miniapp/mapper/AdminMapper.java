package com.jz.miniapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jz.miniapp.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员 Mapper 接口
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

}
