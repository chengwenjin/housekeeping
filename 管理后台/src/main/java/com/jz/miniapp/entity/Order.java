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
 * 订单实体类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@TableName("orders")
@Schema(description = "订单信息")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "订单 ID")
    private Long id;

    /**
     * 需求 ID
     */
    @TableField("demand_id")
    @Schema(description = "需求 ID")
    private Long demandId;

    /**
     * 订单编号
     */
    @TableField("order_no")
    @Schema(description = "订单编号")
    private String orderNo;

    /**
     * 订单标题
     */
    @TableField("title")
    @Schema(description = "订单标题")
    private String title;

    /**
     * 服务者 ID
     */
    @TableField("service_provider_id")
    @Schema(description = "服务者 ID")
    private Long serviceProviderId;

    /**
     * 客户 ID
     */
    @TableField("customer_id")
    @Schema(description = "客户 ID")
    private Long customerId;

    /**
     * 服务分类 ID
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
     * 服务时长 (小时)
     */
    @TableField("service_duration")
    @Schema(description = "服务时长 (小时)")
    private Double serviceDuration;

    /**
     * 服务价格
     */
    @TableField("service_price")
    @Schema(description = "服务价格")
    private BigDecimal servicePrice;

    /**
     * 总金额
     */
    @TableField("total_amount")
    @Schema(description = "总金额")
    private BigDecimal totalAmount;

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
     * 服务地址（拼接字段）
     */
    @TableField(exist = false)
    @Schema(description = "服务地址")
    private String serviceAddress;

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
     * 订单状态 (1:待服务，2:服务中，3:服务完成，4:已评价，5:已取消，6:退款/售后)
     */
    @TableField("status")
    @Schema(description = "订单状态")
    private Integer status;

    /**
     * 支付状态 (0:未支付，1:已支付)
     */
    @TableField("payment_status")
    @Schema(description = "支付状态")
    private Integer paymentStatus;

    /**
     * 服务者接单备注
     */
    @TableField("provider_remark")
    @Schema(description = "服务者接单备注")
    private String providerRemark;

    /**
     * 客户取消原因
     */
    @TableField("cancel_reason")
    @Schema(description = "客户取消原因")
    private String cancelReason;

    /**
     * 取消时间
     */
    @TableField("cancel_time")
    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    /**
     * 服务完成时间
     */
    @TableField("complete_time")
    @Schema(description = "服务完成时间")
    private LocalDateTime completeTime;

    /**
     * 评价 ID
     */
    @TableField("review_id")
    @Schema(description = "评价 ID")
    private Long reviewId;

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
