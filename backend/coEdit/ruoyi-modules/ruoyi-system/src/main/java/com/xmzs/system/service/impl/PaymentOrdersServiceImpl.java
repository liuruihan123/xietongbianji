package com.xmzs.system.service.impl;

import com.xmzs.common.core.enums.PayStatusEnum;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xmzs.system.domain.SysMenu;
import com.xmzs.system.domain.SysPost;
import com.xmzs.system.service.ISysOssService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.xmzs.system.domain.bo.PaymentOrdersBo;
import com.xmzs.system.domain.vo.PaymentOrdersVo;
import com.xmzs.system.domain.PaymentOrder;
import com.xmzs.system.mapper.PaymentOrdersMapper;
import com.xmzs.system.service.IPaymentOrdersService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 支付订单Service业务层处理
 *
 * @author Lion Li
 * @date 2024-04-16
 */
@RequiredArgsConstructor
@Service
public class PaymentOrdersServiceImpl implements IPaymentOrdersService {

    private final PaymentOrdersMapper baseMapper;

    private final ISysOssService sysOssService;

    // 设置超时时间，例如 15 分钟
    private static final Duration TIMEOUT_DURATION = Duration.ofMinutes(5);

    /**
     * 每分钟执行一次，关闭15分钟之前的订单
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void closeExpiredOrders() {
        LocalDateTime timeoutTime = LocalDateTime.now().minus(TIMEOUT_DURATION);

        // 获取超时订单的二维码地址
        List<String> expiredOrderQRCodes = baseMapper.findExpiredOrderQRCodes(timeoutTime);

        // 删除二维码图片
        for (String qrcode : expiredOrderQRCodes) {
            if (qrcode != null && !qrcode.isEmpty()) {
                sysOssService.deleteFile(qrcode);
            }
        }

        baseMapper.closeExpiredOrders(timeoutTime);

    }

    /**
     * 查询支付订单
     */
    @Override
    public PaymentOrdersVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }


    /**
     * 查询支付订单列表
     */
    @Override
    public TableDataInfo<PaymentOrdersVo> queryPageList(PaymentOrdersBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PaymentOrder> lqw = buildQueryWrapper(bo);
        Page<PaymentOrdersVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询支付订单列表
     */
    @Override
    public List<PaymentOrdersVo> queryList(PaymentOrdersBo bo) {
        LambdaQueryWrapper<PaymentOrder> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<PaymentOrder> buildQueryWrapper(PaymentOrdersBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<PaymentOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getOrderNo()), PaymentOrder::getOrderNo, bo.getOrderNo());
        lqw.like(StringUtils.isNotBlank(bo.getOrderName()), PaymentOrder::getOrderName, bo.getOrderName());
        lqw.eq(bo.getAmount() != null, PaymentOrder::getAmount, bo.getAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getPaymentStatus()), PaymentOrder::getPaymentStatus, bo.getPaymentStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getPaymentMethod()), PaymentOrder::getPaymentMethod, bo.getPaymentMethod());
        lqw.eq(bo.getUserId() != null, PaymentOrder::getUserId, bo.getUserId());
        lqw.orderByDesc(PaymentOrder::getCreateTime);
        return lqw;
    }





    /**
     * 查询我的订单列表
     */
    @Override
    public List<PaymentOrdersVo> queryMyOrderList(PaymentOrdersBo bo) {
        LambdaQueryWrapper<PaymentOrder> lqw = buildMyOrderQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<PaymentOrder> buildMyOrderQueryWrapper(PaymentOrdersBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<PaymentOrder> lqw = Wrappers.lambdaQuery();

        lqw.eq(StringUtils.isNotBlank(bo.getOrderNo()), PaymentOrder::getOrderNo, bo.getOrderNo());
        lqw.like(StringUtils.isNotBlank(bo.getOrderName()), PaymentOrder::getOrderName, bo.getOrderName());
        lqw.eq(bo.getAmount() != null, PaymentOrder::getAmount, bo.getAmount());


        // 限制 PaymentStatus 只能为 '1' 或 '2'
        lqw.and(wrapper -> wrapper.eq(PaymentOrder::getPaymentStatus, String.valueOf(PayStatusEnum.BY_COMPLETE.getCode()))
            .or()
            .eq(PaymentOrder::getPaymentStatus, String.valueOf(PayStatusEnum.BY_PENDING.getCode())));

        lqw.eq(StringUtils.isNotBlank(bo.getPaymentMethod()), PaymentOrder::getPaymentMethod, bo.getPaymentMethod());
        lqw.eq(bo.getUserId() != null, PaymentOrder::getUserId, bo.getUserId());
        lqw.orderByDesc(PaymentOrder::getCreateTime);

        return lqw;
    }



    /**
     * 新增支付订单
     */
    @Override
    public Boolean insertByBo(PaymentOrdersBo bo) {
        PaymentOrder add = MapstructUtils.convert(bo, PaymentOrder.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改支付订单
     */
    @Override
    public Boolean updateByBo(PaymentOrdersBo bo) {
        PaymentOrder update = MapstructUtils.convert(bo, PaymentOrder.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(PaymentOrder entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除支付订单
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }


}
