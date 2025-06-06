package cn.qihangerp.module.order.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 发货电子面单记录表（打单记录）
 * @TableName o_ship_waybill
 */
@Data
public class OShipWaybill implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺类型
     */
    private Integer shopType;

    /**
     * 电子面单订单id(仅视频号)
     */
    private String waybillOrderId;

    /**
     * 快递单号
     */
    private String waybillCode;

    /**
     * 快递公司编码
     */
    private String logisticsCode;

    /**
     * 打印数据
     */
    private String printData;

    /**
     * 状态（1已取号2已打印3已发货）
     */
    private Integer status;

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