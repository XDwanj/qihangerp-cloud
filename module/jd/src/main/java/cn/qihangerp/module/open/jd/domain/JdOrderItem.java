package cn.qihangerp.module.open.jd.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 京东订单明细表
 * @TableName oms_jd_order_item
 */
@TableName(value ="oms_jd_order_item")
@Data
public class JdOrderItem implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 外键id（jd_order表id）
     */
    private Long jdOrderId;

    /**
     * 京东平台订单id
     */
    private String orderId;

    /**
     * 京东内部SKU的ID
     */
    private String skuId;

    /**
     * SKU外部ID（极端情况下不保证返回，建议从商品接口获取
     */
    private String outerSkuId;

    /**
     * 商品的名称+SKU规格
     */
    private String skuName;

    /**
     * SKU的京东价
     */
    private String jdPrice;

    /**
     * 赠送积分
     */
    private String giftPoint;

    /**
     * 	京东内部商品ID（极端情况下不保证返回，建议从商品接口获取）
     */
    private String wareId;

    /**
     * 数量
     */
    private Integer itemTotal;

    /**
     * 
     */
    private String productNo;

    /**
     * 
     */
    private String serviceName;

    /**
     * 
     */
    private String newStoreId;

    /**
     * 商品id(o_goods外键)
     */
    private Long erpGoodsId;

    /**
     * 商品skuid(o_goods_sku外键)
     */
    private Long erpGoodsSkuId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}