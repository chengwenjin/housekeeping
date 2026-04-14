package com.jz.miniapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jz.miniapp.entity.Follow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 关注关系 Mapper 接口
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Mapper
public interface FollowMapper extends BaseMapper<Follow> {

}
