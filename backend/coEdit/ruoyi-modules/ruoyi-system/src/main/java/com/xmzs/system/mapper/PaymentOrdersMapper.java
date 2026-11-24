package com.xmzs.system.mapper;

import com.xmzs.system.domain.PaymentOrder;
import com.xmzs.system.domain.vo.PaymentOrdersVo;
import com.xmzs.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 支付订单Mapper接口
 *
 * @author Lion Li
 * @date 2024-04-16
 */
public interface PaymentOrdersMapper extends BaseMapperPlus<PaymentOrder, PaymentOrdersVo> {

    @Update("UPDATE sys_pay_order SET payment_status = '3' ,pay_qrcode=''  WHERE payment_status = '1' AND create_time < #{timeoutTime}")
    void closeExpiredOrders(@Param("timeoutTime") LocalDateTime timeoutTime);

    @Select("SELECT pay_qrcode FROM sys_pay_order WHERE  payment_status = '1' AND  create_time < #{timeoutTime}")
    List<String> findExpiredOrderQRCodes(@Param("timeoutTime") LocalDateTime timeoutTime);




    /**
     * 计算人民币
     * @param beginTime
     * @param endTime
     * @return
     */
    @Select("SELECT SUM(amount) FROM sys_pay_order WHERE  payment_status ='2' AND create_time >= STR_TO_DATE(#{beginTime}, '%Y-%m-%d %H:%i:%s') AND create_time <= STR_TO_DATE(#{endTime}, '%Y-%m-%d %H:%i:%s') AND payment_method != 'stripePay'")
    Double sumRmbAmount(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /**
     * 计算USD
     * @param beginTime
     * @param endTime
     * @return
     */
    @Select("SELECT SUM(amount) FROM sys_pay_order WHERE  payment_status ='2' AND create_time >= STR_TO_DATE(#{beginTime}, '%Y-%m-%d %H:%i:%s') AND create_time <= STR_TO_DATE(#{endTime}, '%Y-%m-%d %H:%i:%s') AND payment_method = 'stripePay'")
    Double sumUsdAmount(@Param("beginTime") String beginTime, @Param("endTime") String endTime);


    /**
     * 获取人民币最近15天的金额数据
     * @return
     */
    @Select("SELECT DATE(create_time) as date, SUM(amount) as total_amount " +
        "FROM sys_pay_order " +
        "WHERE payment_status ='2' AND payment_method !='stripePay' AND create_time >= CURDATE() - INTERVAL #{days} DAY " +
        "GROUP BY DATE(create_time) " +
        "ORDER BY DATE(create_time)")
    List<Map<String, Object>> getRMBLast15DaysAmount(@Param("days") int days);



    /**
     * 获取美元最近15天的金额数据
     * @return
     */
    @Select("SELECT DATE(create_time) as date, SUM(amount) as total_amount " +
        "FROM sys_pay_order " +
        "WHERE payment_status ='2' AND payment_method ='stripePay' AND create_time >= CURDATE() - INTERVAL #{days} DAY " +
        "GROUP BY DATE(create_time) " +
        "ORDER BY DATE(create_time)")
    List<Map<String, Object>> getUSDLast15DaysAmount( @Param("days") int days);


}
