package cn.qihangerp.request;

import lombok.Data;

@Data
public class OrderSearchRequest {
    private Long shopId;
    private Integer shopType;
    private String orderNum;
    private String orderStatus;
    private String startTime;
    private String endTime;
    private Integer erpPushStatus;
    private String receiverName;
    private String receiverMobile;
    private String shippingNumber;
}
