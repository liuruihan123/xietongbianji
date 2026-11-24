package com.xmzs.system.service.impl;

import com.xmzs.system.domain.UserAmount;
import com.xmzs.system.mapper.ChatMessageMapper;
import com.xmzs.system.mapper.PaymentOrdersMapper;
import com.xmzs.system.mapper.SysUserMapper;
import com.xmzs.system.service.*;
import lombok.RequiredArgsConstructor;
import net.mamoe.mirai.internal.deps.io.ktor.http.parsing.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * packageName com.xmzs.system.service.impl
 *
 * @author zhuzi
 * @date 2024/12/16
 * @description TODO
 */
@RequiredArgsConstructor
@Service
public class PanelServiceImpl implements IPanelService {
    private final IPaymentOrdersService ordersService;
    private final ISysUserService userService;
    private final IChatMessageService chatMessageService;
    private final IChatTokenService chatTokenService;

    private final PaymentOrdersMapper paymentOrdersMapper;
    private final SysUserMapper sysUserMapper;
    private final ChatMessageMapper messageMapper;


    /**
     * 获取顶部卡片图片所需数据
     *
     * @param type      today, week, month, year 或 free（代表根据 beginTime、endTime 计算时间）
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 图表所需数据
     */
    public  Map<String, Object> getHeaderCountInfo(String type, String beginTime, String endTime) {

            Map<String,String> selTimeMap =  getDateTimeHandler(type,beginTime,endTime);
            beginTime = selTimeMap.get("beginTime");
            endTime = selTimeMap.get("endTime");
            //今日
            Map<String,String> todayTimeMap =  getDateTimeHandler("today",null,null);
            String todaybeginTime = todayTimeMap.get("beginTime");
            String todayendTime = todayTimeMap.get("endTime");

            //-----------总销售额卡片--------------
          //所选时间内
            //人民币
            Double totalrmb =  paymentOrdersMapper.sumRmbAmount(beginTime,endTime);
            //美元
            Double totalusd =  paymentOrdersMapper.sumUsdAmount(beginTime,endTime);

             Double todayrmb =  paymentOrdersMapper.sumRmbAmount(todaybeginTime,todayendTime);
             Double todayusd =  paymentOrdersMapper.sumUsdAmount(todaybeginTime,todayendTime);
             //minichart数据 （15天）
             List<Map<String, Object>> daysMap = getLastNDaysAmount(15);


            //----------会员数-----------------

            //注册用户数
            Integer userCount =  sysUserMapper.userCount(beginTime,endTime);
            //付费会员
            Integer gradeCount =  sysUserMapper.gradeCount(beginTime,endTime);

            //今日
            //注册用户数
            Integer toDayuserCount =  sysUserMapper.userCount(todaybeginTime,todayendTime);
            //付费会员
            Integer toDaygradeCount =  sysUserMapper.gradeCount(todaybeginTime,todayendTime);

            //minichart数据 （15天）
            List<Map<String, Object>> userChartData =  getLastDaysUser(15,"user");


            //----------对话数-----------------
        Integer msgCount =  messageMapper.getCount(beginTime,endTime);
        Integer todayCount =  messageMapper.getCount(todaybeginTime,todayendTime);
        List<Map<String, Object>> msgChartData =  getLastDaysUser(15,"msg");


        //----------token-----------------
        Long  tokenCount =  Optional.ofNullable(messageMapper.getTokenCount(beginTime,endTime)).orElse(0l);
        Long  todayTokenCount = Optional.ofNullable( messageMapper.getTokenCount(todaybeginTime,todayendTime)).orElse(0l);
        List<Map<String, Object>> tokenChartData =  getLastDaysUser(15,"token");


        //---------用户消费排行-----------
        List<UserAmount> userAmounts = sysUserMapper.getUserPaymentRank();
        userAmounts.forEach(userAmount -> {
            if (userAmount.getTotalAmount() == null) {
                userAmount.setTotalAmount(0.0);
            }
        });
        //最终输出
        Map<String,Object> resultMap = new HashMap<>();


        //----销售额卡片输出数据-----
        Map<String,Object> total = new HashMap<>();
        total.put("rmb",Optional.ofNullable(totalrmb).orElse(0.0));
        total.put("usd",Optional.ofNullable(totalusd).orElse(0.0));
//        total.put("rmb",totalrmb);
//        total.put("usd",totalusd);

        Map<String,Object> today = new HashMap<>();
        today.put("rmb", Optional.ofNullable(todayrmb).orElse(0.0));
        today.put("usd",Optional.ofNullable(todayusd).orElse(0.0));
//        today.put("rmb",todayrmb);
//        today.put("usd",todayusd);
        // 提取所有的 date 并转换为 String[]
        String[] dates = daysMap.stream()
            .map(map -> (String) map.get("date"))
            .toArray(String[]::new);
        Double[] rmb = daysMap.stream()
            .map(map -> (Double) map.get("rmb"))
            .toArray(Double[]::new);
        Double[] usd = daysMap.stream()
            .map(map -> (Double) map.get("usd"))
            .toArray(Double[]::new);
        Map<String,Object> chartData = new HashMap<>();
        chartData.put("dates",dates);
        chartData.put("rmb",rmb);
        chartData.put("usd",usd);

        Map<String,Object> salesMap = new HashMap<>();
        salesMap.put("total",total);
        salesMap.put("today",today);
        salesMap.put("chartData",chartData);
        resultMap.put("sales",salesMap);

        //----会员卡片输出数据-----
        Map<String,Object> members = new HashMap<>();
        Map<String,Object> userchartData = new HashMap<>();
        String[] userdates = userChartData.stream()
            .map(map -> (String) map.get("date"))
            .toArray(String[]::new);
        String[] userValues = userChartData.stream()
            .map(map -> (String) map.get("count"))
            .toArray(String[]::new);
        userchartData.put("dates",userdates);
        userchartData.put("values",userValues);

        members.put("total",Optional.ofNullable(userCount).orElse(0));
        members.put("paying",Optional.ofNullable(gradeCount).orElse(0));
        members.put("chartData",userchartData);
        resultMap.put("members",members);


        //----对话数量卡片输出数据----
        Map<String,Object> msgCountMap = new HashMap<>();
        Map<String,Object> msgchartDataMap = new HashMap<>();
        String[] msgdates = msgChartData.stream()
            .map(map -> (String) map.get("date"))
            .toArray(String[]::new);
        String[] msgValues = msgChartData.stream()
            .map(map -> (String) map.get("count"))
            .toArray(String[]::new);
        msgchartDataMap.put("dates",msgdates);
        msgchartDataMap.put("values",msgValues);

        msgCountMap.put("total",Optional.ofNullable(msgCount).orElse(0));
        msgCountMap.put("today",Optional.ofNullable(todayCount).orElse(0));
        msgCountMap.put("chartData",msgchartDataMap);

        resultMap.put("conversations",msgCountMap);


        //----Token消耗卡片输出数据----

        Map<String,Object> tokenCountMap = new HashMap<>();
        Map<String,Object> tokenchartDataMap = new HashMap<>();
        String[] tokendates = tokenChartData.stream()
            .map(map -> (String) map.get("date"))
            .toArray(String[]::new);
        String[] tokenValues = tokenChartData.stream()
            .map(map -> (String) map.get("count"))
            .toArray(String[]::new);

        tokenchartDataMap.put("dates",tokendates);
        tokenchartDataMap.put("values",tokenValues);

        tokenCountMap.put("total",Optional.ofNullable(formatNumber(tokenCount)).orElse("0"));
        tokenCountMap.put("today",Optional.ofNullable(formatNumber(todayTokenCount)).orElse("0"));
        tokenCountMap.put("chartData",tokenchartDataMap);

        resultMap.put("tokens",tokenCountMap);


        // 排行榜数据

        resultMap.put("rankList",userAmounts);

        return resultMap;
    }


