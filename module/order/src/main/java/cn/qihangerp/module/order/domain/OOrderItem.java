package cn.qihangerp.module.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单明细表
 * @TableName o_order_item
 */
@Data
public class OOrderItem implements Serializable {
    /**
     * id，自增
     */
    @TableId(type = IdType.AUTO)
    private String id;

    /**
     * 订单ID（o_order外键）
     */
    private String orderId;
    /**
     * 店铺类型
     */
    private Integer shopType;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 子订单号（第三方平台）
     */
    private String subOrderNum;
    private String orderNum;

    /**
     * 第三方平台skuId
     */
    private String skuId;

    /**
     * erp系统商品id
     */
    private Long goodsId;

    /**
     * erp系统商品规格id
     */
    private Long goodsSkuId;

    /**
     * 商品标题
     */
    private String goodsTitle;

    /**
     * 商品图片
     */
    private String goodsImg;

    /**
     * 商品编码
     */
    private String goodsNum;

    /**
     * 商品规格
     */
    private String goodsSpec;

    /**
     * 商品规格编码
     */
    private String skuNum;

    /**
     * 商品单价
     */
    private Double goodsPrice;

    /**
     * 子订单金额
     */
    private Double itemAmount;
    private Double discountAmount;
    private Double payment;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 备注
     */
    private String remark;

    /**
     * 已退货数量
     */
    private Integer refundCount;

    /**
     * 售后状态 1：无售后或售后关闭，2：售后处理中，3：退款中，4： 退款成功 
     */
    private Integer refundStatus;
    private Integer orderStatus;
    private Integer hasPushErp;//是否推送到ERP
    /**
     * 发货类型（0仓库发货；1供应商代发；2联合发货）
     */
    private Integer shipType;
    /**
     * 发货状态 0 待发货 1 部分发货 2全部发货
     */
    private Integer shipStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateBy;

    private static final long serialVersionUID = 1L;


}