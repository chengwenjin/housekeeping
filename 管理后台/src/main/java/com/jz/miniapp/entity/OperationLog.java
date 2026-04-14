package com.jz.miniapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@TableName("operation_logs")
@Schema(description = "操作日志信息")
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "日志 ID")
    private Long id;

    /**
     * 管理员 ID
     */
    @TableField("admin_id")
    @Schema(description = "管理员 ID")
    private Long adminId;

    /**
     * 操作人员（用户名）
     */
    @TableField("username")
    @Schema(description = "操作人员用户名")
    private String username;

    /**
     * 操作类型（CREATE/UPDATE/DELETE/QUERY/LOGIN/OTHER）
     */
    @TableField("action")
    @Schema(description = "操作类型")
    private String action;

    /**
     * 模块
     */
    @TableField("module")
    @Schema(description = "模块")
    private String module;

    /**
     * 请求方法
     */
    @TableField("method")
    @Schema(description = "请求方法")
    private String method;

    /**
     * 请求 URL
     */
    @TableField("url")
    @Schema(description = "请求 URL")
    private String url;

    /**
     * IP 地址
     */
    @TableField("ip")
    @Schema(description = "IP 地址")
    private String ip;

    /**
     * User-Agent
     */
    @TableField("user_agent")
    @Schema(description = "User-Agent")
    private String userAgent;

    /**
     * 请求参数
     */
    @TableField("request_data")
    @Schema(description = "请求参数")
    private String requestData;

    /**
     * 响应状态码
     */
    @TableField("response_code")
    @Schema(description = "响应状态码")
    private Integer responseCode;

    /**
     * 响应数据
     */
    @TableField("response_data")
    @Schema(description = "响应数据")
    private String responseData;

    /**
     * 执行时长 (ms)
     */
    @TableField("duration")
    @Schema(description = "执行时长")
    private Long duration;

    /**
     * 创建时间
     */
    @TableField("created_at")
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

}
