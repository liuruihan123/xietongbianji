package com.xmzs.common.chat.plugin.ConvertText;


import com.github.prominence.openweathermap.api.OpenWeatherMapClient;
import com.github.prominence.openweathermap.api.enums.Language;
import com.github.prominence.openweathermap.api.enums.UnitSystem;
import com.github.prominence.openweathermap.api.model.Coordinate;
import com.github.prominence.openweathermap.api.model.forecast.Forecast;
import com.github.prominence.openweathermap.api.model.forecast.WeatherForecast;
import com.xmzs.common.chat.config.WeatherConfig;
import com.xmzs.common.chat.entity.ParamExt;
import com.xmzs.common.chat.plugin.PluginAbstract;
import okhttp3.OkHttpClient;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * 用于转化文章为微调格式
 * @author zhuzi
 */

public class ConvertTextPlugin extends PluginAbstract<ConvertTextReq, ConvertTextResp> {
    private final OkHttpClient client = new OkHttpClient();

    private  ParamExt paramExt;

    public ConvertTextPlugin(Class<?> r, ParamExt paramExt) {
        super(r);
        this.paramExt =paramExt;
    }

    public static void main(String[] args) {
//        System.out.println(Language.);
    }
    @Override
    public ConvertTextResp func(ConvertTextReq args)  {
        String busID = null;
        String textContent = null;
        String modelCode = null;
        if(!ObjectUtils.isEmpty(args)){
            busID = args.getBusID();
            textContent = args.getTextContent();
            modelCode = args.getModelCode();
        }


        double latitude = 0.0;
        double longitude = 0.0;
        if(!ObjectUtils.isEmpty(paramExt)){
             latitude = paramExt.getLatitude();
             longitude = paramExt.getLongitude();
        }
        ConvertTextResp convertTextResp = new ConvertTextResp();



        //输出结果
        convertTextResp.setConvertText(textContent );
        return convertTextResp;
    }

    @Override
    public String content(ConvertTextResp convertTextResp) {
        String weathDetail  = convertTextResp.getConvertText();
        System.out.println("转化后数据：："+weathDetail);
        return weathDetail;
    }

    public ParamExt getParamExt() {
        return paramExt;
    }

    public void setParamExt(ParamExt paramExt) {
        this.paramExt = paramExt;
    }
}
