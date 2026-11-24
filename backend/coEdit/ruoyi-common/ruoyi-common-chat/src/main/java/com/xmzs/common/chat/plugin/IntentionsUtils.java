package com.xmzs.common.chat.plugin;

import com.xmzs.common.chat.entity.chat.PluginCode;

import java.util.HashMap;
import java.util.Map;

/**
 * packageName com.xmzs.common.chat.plugin
 *用于识别意图
 * @author zhuzi
 * @date 2024/4/24
 * @description TODO
 */
public class IntentionsUtils {

    /**
     * 用于判断是否可能是新闻意图
     * @param prompts
     * @return
     */
    public Map<String,Object> pluginCode(String prompts){
        Map<String,Object> map = new HashMap<String,Object>();
        //新闻意图
        if(prompts.contains("新闻")||prompts.contains("搜索")||prompts.contains("动态")||prompts.contains("发生了什么")||prompts.contains("报道")
           ||prompts.contains("新聞")||prompts.contains("蒐索")||prompts.contains("動態")||prompts.contains("發生了什麼")||prompts.contains("報導")
        ||prompts.contains("news")||prompts.contains("search")||prompts.contains("latest developments")||prompts.contains("report")||prompts.contains("coverage")
        ){
            System.out.println("新闻意图");
            map.put("code", PluginCode.NEWS);
        }else if(prompts.contains("冷不冷")||prompts.contains("气温")||prompts.contains("天气")||prompts.contains("气候")||prompts.contains("多云")||prompts.contains("风大")||prompts.contains("有雨")||prompts.contains("带伞")||prompts.contains("降雨")||prompts.contains("热吗")||prompts.contains("热不热")
        ||prompts.contains("氣溫")||prompts.contains("天氣")||prompts.contains("多雲")||prompts.contains("風大")||prompts.contains("熱嗎")||prompts.contains("熱不熱")||prompts.contains("帶傘")
            ||prompts.contains("weather")||prompts.contains("temperature")||prompts.contains("rain")||prompts.contains("forecast")||prompts.contains("cold")||prompts.contains("sunny ")
        ){
            System.out.println("天气意图");
            map.put("code", PluginCode.WEATHER);
        }else{
            System.out.println("聊天意图");
            map.put("code",PluginCode.CHAT);
        }
        return map;
    }
}
