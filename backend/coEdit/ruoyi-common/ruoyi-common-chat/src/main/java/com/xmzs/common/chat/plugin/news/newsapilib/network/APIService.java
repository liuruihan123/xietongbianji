package com.xmzs.common.chat.plugin.news.newsapilib.network;


import com.xmzs.common.chat.plugin.news.newsapilib.models.response.ArticleResponse;
import com.xmzs.common.chat.plugin.news.newsapilib.models.response.SourcesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

import java.util.Map;

/**
 * Created by Kwabena Berko on 5/7/2018.
 */

public interface APIService {
    @GET("/v2/sources")
    Call<SourcesResponse> getSources(@QueryMap Map<String, String> query);

    @GET("/v2/top-headlines")
    Call<ArticleResponse> getTopHeadlines(@QueryMap Map<String, String> query);

    @GET("/v2/everything")
    Call<ArticleResponse> getEverything( @QueryMap Map<String, String> query);
}

