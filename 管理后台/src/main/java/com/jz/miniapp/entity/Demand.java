package com.jz.miniapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 需求实体类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@TableName("demands")
@Schema(description = "需求信息")
public class Demand implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "需求 ID")
    private Long id;

    /**
     * 标题
     */
    @TableField("title")
    @Schema(description = "需求标题")
    private String title;

    /**
     * 描述
     */
    @TableField("description")
    @Schema(description = "需求描述")
    private String description;

    /**
     * 分类 ID
     */
    @TableField("category_id")
    @Schema(description = "服务分类 ID")
    private Long categoryId;

    /**
     * 服务类型 (1:小时工，2:天工，3:包月)
     */
    @TableField("service_type")
    @Schema(description = "服务类型 (1:小时工，2:天工，3:包月)")
    private Integer serviceType;

    /**
     * 期望价格
     */
    @TableField("expected_price")
    @Schema(description = "期望价格")
    private BigDecimal expectedPrice;

    /**
     * 价格单位 (小时/天/月)
     */
    @TableField("price_unit")
    @Schema(description = "价格单位")
    private String priceUnit;

    /**
     * 最小服务时长
     */
    @TableField("min_duration")
    @Schema(description = "最小服务时长 (小时)")
    private Double minDuration;

    /**
     * 最大服务时长
     */
    @TableField("max_duration")
    @Schema(description = "最大服务时长 (小时)")
    private Double maxDuration;

    /**
     * 省份
     */
    @TableField("province")
    @Schema(description = "省份")
    private String province;

    /**
     * 城市
     */
    @TableField("city")
    @Schema(description = "城市")
    private String city;

    /**
     * 区县
     */
    @TableField("district")
    @Schema(description = "区县")
    private String district;

    /**
     * 详细地址
     */
    @TableField("address")
    @Schema(description = "详细地址")
    private String address;

    /**
     * 纬度
     */
    @TableField("latitude")
    @Schema(description = "纬度")
    private Double latitude;

    /**
     * 经度
     */
    @TableField("longitude")
    @Schema(description = "经度")
    private Double longitude;

    /**
     * 服务时间
     */
    @TableField("service_time")
    @Schema(description = "服务时间")
    private LocalDateTime serviceTime;

    /**
     * 联系人姓名
     */
    @TableField("contact_name")
    @Schema(description = "联系人姓名")
    private String contactName;

    /**
     * 联系人电话
     */
    @TableField("contact_phone")
    @Schema(description = "联系人电话")
    private String contactPhone;

    /**
     * 图片 URLs (逗号分隔)
     */
    @TableField("images")
    @Schema(description = "图片 URLs")
    private String images;

    /**
     * 发布人 ID
     */
    @TableField("publisher_id")
    @Schema(description = "发布人 ID")
    private Long publisherId;

    /**
     * 接单人 ID
     */
    @TableField("taker_id")
    @Schema(description = "接单人 ID")
    private Long takerId;

    /**
     * 状态 (1:招募中，2:已接单，3:进行中，4:已完成，5:已取消)
     */
    @TableField("status")
    @Schema(description = "状态 (1:招募中，2:已接单，3:进行中，4:已完成，5:已取消)")
    private Integer status;

    /**
     * 浏览次数
     */
    @TableField("view_count")
    @Schema(description = "浏览次数")
    private Integer viewCount;

    /**
     * 足迹数
     */
    @TableField("footprint_count")
    @Schema(description = "足迹数")
    private Integer footprintCount;

    /**
     * 创建时间
     */
    @TableField("created_at")
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

}
