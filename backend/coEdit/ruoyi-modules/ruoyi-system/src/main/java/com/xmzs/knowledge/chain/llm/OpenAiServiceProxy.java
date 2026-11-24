package com.xmzs.knowledge.chain.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.service.OpenAiService;
import com.xmzs.common.chat.openai.OpenAiClient;
import com.xmzs.common.core.service.ConfigService;
import com.xmzs.common.core.utils.SpringUtils;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;

import java.time.Duration;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class OpenAiServiceProxy {

    private final ConfigService configService;

    public OpenAiService service(){
        ObjectMapper objectMapper = OpenAiService.defaultObjectMapper();
        OkHttpClient client =null;
        Boolean vpnEnabled = Boolean.valueOf(configService.getConfigValue("proxy", "vpnEnabled"));

        if(vpnEnabled){
//            String proxyHost = configService.getConfigValue("proxy", "host");
//            Integer proxyPort = Integer.valueOf(configService.getConfigValue("proxy", "port"));
//            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost,proxyPort ));
//            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
//            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
//            client = new OkHttpClient.Builder()
//                .addInterceptor(httpLoggingInterceptor)
//                .proxy(proxy)
//                //hzb  改
////                .protocols(protocols)
//                .connectTimeout(600, TimeUnit.SECONDS)
//                .writeTimeout(600, TimeUnit.SECONDS)
//                .readTimeout(600, TimeUnit.SECONDS)
//                .build();

            OpenAiClient openAiClient =(OpenAiClient) SpringUtils.context().getBean("openAiClient");
            client = openAiClient.getOkHttpClient();
        }else{
            client = OpenAiService.defaultClient(getKey("apiKey"), Duration.ZERO);
        }


        Retrofit retrofit = OpenAiService.defaultRetrofit(client, objectMapper);
        retrofit = retrofit.newBuilder().baseUrl(getKey("apiHost")).build();
        OpenAiApi openAiApi = retrofit.create(OpenAiApi.class);
        ExecutorService executorService = client.dispatcher().executorService();
        OpenAiService service = new OpenAiService(openAiApi,executorService);
        return service;
    }

    public String getKey(String key) {
        return configService.getConfigValue("chat", key);
    }
}
