package com.xmzs.common.chat.plugin.news;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.xmzs.common.chat.config.NewsConfig;
import com.xmzs.common.chat.plugin.PluginAbstract;
import com.xmzs.common.chat.plugin.news.newsapilib.HttpClientProxy;
import okhttp3.OkHttpClient;

import java.util.ArrayList;
import java.util.List;


/**
 * 插件目前使用 https://newsapi.org/
 * @author zhuzi
 */

public class NewsPlugin extends PluginAbstract<NewsReq, NewsResp> {
    private final OkHttpClient client = new OkHttpClient();


    public NewsPlugin(Class<?> r) {
        super(r);
    }

    @Override
    public NewsResp func(NewsReq args)  {
        String query = "%22"+args.getQuery()+"%22";
        query = query.replace(" ","%20");
//        String from = args.getFrom();
        String sortBy = args.getSortBy()==null?"published":args.getSortBy();
        Integer pageSize = args.getPageSize()==null?10:args.getPageSize();
        NewsResp newsResp = new NewsResp();
//        NewsApiClient newsApiClient = new NewsApiClient(NEWS_KEY);
        //获取新闻 加不了vpn
//        newsApiClient.getEverything(
//            new EverythingRequest.Builder()
//                .q(query)
////                .from(from)
//                .sortBy(sortBy)
//                .pageSize(pageSize)
//                .build(),
//            new NewsApiClient.ArticlesResponseCallback() {
//                @Override
//                public void onSuccess(ArticleResponse response) {
//                    System.out.println(response.getArticles().get(0).getTitle());
//                    Integer totalResults =  response.getTotalResults();
//                    if(totalResults==0){
//                        newsResp.setNewsList(new ArrayList<NewsResp.newEn>());
//                    }else{
//                        List<Article> articles = response.getArticles();
//                        NewsResp.newEn news = null;
//                        List<NewsResp.newEn> newsList =   new ArrayList<NewsResp.newEn>();
//                        for(Article li:articles){
//                            news = new NewsResp.newEn();
//                            news.setTitle(li.getTitle());
//                            news.setDescription(li.getDescription());
//                            news.setNewsUrl(li.getUrl());
//                            news.setPublishedAt(li.getPublishedAt());
//                            newsList.add(news);
//                        }
//                        newsResp.setNewsList(newsList);
//                    }
//
//                }
//                @Override
//                public void onFailure(Throwable throwable) {
//                    System.out.println("新闻获取出错");
//                    System.out.println(throwable.getMessage());
//                }
//            }
//        );
        String url = NewsConfig.getNews_host()+"/v2/everything?q="+query+
            "&sortBy=published"+
            "&pageSize=10"+
            "&apiKey="+NewsConfig.getNews_key();

        String res = new HttpClientProxy().httpGet(url);
        //处理res
        JSONObject result = new JSONObject(res);
        Integer totalResults = result.getInt("totalResults");

        JSONArray array = new JSONArray(result.getJSONArray("articles"));

        if(totalResults==null||totalResults==0){
            newsResp.setNewsList(new ArrayList<NewsResp.newEn>());
        }else{
            NewsResp.newEn news = null;
            List<NewsResp.newEn> newsList =   new ArrayList<NewsResp.newEn>();
            for(Object li:array){
                JSONObject js = new JSONObject(li);
                news = new NewsResp.newEn();
                news.setTitle(js.getStr("title"));
                news.setDescription(js.getStr("description"));
                news.setNewsUrl(js.getStr("url"));
                news.setPublishedAt(js.getStr("publishedAt"));
                newsList.add(news);
            }
            newsResp.setNewsList(newsList);
        }




        // 创建请求对象
//        Request request = new Request.Builder()
//            .url(url)
//            .build();
//        ResponseBody response = null;
//        try {
//            response =  client.newCall(request).execute().body();
//            String res2 = response.toString();
//
//        } catch (IOException e) {
//            throw new BaseException("失败！{}",e.getMessage());
//        }

        return newsResp;
    }

    @Override
    public String content(NewsResp NewsResp) {
        List<NewsResp.newEn> newsList =NewsResp.getNewsList();
        String newsRes = "";
        int j=1;
        for(NewsResp.newEn news:newsList){
            newsRes+= j+"."+news.getTitle()+","+news.getDescription()+"\n\n";
            j++;
        }
        System.out.println("新闻数据：："+newsRes);
        return newsRes;
    }
}
