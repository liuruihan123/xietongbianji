package com.xmzs.system.config.base;

import java.util.Map;

import com.xmzs.common.core.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * packageName com.xmzs.system.config.base
 *
 * @author zhuzi
 * @date 2024/11/27
 * @description 基础配置中心，后续都以次为主
 */
@RequiredArgsConstructor
@Configuration
@Slf4j
public class BaseConfig {


    private final ConfigCache configCache;




    /**
     * 根据category、configName获取配置
     *
     * @author zuleed
     * @param category 类型
     * @return String
     */
    public  String get(String category, String configName) {
       String configNameValue = configCache.get(category,configName);
        if (!StringUtils.isEmpty(configNameValue)) {
            return configNameValue;
        }
        configCache.set(category);
        String endconfigNameValue = configCache.get(category,configName);
        return endconfigNameValue;
    }


    public  Map<String,String> get(String category) {
        Map<String, String> cacheConfigMap  = configCache.getBycategory(configCache.getCacheKey());
        if (!cacheConfigMap.isEmpty()) {
            return cacheConfigMap;
        }
        configCache.set(category);
//        return configCache.getBycategory(configCache.getCacheKey());
        return configCache.getBycategory(category);
    }
    //重置某项配置
    public void reset(String category){
        configCache.set(category);
    }

//
//    /**
//     * 根据类型和名称获取配置
//     *
//     * @author fzr
//     * @param type 类型
//     * @param name 名称
//     * @return String
//     */
//    public static String get(String type, String name) {
//        String cache = ConfigCache.get(type, name);
//        if (!StringUtils.isNull(cache) && !StringUtils.isEmpty(cache)) {
//            return cache;
//        }
//
//        SystemConfigMapper model = SpringUtils.getBean(SystemConfigMapper.class);
//        SystemConfig config = model.selectOne(
//            new QueryWrapper<SystemConfig>()
//                .select("id", "type", "name", "value")
//                .eq("name", name)
//                .eq("type", type));
//
//        ConfigCache.set();
//        return config != null ? config.getValue() : null;
//    }
//
//    /**
//     * 根据类型和名称获取配置
//     *
//     * @author fzr
//     * @param type 类型
//     * @param name 名称
//     * @return String
//     */
//    public static String get(String type, String name, String defaults) {
//        String cache = ConfigCache.get(type, name);
//        if (!StringUtils.isNull(cache) && !StringUtils.isEmpty(cache)) {
//            return cache;
//        }
//
//        SystemConfigMapper model = SpringUtils.getBean(SystemConfigMapper.class);
//        SystemConfig config = model.selectOne(
//            new QueryWrapper<SystemConfig>()
//                .select("id", "type", "name", "value")
//                .eq("type", type)
//                .eq("name", name));
//
//        if (config == null) {
//            return defaults;
//        }
//
//        ConfigCache.set();
//        return config.getValue();
//    }
//
//    /**
//     * 根据类型和名称获取配置(JSON自定转Map)
//     *
//     * @author fzr
//     * @param type 类型
//     * @param name 名称
//     * @return String
//     */
//    public static Map<String, String> getMap(String type, String name) {
//        String cache = ConfigCache.get(type, name);
//        if (!StringUtils.isNull(cache) && !StringUtils.isEmpty(cache)) {
//            return MapUtils.jsonToMap(cache);
//        }
//
//        SystemConfigMapper model = SpringUtils.getBean(SystemConfigMapper.class);
//
//        SystemConfig config = model.selectOne(
//            new QueryWrapper<SystemConfig>()
//                .select("id", "type", "name", "value")
//                .eq("type", type)
//                .eq("name", name));
//
//        if (config == null) {
//            return null;
//        }
//
//        if (config.getValue().equals("") || config.getValue().equals("[]") || config.getValue().equals("{}")) {
//            return new LinkedHashMap<>();
//        }
//
//        ConfigCache.set();
//        return MapUtils.jsonToMap(config.getValue());
//    }
//
//    /**
//     * 设置配置的值
//     *
//     * @author fzr
//     * @param type 类型
//     * @param name 名称
//     * @param val 值
//     */
//    public static void set(String type, String name, String val) {
//        SystemConfigMapper model = SpringUtils.getBean(SystemConfigMapper.class);
//        SystemConfig config = model.selectOne(
//            new QueryWrapper<SystemConfig>()
//                .eq("type", type)
//                .eq("name", name));
//
//        if (config != null) {
//            config.setValue(val);
//            config.setUpdateTime(System.currentTimeMillis() / 1000);
//            model.updateById(config);
//        } else {
//            SystemConfig systemConfig = new SystemConfig();
//            systemConfig.setType(type);
//            systemConfig.setName(name);
//            systemConfig.setValue(val);
//            systemConfig.setCreateTime(System.currentTimeMillis() / 1000);
//            systemConfig.setUpdateTime(System.currentTimeMillis() / 1000);
//            model.insert(systemConfig);
//        }
//
//        ConfigCache.set();
//    }



}
