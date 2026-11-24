package com.xmzs.common.chat.config;

import com.xmzs.common.chat.openai.OpenAiClient;
import com.xmzs.common.chat.gemini.GeminiClientV2;
import com.xmzs.common.core.service.ConfigService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;
import com.xmzs.common.chat.openai.OpenAiStreamClient;
import com.xmzs.common.chat.openai.function.KeyRandomStrategy;
import com.xmzs.common.chat.openai.interceptor.OpenAILogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Chat客户端配置类
 *
 * @author: wangle
 * @date: 2023/5/16
 */
//@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class ChatClientConfig {

    private final ConfigService configService;

    @Getter
    private OpenAiStreamClient openAiStreamClient;

    @Getter
    private OpenAiClient openAiClient;

//    @Getter
//    private GeminiClient geminiClient;


    @Bean
    @Scope("singleton")
    public OpenAiStreamClient openAiStreamClient() {
        openAiStreamClient = createOpenAiStreamClient();
        return openAiStreamClient;
    }

    @Bean
    @Scope("singleton")
    public OpenAiClient openAiClient() {
        openAiClient = createOpenAiClient();
        return openAiClient;
    }

//    @Bean
//    @Scope("singleton")
//    public GeminiClient geminiClient() {
//        geminiClient = createGeminiClient();
//        return geminiClient;
//    }

    @Bean
    @Scope("singleton")
    public GeminiClientV2 geminiClientV2() {
        return createGeminiClientV2();
    }


    @Scheduled(fixedDelay = 10000)  // 每10秒检查一次
    public void refreshConfig() {
//        System.out.println("配置检查...");
        openAiStreamClient = createOpenAiStreamClient();
        openAiClient = createOpenAiClient();
//        geminiClient = createGeminiClient();
    }



    private OpenAiStreamClient createOpenAiStreamClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        Boolean vpnEnabled = Boolean.valueOf(configService.getConfigValue("proxy", "vpnEnabled"));
        OkHttpClient okHttpClient = null;
        List<Protocol> protocols = new ArrayList<>();
        protocols.add(Protocol.HTTP_1_1);
        if(vpnEnabled){
            String proxyHost = configService.getConfigValue("proxy", "host");
            Integer proxyPort = Integer.valueOf(configService.getConfigValue("proxy", "port"));
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost,proxyPort ));

             okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .proxy(proxy)
                 //hzb  改
//                 .protocols(protocols)
                .connectTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)

                .readTimeout(600, TimeUnit.SECONDS)
                .build();
        }else{
             okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
//hzb  改
//                 .protocols(protocols)
                .readTimeout(600, TimeUnit.SECONDS)
                .build();
        }

//        proxy
        return OpenAiStreamClient.builder()
            .apiHost(configService.getConfigValue("chat", "apiHost"))
            .apiKey(Arrays.asList(configService.getConfigValue("chat", "apiKey").split(",")))
            .keyStrategy(new KeyRandomStrategy())
            .okHttpClient(okHttpClient)
            .build();
    }



    private OpenAiClient createOpenAiClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        Boolean vpnEnabled = Boolean.valueOf(configService.getConfigValue("proxy", "vpnEnabled"));
        OkHttpClient okHttpClient = null;
        List<Protocol> protocols = new ArrayList<>();
        protocols.add(Protocol.HTTP_1_1);
        if(vpnEnabled){
            String proxyHost = configService.getConfigValue("proxy", "host");
            Integer proxyPort = Integer.valueOf(configService.getConfigValue("proxy", "port"));
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost,proxyPort ));
            okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .proxy(proxy)
                //hzb  改
//                .protocols(protocols)
                .connectTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)

                .readTimeout(600, TimeUnit.SECONDS)
                .build();
        }else{
            okHttpClient = new OkHttpClient.Builder()
                //hzb  改
//                .protocols(protocols)
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)

                .readTimeout(600, TimeUnit.SECONDS)
                .build();
        }

//        proxy
        return OpenAiClient.builder()
            .apiHost(configService.getConfigValue("chat", "apiHost"))
            .apiKey(Arrays.asList(configService.getConfigValue("chat", "apiKey").split(",")))
            .keyStrategy(new KeyRandomStrategy())
            .okHttpClient(okHttpClient)

            .build();
    }

    /**
     * 创建 Gemini 客户端
     */
//    private GeminiClient createGeminiClient() {
//        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
//        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
//
//        Boolean vpnEnabled = Boolean.valueOf(configService.getConfigValue("proxy", "vpnEnabled"));
//        OkHttpClient okHttpClient = null;
//
//        if(vpnEnabled){
//            String proxyHost = configService.getConfigValue("proxy", "host");
//            Integer proxyPort = Integer.valueOf(configService.getConfigValue("proxy", "port"));
//            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
//
//            okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(httpLoggingInterceptor)
//                .proxy(proxy)
//                .connectTimeout(600, TimeUnit.SECONDS)
//                .writeTimeout(600, TimeUnit.SECONDS)
//                .readTimeout(600, TimeUnit.SECONDS)
//                .build();
//        } else {
//            okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(httpLoggingInterceptor)
//                .connectTimeout(600, TimeUnit.SECONDS)
//                .writeTimeout(600, TimeUnit.SECONDS)
//                .readTimeout(600, TimeUnit.SECONDS)
//                .build();
//        }
//
//        // 修复配置读取：使用正确的配置类别和键名
//        String geminiHost = configService.getConfigValue("chat", "geminiHost");
//        String geminiApiKey = configService.getConfigValue("chat", "geminikey");
//
//        // 如果配置为空，使用默认值或抛出异常
//        if (geminiHost == null || geminiHost.trim().isEmpty()) {
//            geminiHost = "https://generativelanguage.googleapis.com/";
//            log.warn("Gemini API Host 配置为空，使用默认值: {}", geminiHost);
//        }
//
//        if (geminiApiKey == null || geminiApiKey.trim().isEmpty()) {
//            log.error("Gemini API Key 配置为空，请在 chat_config 表中配置 category='gemini', config_name='apiKey'");
//            throw new RuntimeException("Gemini API Key 未配置");
//        }
//
//        return GeminiClient.builder()
//            .geminiHost(geminiHost)
//            .geminikey(Arrays.asList(geminiApiKey.split(",")))
//            .keyStrategy(new KeyRandomStrategy())
//            .okHttpClient(okHttpClient)
//            .build();
//    }

    /**
     * 创建 Gemini 客户端 V2 (基于官方SDK)
     * 使用Google官方GenAI SDK创建客户端
     */
    private GeminiClientV2 createGeminiClientV2() {
        try {
            // 读取配置
            String geminiApiKey = configService.getConfigValue("chat", "geminikey");

            // 如果配置为空，抛出异常
            if (geminiApiKey == null || geminiApiKey.trim().isEmpty()) {
                log.error("Gemini API Key 配置为空，请在 chat_config 表中配置 category='chat', config_name='geminikey'");
                throw new RuntimeException("Gemini API Key 未配置");
            }

            // 如果有多个API Key，取第一个
            String apiKey = geminiApiKey.split(",")[0].trim();

            log.info("创建 GeminiClientV2（官方SDK），API Key: {}***",
                    apiKey.substring(0, Math.min(10, apiKey.length())));

            return GeminiClientV2.builder()
                .geminikey(apiKey)
                .build();

        } catch (Exception e) {
            log.error("创建 GeminiClientV2 失败", e);
            throw new RuntimeException("创建 Gemini 客户端失败: " + e.getMessage());
        }
    }
}
