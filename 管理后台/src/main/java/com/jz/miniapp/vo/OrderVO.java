package com.jz.miniapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单信息 VO
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@Schema(description = "订单信息")
public class OrderVO {

    /**
     * 订单 ID
     */
    @Schema(description = "订单 ID")
    private Long id;

    /**
     * 需求 ID
     */
    @Schema(description = "需求 ID")
    private Long demandId;

    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    private String orderNo;

    /**
     * 服务者 ID
     */
    @Schema(description = "服务者 ID")
    private Long serviceProviderId;

    /**
     * 客户 ID
     */
    @Schema(description = "客户 ID")
    private Long customerId;

    /**
     * 服务分类 ID
     */
    @Schema(description = "服务分类 ID")
    private Long categoryId;

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
     * 服务时长 (小时)
     */
    @Schema(description = "服务时长")
    private Double serviceDuration;

    /**
     * 服务价格
     */
    @Schema(description = "服务价格")
    private BigDecimal servicePrice;

    /**
     * 总金额
     */
    @Schema(description = "总金额")
    private BigDecimal totalAmount;

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
     * 订单状态
     */
    @Schema(description = "订单状态")
    private Integer status;

    /**
     * 订单状态文本
     */
    @Schema(description = "订单状态文本")
    private String statusText;

    /**
     * 服务者接单备注
     */
    @Schema(description = "服务者接单备注")
    private String providerRemark;

    /**
     * 客户取消原因
     */
    @Schema(description = "客户取消原因")
    private String cancelReason;

    /**
     * 取消时间
     */
    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    /**
     * 服务完成时间
     */
    @Schema(description = "服务完成时间")
    private LocalDateTime completeTime;

    /**
     * 评价 ID
     */
    @Schema(description = "评价 ID")
    private Long reviewId;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /**
     * 创建时间文本
     */
    @Schema(description = "创建时间文本")
    private String createdAtText;
}
