package cn.qihangerp.api.wei.controller;



import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.mq.MqMessage;
import cn.qihangerp.common.mq.MqType;
import cn.qihangerp.common.mq.MqUtils;
import cn.qihangerp.module.open.wei.domain.WeiRefund;
import cn.qihangerp.module.open.wei.domain.bo.WeiOrderPushBo;
import cn.qihangerp.module.open.wei.service.WeiRefundService;
import cn.qihangerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/wei/refund")
public class WeiRefundController extends BaseController {
    private final WeiRefundService refundService;
    private final MqUtils mqUtils;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo goodsList(WeiRefund bo, PageQuery pageQuery) {
        PageResult<WeiRefund> result = refundService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

    @PostMapping("/push_oms")
    @ResponseBody
    public AjaxResult pushOms(@RequestBody WeiOrderPushBo bo) {
        // TODO:需要优化消息格式
        if(bo!=null && bo.getIds()!=null) {
            for(String id: bo.getIds()) {
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.WEI, MqType.REFUND_MESSAGE, id));

            }
        }
        return AjaxResult.success();
    }

}
