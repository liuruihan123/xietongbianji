package com.xmzs.common.chat.plugin.weather;


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
 * 插件目前使用 https://openweathermap.org/
 * @author zhuzi
 */

public class WeathPlugin extends PluginAbstract<WeathReq, WeathResp> {
    private final OkHttpClient client = new OkHttpClient();

    private  ParamExt paramExt;

    public WeathPlugin(Class<?> r, ParamExt paramExt) {
        super(r);
        this.paramExt =paramExt;
    }

    public static void main(String[] args) {
//        System.out.println(Language.);
    }
    @Override
    public WeathResp func(WeathReq args)  {
        String cityName = null;
        String language = null;
        Integer  count = 0;
        if(!ObjectUtils.isEmpty(args)){
             cityName = args.getCityName();
             language = args.getLanguage();
              count = args.getCount();
        }


        double latitude = 0.0;
        double longitude = 0.0;
        if(!ObjectUtils.isEmpty(paramExt)){
             latitude = paramExt.getLatitude();
             longitude = paramExt.getLongitude();
        }



        //参数校准
        Language lag;
        if(StringUtils.isEmpty(language)){
            lag = Language.ENGLISH;
        }else if("zh".equals(language)){
            lag = Language.CHINESE_SIMPLIFIED;
        }else{
            lag = Language.ENGLISH;
        }
        if(count==null||count ==0){
            count = 15;
        }



        OpenWeatherMapClient client = new OpenWeatherMapClient(WeatherConfig.getWeath_key());
        WeathResp weathResp = new WeathResp();
//        Forecast forecast = null;
        List<WeatherForecast> forecastList= new ArrayList<WeatherForecast>();
        System.out.println("查询天气城市："+cityName);
        //如果包含经纬度
//        if(!ObjectUtils.isEmpty(latitude)&&latitude!=0.0){
        if(!ObjectUtils.isEmpty(cityName)&&cityName!=null){
            System.out.println("无坐标");
            final  Forecast  forecast = client
                .forecast5Day3HourStep()
                .byCityName(cityName)
                .language(lag)
                .unitSystem(UnitSystem.METRIC)
                .count(count)
                .retrieve()
                .asJava();
            forecastList = forecast.getWeatherForecasts();
        }else{
            System.out.println("有坐标");
            final  Forecast forecast = client
                .forecast5Day3HourStep()
                .byCoordinate(Coordinate.of(latitude, longitude))
                .language(lag)
                .unitSystem(UnitSystem.METRIC)
                .count(count)
                .retrieve()
                .asJava();
            forecastList = forecast.getWeatherForecasts();


        }

        String weatherdetail = "";
        if(forecastList.size()>0){
            for (WeatherForecast weatherForecast : forecastList) {
                System.out.println(weatherForecast.toString());
                weatherdetail+=weatherForecast.toString();
            }
            weathResp.setWeathDetail(weatherdetail );
        }

        return weathResp;
    }

    @Override
    public String content(WeathResp weathResp) {
        String weathDetail  =weathResp.getWeathDetail();
        System.out.println("天气数据：："+weathDetail);
        return weathDetail;
    }

    public ParamExt getParamExt() {
        return paramExt;
    }

    public void setParamExt(ParamExt paramExt) {
        this.paramExt = paramExt;
    }
}
