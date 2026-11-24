package com.xmzs.common.chat.plugin.weather;

import com.xmzs.common.chat.plugin.PluginParam;
import lombok.Data;

@Data
public class WeathReq extends PluginParam {


    //城市名称
    private String cityName;

    //语言
    private String language;


    //数量
    private Integer  count;


}
