package com.xmzs.common.chat.plugin.ConvertText;

import com.xmzs.common.chat.plugin.PluginParam;
import lombok.Data;

@Data
public class ConvertTextReq extends PluginParam {


    //业务ID
    private String busID;

    //原文内容
    private String textContent;


    //使用模型
    private String  modelCode;


}
