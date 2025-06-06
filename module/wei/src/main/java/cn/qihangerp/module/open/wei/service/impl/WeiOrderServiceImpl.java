package cn.qihangerp.module.open.wei.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.module.open.wei.domain.WeiGoodsSku;
import cn.qihangerp.module.open.wei.domain.WeiOrder;
import cn.qihangerp.module.open.wei.domain.WeiOrderItem;
import cn.qihangerp.module.open.wei.mapper.WeiGoodsSkuMapper;
import cn.qihangerp.module.open.wei.mapper.WeiOrderItemMapper;
import cn.qihangerp.module.open.wei.mapper.WeiOrderMapper;
import cn.qihangerp.module.open.wei.service.WeiOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.util.List;

/**
* @author TW
* @description 针对表【oms_wei_order】的数据库操作Service实现
* @createDate 2024-06-03 16:48:31
*/
@AllArgsConstructor
@Service
public class WeiOrderServiceImpl extends ServiceImpl<WeiOrderMapper, WeiOrder>
    implements WeiOrderService {
    private final WeiOrderMapper mapper;
    private final WeiOrderItemMapper itemMapper;
    private final WeiGoodsSkuMapper goodsSkuMapper;
//    private final MQClientService mqClientService;

    @Override
    public PageResult<WeiOrder> queryPageList(WeiOrder bo, PageQuery pageQuery) {
        LambdaQueryWrapper<WeiOrder> queryWrapper = new LambdaQueryWrapper<WeiOrder>()
                .eq(bo.getShopId()!=null, WeiOrder::getShopId,bo.getShopId())
                .eq(StringUtils.hasText(bo.getOrderId()), WeiOrder::getOrderId,bo.getOrderId())
                .eq(bo.getStatus()!=null, WeiOrder::getStatus,bo.getStatus())
                ;
        if(bo.getErpSendStatus()!=null){
            if(bo.getErpSendStatus()==-1) {
                queryWrapper.lt(WeiOrder::getErpSendStatus,3);
            }else {
                queryWrapper.eq(WeiOrder::getErpSendStatus, bo.getErpSendStatus());
            }
        }

        Page<WeiOrder> page = mapper.selectPage(pageQuery.build(), queryWrapper);
        if(page.getRecords()!=null){
            for (var order:page.getRecords()) {
                order.setItems(itemMapper.selectList(new LambdaQueryWrapper<WeiOrderItem>().eq(WeiOrderItem::getOrderId,order.getOrderId())));
            }
        }
        return PageResult.build(page);
    }

    @Transactional
    @Override
    public ResultVo<Integer> saveOrder(Long shopId, WeiOrder order) {
        try {
            List<WeiOrder> orders = mapper.selectList(new LambdaQueryWrapper<WeiOrder>().eq(WeiOrder::getOrderId, order.getOrderId()));
            if (orders != null && orders.size() > 0) {
                // 存在，修改
                WeiOrder update = new WeiOrder();
                update.setId(orders.get(0).getId());
                update.setOrderId(order.getOrderId());
                update.setStatus(order.getStatus());
                update.setUpdateTime(order.getUpdateTime());
                update.setPayInfo(order.getPayInfo());
                update.setAftersaleDetail(order.getAftersaleDetail());
                update.setDeliveryProductInfo(order.getDeliveryProductInfo());

                mapper.updateById(update);
                // 更新item
                for (var item : order.getItems()) {
                    List<WeiOrderItem> taoOrderItems = itemMapper.selectList(
                            new LambdaQueryWrapper<WeiOrderItem>().eq(WeiOrderItem::getSkuId, item.getSkuId()).eq(WeiOrderItem::getOrderId,order.getOrderId())
                    );

                    if (taoOrderItems != null && taoOrderItems.size() > 0) {
                        // 更新
                        WeiOrderItem itemUpdate = new WeiOrderItem();
                        itemUpdate.setId(taoOrderItems.get(0).getId());
                        List<WeiGoodsSku> skus = goodsSkuMapper.selectList(new LambdaQueryWrapper<WeiGoodsSku>().eq(WeiGoodsSku::getSkuId, item.getSkuId()));
                        if (skus != null && !skus.isEmpty()) {
                            itemUpdate.setOGoodsId(skus.get(0).getErpGoodsId());
                            itemUpdate.setOGoodsSkuId(skus.get(0).getErpGoodsSkuId());
                        }
                        itemMapper.updateById(itemUpdate);
                    } else {
                        // 新增
                        List<WeiGoodsSku> skus = goodsSkuMapper.selectList(new LambdaQueryWrapper<WeiGoodsSku>().eq(WeiGoodsSku::getSkuId, item.getSkuId()));
                        if (skus != null && !skus.isEmpty()) {
                            item.setOGoodsId(skus.get(0).getErpGoodsId());
                            item.setOGoodsSkuId(skus.get(0).getErpGoodsSkuId());
                        }
                        item.setShopId(shopId);
                        item.setOrderId(order.getOrderId());
                        itemMapper.insert(item);
                    }
                }
                return ResultVo.error(ResultVoEnum.DataExist, "订单已经存在，更新成功");
            } else {
                // 不存在，新增

                order.setShopId(shopId);
                mapper.insert(order);
                // 添加item
                for (var item : order.getItems()) {
                    List<WeiGoodsSku> skus = goodsSkuMapper.selectList(new LambdaQueryWrapper<WeiGoodsSku>().eq(WeiGoodsSku::getSkuId, item.getSkuId()));
                    if (skus != null && !skus.isEmpty()) {
                        item.setOGoodsId(skus.get(0).getErpGoodsId());
                        item.setOGoodsSkuId(skus.get(0).getErpGoodsSkuId());
                    }
                    item.setShopId(shopId);
                    item.setOrderId(order.getOrderId());
                    itemMapper.insert(item);
                }
                return ResultVo.success();
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultVo.error(ResultVoEnum.SystemException, "系统异常：" + e.getMessage());
        }
    }


    @Override
    public WeiOrder queryDetailById(Long id) {
        WeiOrder weiOrder = mapper.selectById(id);
        if(weiOrder!=null){
            weiOrder.setItems(itemMapper.selectList(new LambdaQueryWrapper<WeiOrderItem>().eq(WeiOrderItem::getOrderId,weiOrder.getOrderId())));
        }
        return weiOrder;
    }
    @Override
    public WeiOrder queryDetailByOrderId(String orderId) {
        List<WeiOrder> weiOrders = mapper.selectList(new LambdaQueryWrapper<WeiOrder>().eq(WeiOrder::getOrderId,orderId));
        if(weiOrders!=null&&weiOrders.size()>0){
            weiOrders.get(0).setItems(itemMapper.selectList(new LambdaQueryWrapper<WeiOrderItem>().eq(WeiOrderItem::getOrderId,orderId)));
            return weiOrders.get(0);
        }else return null;
    }
}




