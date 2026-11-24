package com.xmzs.common.chat.plugin.news.newsapilib.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Kwabena Berko on 5/7/2018.
 */

public class APIClient {
    private static Retrofit mRetrofit = null;

    private static Retrofit getRetrofit(){
        if(mRetrofit == null){
            System.setProperty("http.proxyHost", "127.0.0.1");
            System.setProperty("http.proxyPort", "7890");
            // 对https也开启代理
            System.setProperty("https.proxyHost", "127.0.0.1");
            System.setProperty("https.proxyPort", "7890");
            mRetrofit = new Retrofit.Builder()
                    .baseUrl("https://newsapi.org/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return mRetrofit;
    }

    public static APIService getAPIService(){
        return getRetrofit().create(APIService.class);
    }

}

