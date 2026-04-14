package com.jz.miniapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 需求信息 VO
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@Schema(description = "需求信息")
public class DemandVO {

    /**
     * 需求 ID
     */
    @Schema(description = "需求 ID")
    private Long id;

    /**
     * 标题
     */
    @Schema(description = "需求标题")
    private String title;

    /**
     * 描述
     */
    @Schema(description = "需求描述")
    private String description;

    /**
     * 分类 ID
     */
    @Schema(description = "服务分类 ID")
    private Long categoryId;

    /**
     * 分类名称
     */
    @Schema(description = "服务分类名称")
    private String categoryName;

    /**
     * 服务类型 (1:小时工，2:天工，3:包月)
     */
    @Schema(description = "服务类型")
    private Integer serviceType;

    /**
     * 服务类型文本
     */
    @Schema(description = "服务类型文本")
    private String serviceTypeText;

    /**
     * 期望价格
     */
    @Schema(description = "期望价格")
    private BigDecimal expectedPrice;

    /**
     * 价格单位
     */
    @Schema(description = "价格单位")
    private String priceUnit;

    /**
     * 最小服务时长
     */
    @Schema(description = "最小服务时长")
    private Double minDuration;

    /**
     * 最大服务时长
     */
    @Schema(description = "最大服务时长")
    private Double maxDuration;

    /**
     * 省份
     */
    @Schema(description = "省份")
    private String province;

    /**
     * 城市
     */
    @Schema(description = "城市")
    private String city;

    /**
     * 区县
     */
    @Schema(description = "区县")
    private String district;

    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    private String address;

    /**
     * 纬度
     */
    @Schema(description = "纬度")
    private Double latitude;

    /**
     * 经度
     */
    @Schema(description = "经度")
    private Double longitude;

    /**
     * 服务时间
     */
    @Schema(description = "服务时间")
    private LocalDateTime serviceTime;

    /**
     * 联系人姓名
     */
    @Schema(description = "联系人姓名")
    private String contactName;

    /**
     * 联系人电话
     */
    @Schema(description = "联系人电话")
    private String contactPhone;

    /**
     * 图片 URLs
     */
    @Schema(description = "图片 URLs")
    private java.util.List<String> images;

    /**
     * 发布人信息
     */
    @Schema(description = "发布人信息")
    private PublisherVO publisher;

    /**
     * 浏览次数
     */
    @Schema(description = "浏览次数")
    private Integer viewCount;

    /**
     * 足迹数
     */
    @Schema(description = "足迹数")
    private Integer footprintCount;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 状态文本
     */
    @Schema(description = "状态文本")
    private String statusText;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 创建时间文本
     */
    @Schema(description = "创建时间文本")
    private String createdAtText;
}
