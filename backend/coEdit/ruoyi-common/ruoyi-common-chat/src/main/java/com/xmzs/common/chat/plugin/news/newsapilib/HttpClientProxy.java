package com.xmzs.common.chat.plugin.news.newsapilib;


import com.xmzs.common.chat.config.VpnConfig;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

public class HttpClientProxy {

    /**
     * @param args
     */
    public static void main(String[] args) {

//        String content = httpGet();
//        System.out.println("返回内容："+content);
    }

    /**
     * java使用代理发送http请求
     * @return
     */
    public  String httpGet(String url) {
        String ip = "127.0.0.1";
        Integer port = 7890;
        String content = null;
        DefaultHttpClient httpclient = null;
        try {
            httpclient = new DefaultHttpClient();
            /** 设置代理IP **/
            if(VpnConfig.getEnabled()){
                HttpHost proxy = new HttpHost(ip, port);
                httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
            }

            HttpGet httpget = new HttpGet(url);

            httpget.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,1000*30);	//设置请求超时时间
            httpget.setHeader("Proxy-Authorization","Basic eXVsb3JlOll1bG9yZVByb3h5MjAxMzo=");
            httpget.setHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1");
            httpget.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

            HttpResponse responses = httpclient.execute(httpget);
            HttpEntity entity = responses.getEntity();
            content = EntityUtils.toString(entity);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();	//关闭连接
        }
        return content;
    }

}