    public static String formatNumber(long number) {
        if (number < 1000) {
            return String.valueOf(number);
        } else if (number < 1_000_000) {
            return String.format("%.1fk", number / 1000.0);
        } else if (number < 1_000_000_000) {
            return String.format("%.1fm", number / 1_000_000.0);
        } else {
            return String.format("%.1fb", number / 1_000_000_000.0);
        }
    }

    /**
     * 获取对应天数数据
     * @param days
     * @param type
     * @return
     */
    private List<Map<String, Object>> getLastDaysUser(int days,String type){
        List<Map<String, Object>> dataList =  new ArrayList<Map<String, Object>>();
        if("user".equals(type)){
            dataList =  sysUserMapper.getLastDaysUser( days);
        }else if("msg".equals(type)){
            dataList =  messageMapper.getLastDaysCount(days);
        }else if("token".equals(type)){
            dataList =  messageMapper.getLastDaysTokenCount(days);
        }

        // 创建一个日期列表以存储结果
        List<Map<String, Object>> resultList = new ArrayList<>();
        // 获取当前日期并计算最近 N 天的日期
        Calendar calendar = Calendar.getInstance();
        for (int i = days - 1; i >= 0; i--) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
            Date date = calendar.getTime();
            String formattedDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
            // 创建一个结果 Map
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("date", formattedDate);
            resultMap.put("count", "0");
            // 查找对应的数据
            for (Map<String, Object> map : dataList) {
                if (String.valueOf(map.get("date")).equals(formattedDate)) {
                    resultMap.put("count",Optional.ofNullable(String.valueOf( map.get("count"))).orElse("0"));
//                    resultMap.put("count",String.valueOf( map.get("count")));
                    break;
                }
            }
            // 将结果添加到列表中
            resultList.add(resultMap);
        }
        return resultList;

    }

    /**
     * 获取最近指定天数的金额数据
     *
     * @param days 最近的天数
     * @return List<Map<String, Object>> 包含日期、人民币和美元
     */
    public List<Map<String, Object>> getLastNDaysAmount(int days) {
        // 获取最近 N 天的人民币和美元金额数据
        List<Map<String, Object>> rmbData = paymentOrdersMapper.getRMBLast15DaysAmount( days);
        List<Map<String, Object>> usdData = paymentOrdersMapper.getUSDLast15DaysAmount( days);

        // 创建一个日期列表以存储结果
        List<Map<String, Object>> resultList = new ArrayList<>();

        // 获取当前日期并计算最近 N 天的日期
        Calendar calendar = Calendar.getInstance();
        for (int i = days - 1; i >= 0; i--) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
            Date date = calendar.getTime();
            String formattedDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
            // 创建一个结果 Map
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("date", formattedDate);
            resultMap.put("rmb", 0.0); // 默认人民币为0
            resultMap.put("usd", 0.0); // 默认美元为0
            // 查找对应的人民币数据
            for (Map<String, Object> rmb : rmbData) {
                System.out.println(String.valueOf(rmb.get("date")));
                System.out.println(formattedDate);
                System.out.println(String.valueOf(rmb.get("date")).equals(formattedDate));
                if (String.valueOf(rmb.get("date")).equals(formattedDate)) {
//                    resultMap.put("rmb", rmb.get("total_amount"));
                    resultMap.put("rmb", ((BigDecimal) rmb.get("total_amount")).doubleValue());
                    break;
                }
            }
            // 查找对应的美元数据
            for (Map<String, Object> usd : usdData) {
                if (String.valueOf(usd.get("date")).equals(formattedDate)) {
//                    resultMap.put("usd", usd.get("total_amount"));
                    resultMap.put("usd", ((BigDecimal) usd.get("total_amount")).doubleValue());
                    break;
                }
            }
            // 将结果添加到列表中
            resultList.add(resultMap);
        }

        return resultList;
    }



    /**
     * 时间统一计算方式
     * @param type      today, week, month, year 或 free（代表根据 beginTime、endTime 计算时间）
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 包含 beginTime 和 endTime 的 Map
     */
    private Map<String,String> getDateTimeHandler(String type, String beginTime, String endTime) {
        Map<String, String> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        try {
            Date now = new Date();
            endTime = sdf.format(now); // endTime 始终是当前时间
            // 如果不是 "free"，根据 type 计算时间范围
            if (!"free".equals(type)) {
                switch (type) {
                    case "today":
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        beginTime = sdf.format(calendar.getTime());
                        break;

                    case "week":
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                        beginTime = sdf.format(calendar.getTime());
                        break;

                    case "month":
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        beginTime = sdf.format(calendar.getTime());
                        break;

                    case "year":
                        calendar.set(Calendar.MONTH, Calendar.JANUARY);
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        beginTime = sdf.format(calendar.getTime());
                        break;

                    default:
                        throw new IllegalArgumentException("Invalid type: " + type);
                }
            } else {
                // 对于 "free" 类型，使用传入的 beginTime 和 endTime
                // 这里假设传入的 beginTime 和 endTime 是有效的
                // 对于 "free" 类型，使用传入的 beginTime 和 endTime
                // 检查并补全 beginTime 和 endTime
                beginTime = completeTime(beginTime, "00:00:00");
                endTime = completeTime(endTime, "23:59:59");
            }
            result.put("beginTime", beginTime);
            result.put("endTime", endTime);

    } catch (Exception e) {
        e.printStackTrace();
        // 处理时间解析异常
    }

        return result;
    }

    /**
     * 检查并补全时间
     *
     * @param time        输入的时间字符串
     * @param defaultTime 默认的时分秒
     * @return 完整的时间字符串
     */
    private  String completeTime(String time, String defaultTime) {
        if (time != null && !time.trim().isEmpty()) {
            // 检查时间字符串是否包含时分秒
            if (time.split(" ").length == 1) {
                // 如果没有时分秒，补全
                return time + " " + defaultTime;
            }
        }
        return time; // 如果已经包含时分秒，直接返回
    }

}
