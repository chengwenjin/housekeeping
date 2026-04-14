package com.jz.miniapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 发布需求请求 DTO
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@Schema(description = "发布需求请求")
public class DemandDTO {

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Schema(description = "需求标题", required = true)
    private String title;

    /**
     * 描述
     */
    @NotBlank(message = "描述不能为空")
    @Schema(description = "需求描述", required = true)
    private String description;

    /**
     * 分类 ID
     */
    @NotNull(message = "分类不能为空")
    @Schema(description = "服务分类 ID", required = true)
    private Long categoryId;

    /**
     * 服务类型 (1:小时工，2:天工，3:包月)
     */
    @NotNull(message = "服务类型不能为空")
    @Schema(description = "服务类型 (1:小时工，2:天工，3:包月)", required = true)
    private Integer serviceType;

    /**
     * 期望价格
     */
    @NotNull(message = "期望价格不能为空")
    @Schema(description = "期望价格", required = true)
    private BigDecimal expectedPrice;

    /**
     * 价格单位
     */
    @Schema(description = "价格单位")
    private String priceUnit;

    /**
     * 最小服务时长
     */
    @Schema(description = "最小服务时长 (小时)")
    private Double minDuration;

    /**
     * 最大服务时长
     */
    @Schema(description = "最大服务时长 (小时)")
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
     * 图片 URLs (逗号分隔)
     */
    @Schema(description = "图片 URLs")
    private String images;
}
