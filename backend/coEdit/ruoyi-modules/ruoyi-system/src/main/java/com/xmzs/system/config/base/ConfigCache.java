package com.xmzs.system.config.base;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xmzs.common.core.utils.MapUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.redis.utils.CacheUtils;
import com.xmzs.system.domain.ChatConfig;
import com.xmzs.system.domain.bo.ChatConfigBo;
import com.xmzs.system.domain.vo.ChatConfigVo;
import com.xmzs.system.mapper.ChatConfigMapper;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置缓存
 * zuleed++ 重新定义全局配置 放置缓存，后续会组件将配置从缓存中获取
 */
@Data
@Component
public class ConfigCache {

    private  final String cacheKey = "intv_config";

    private final ChatConfigMapper chatConfigMapper;

    public ConfigCache(ChatConfigMapper chatConfigMapper) {
        this.chatConfigMapper = chatConfigMapper;
    }

    public String getCacheKey(){
        return cacheKey;
    }
    /**
     * 设置缓存配置
     * @param category
     *    如果为空则刷新所有配置 不为空则刷新某项配置目录
     */
    public  void set(String category) {
        ChatConfigBo chatConfigBo = new ChatConfigBo();
        if(!StringUtils.isEmpty(category)){
            chatConfigBo.setCategory(category);
        }
        List<ChatConfigVo> configs = queryList(chatConfigBo);

        List<String> categoryList = configs.stream().map(ChatConfigVo::getCategory)
            .collect(Collectors.toList())
            .stream().distinct()
            .collect(Collectors.toList());

        Map<String, Object> subMap = new LinkedHashMap<>();
        for (String item : categoryList) {
            subMap.clear();
            for (ChatConfigVo configItem : configs) {
                if (configItem.getCategory().equals(item)) {
                    subMap.put(configItem.getConfigName(), configItem.getConfigValue());
                }
            }
//            map.put(typeItem, JSON.toJSONString(subMap));
            CacheUtils.put("intv_config", item, JSON.toJSONString(subMap));
        }


    }






    /**
     * 获取配置缓存 返回字符串
     *
     * @param category 类型
     * @param configName 名称
     * @return String
     */
    public  String get(String category, String configName) {


        Object configs = CacheUtils.get(cacheKey,category);

        if (!ObjectUtil.isNull(configs) && !StringUtils.isEmpty(configs.toString())) {
            Map<String, String> configCategoryMap = MapUtils.jsonToMap(configs.toString());
            return configCategoryMap.get(configName);
        }
        return null;
    }

    public static void main(String[] args) {

        String s = "{\"appId\":\"wxacaeb9b4f0db879f\",\"appSecret\":\"4e95a9076a37f854e9a83dc707b24469\"}";
        Map<String, String> configCategoryMap =  MapUtils.jsonToMap(s.toString());
        System.out.println(configCategoryMap);
    }
    /**
     * 根据category获取配置缓存 返回map
     *
     * @param category 类型
     * @return Map
     */
    public  Map<String, String> getBycategory(String category) {
        // 读取缓存
        Object configs = CacheUtils.get(cacheKey,category);
        if (!ObjectUtil.isNull(configs)) {
            Map<String, String> cacheConfigMap = MapUtils.jsonToMap(configs.toString());
            return  cacheConfigMap;
        }else{
            return new HashMap<>();
        }

    }



    /**
     * 清除缓存
     */
    public  void clear() {
//        RedisUtils.del(cacheKey);
        CacheUtils.clear(cacheKey);
    }


    public List<ChatConfigVo> queryList(ChatConfigBo bo) {
        LambdaQueryWrapper<ChatConfig> lqw = buildQueryWrapper(bo);
        return chatConfigMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ChatConfig> buildQueryWrapper(ChatConfigBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ChatConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getDelFlag()), ChatConfig::getDelFlag, "0");
        lqw.eq(StringUtils.isNotBlank(bo.getCategory()), ChatConfig::getCategory, bo.getCategory());
//        lqw.eq(bo.getBusType() != null, ChatConfig::getBusType, bo.getBusType());
        lqw.eq(StringUtils.isNotBlank(bo.getConfigName()), ChatConfig::getConfigName, bo.getConfigName());
        return lqw;
    }


}
