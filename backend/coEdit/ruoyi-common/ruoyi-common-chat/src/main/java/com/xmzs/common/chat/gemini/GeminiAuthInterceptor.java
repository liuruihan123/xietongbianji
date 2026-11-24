package com.xmzs.common.chat.gemini;

import com.xmzs.common.chat.openai.function.KeyStrategyFunction;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * Gemini API 认证拦截器
 * 负责为请求添加 API Key 认证
 * 
 * @author xmzs
 */
@Slf4j
public class GeminiAuthInterceptor implements Interceptor {

    private final List<String> apiKeys;
    private final KeyStrategyFunction<List<String>, String> keyStrategy;

    public GeminiAuthInterceptor(List<String> apiKeys, KeyStrategyFunction<List<String>, String> keyStrategy) {
        this.apiKeys = apiKeys;
        this.keyStrategy = keyStrategy;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // 获取 API Key
        String apiKey = keyStrategy.apply(apiKeys);
        
        // 为 URL 添加 key 参数
        String originalUrl = originalRequest.url().toString();
        String separator = originalUrl.contains("?") ? "&" : "?";
        String newUrl = originalUrl + separator + "key=" + apiKey;
        
        // 构建新的请求
        Request newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build();
        
        log.debug("Gemini API 请求: {}", newUrl);
        
        return chain.proceed(newRequest);
    }
}
