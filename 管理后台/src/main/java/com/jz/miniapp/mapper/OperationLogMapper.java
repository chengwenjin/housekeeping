package com.jz.miniapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jz.miniapp.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper 接口
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

}
