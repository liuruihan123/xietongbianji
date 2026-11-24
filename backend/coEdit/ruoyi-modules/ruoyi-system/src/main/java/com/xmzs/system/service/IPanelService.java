package com.xmzs.system.service;

import java.util.Map;

/**
 * packageName com.xmzs.system.service
 *
 * @author zhuzi
 * @date 2024/12/16
 * @description TODO
 */
public interface IPanelService {
    /**
     * 获取顶部卡片图片所需数据
     *
     * @param type      today, week, month, year 或 free（代表根据 beginTime、endTime 计算时间）
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 图表所需数据
     */
    public Map<String, Object> getHeaderCountInfo(String type, String beginTime, String endTime);
}
