package com.xmzs.common.chat.openai.finetune;

import com.xmzs.common.chat.entity.chat.ChatCompletion;
import com.xmzs.common.chat.entity.chat.ChatCompletionResponse;
import com.xmzs.common.chat.entity.chat.Message;
import com.xmzs.common.chat.entity.files.UploadFileResponse;
import com.xmzs.common.chat.entity.fineTune.FineTuneResponse;
import com.xmzs.common.chat.entity.fineTune.job.FineTuneJobEvent;
import com.xmzs.common.chat.entity.fineTune.job.FineTuneJobListResponse;
import com.xmzs.common.chat.entity.fineTune.job.FineTuneJobResponse;
import com.xmzs.common.chat.entity.models.Model;
import com.xmzs.common.chat.openai.OpenAiClient;
import com.xmzs.common.chat.openai.OpenAiStreamClient;
import com.xmzs.common.chat.openai.function.KeyRandomStrategy;
import com.xmzs.common.chat.openai.interceptor.DynamicKeyOpenAiAuthInterceptor;
import com.xmzs.common.chat.openai.interceptor.OpenAILogger;
import com.xmzs.common.chat.openai.interceptor.OpenAiResponseInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.aspectj.lang.annotation.Before;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 包含微调测试的类
 *
 * @author zhuzi
 * @date 2024/6/29
 * @description TODO
 */
public class FineTuneTest {
    private OpenAiClient client;
    private OpenAiStreamClient streamClient;


    private OpenAiClient v2;


    @Before("execution(* com.xmzs.common.chat.openai.OpenAiClient.*(..))")
    public void before() {
        //可以为null
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890));
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        //！！！！千万别再生产或者测试环境打开BODY级别日志！！！！
        //！！！生产或者测试环境建议设置为这三种级别：NONE,BASIC,HEADERS,！！！
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient okHttpClient = new OkHttpClient
            .Builder()
                .proxy(proxy)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(new OpenAiResponseInterceptor())
            .connectTimeout(600, TimeUnit.SECONDS)
            .writeTimeout(600, TimeUnit.SECONDS)
            .readTimeout(600, TimeUnit.SECONDS)
            .build();
        client = OpenAiClient.builder()
            //支持多key传入，请求时候随机选择
            .apiKey(Arrays.asList("sk-proj-YbdeO4OA4adIc1a"))
            //自定义key的获取策略：默认KeyRandomStrategy
            //.keyStrategy(new KeyRandomStrategy())
            .keyStrategy(new FirstKeyStrategy())
            .okHttpClient(okHttpClient)

            //自己做了代理就传代理地址，没有可不不传,(关注公众号回复：openai ，获取免费的测试代理地址)
            .apiHost("https://api.openai.com/")
            .build();

        streamClient = OpenAiStreamClient.builder()
            //支持多key传入，请求时候随机选择
            .apiKey(Arrays.asList("sk-proj-YbJ22ge4OA4adIc1a"))
            //自定义key的获取策略：默认KeyRandomStrategy
            .keyStrategy(new KeyRandomStrategy())
            .authInterceptor(new DynamicKeyOpenAiAuthInterceptor())
            .okHttpClient(okHttpClient)
            //自己做了代理就传代理地址，没有可不不传,(关注公众号回复：openai ，获取免费的测试代理地址)
            .apiHost("https://api.openai.com/")
            .build();
    }


    public void before2() {
        //可以为null
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890));
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        //！！！！千万别再生产或者测试环境打开BODY级别日志！！！！
        //！！！生产或者测试环境建议设置为这三种级别：NONE,BASIC,HEADERS,！！！
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient okHttpClient = new OkHttpClient
            .Builder()
             .proxy(proxy)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(new OpenAiResponseInterceptor())
            .connectTimeout(600, TimeUnit.SECONDS)
            .writeTimeout(600, TimeUnit.SECONDS)
            .readTimeout(600, TimeUnit.SECONDS)
            .build();
        v2 = OpenAiClient.builder()
            //支持多key传入，请求时候随机选择
            .apiKey(Arrays.asList("sk-proj-YbdeOOz4OA4adIc1a"))
            //自定义key的获取策略：默认KeyRandomStrategy
            //.keyStrategy(new KeyRandomStrategy())
            .keyStrategy(new FirstKeyStrategy())
            .okHttpClient(okHttpClient)
            //自己做了代理就传代理地址，没有可不不传,(关注公众号回复：openai ，获取免费的测试代理地址)
            .apiHost("https://api.openai.com/")
            .build();
    }


    public void uploadFile() {
        UploadFileResponse uploadFileResponse = client.uploadFile(new java.io.File("fine_tune_test_file.json1"));
        //file id = file-6KaBdtVlaassk9Y2P5ZjTqIC
        //ftjob-eBYBlcF1ZutjEZrT5oSKsmvO
        //file-KaNQn5V9YHlLqVQzo8CUMdIr
        System.out.println(uploadFileResponse.getId());
    }
    public void uploadFileV1() {
        UploadFileResponse uploadFileResponse = v2.uploadFile(new java.io.File("C:\\Users\\***\\Desktop\\2.txt"));
        System.out.println(uploadFileResponse);
    }

    public void uploadFileV2() {
        UploadFileResponse uploadFileResponse = v2.uploadFile("fine-tune", new java.io.File("C:\\Users\\***\\Desktop\\2.txt"));
        System.out.println(uploadFileResponse);
    }

    public void fineTuneJob() {
        FineTuneJobResponse fineTuneJobResponse = client.fineTuneJob("file-KaNQn5V9YHlLqVQzo8CUMdIr","gpt-3.5-turbo-1106");
        System.out.println(fineTuneJobResponse.toString());
        //job id = ftjob-5WQr0bZ7grvjnY3Or2sqiixl
    }
    public void fineTuneJobs() {
//        FineTuneJobListResponse<FineTuneJobResponse> jobListResponse = client.fineTuneJobs("ftjob-cG7zIraBhAkq5Ybs7311lH7t", 5);
        FineTuneJobListResponse<FineTuneJobResponse> jobListResponse = client.fineTuneJobs(null, 20);
        System.out.println(jobListResponse);
    }

    public void retrieveFineTuneJob() {
        FineTuneJobResponse fineTuneJobResponse = client.retrieveFineTuneJob("ftjob-5WQr0bZ7grvjnY3Or2sqiixl");
        System.out.println(fineTuneJobResponse);
    }

    public void cancelFineTuneJob() {
        FineTuneJobResponse fineTuneJobResponse = client.cancelFineTuneJob("ftjob-cG7zIraBhAkq5Ybs7311lH7t");
        System.out.println(fineTuneJobResponse);
    }
    public void fineTuneJobEvents() {
        FineTuneJobListResponse<FineTuneJobEvent> listResponse = client.fineTuneJobEvents("ftjob-5WQr0bZ7grvjnY3Or2sqiixl", null, 20);
//        FineTuneJobListResponse<FineTuneJobEvent> listResponse = client.fineTuneJobEvents("ftjob-5WQr0bZ7grvjnY3Or2sqiixl", "ftevent-WwB8lpWxhjgUJX9DYdb47zJe", 20);
        listResponse.getData().forEach(e -> System.out.println(e.getMessage()));
        /**
         * The job has successfully completed
         * New fine-tuned model created: ft:gpt-3.5-turbo-1106:personal::8K5KwJTU
         * Step 91/100: training loss=0.45
         * Step 81/100: training loss=0.00
         * Step 71/100: training loss=0.00
         * Step 61/100: training loss=0.94
         * Step 51/100: training loss=0.19
         * Step 41/100: training loss=0.06
         * Step 31/100: training loss=0.95
         * Step 21/100: training loss=1.99
         * Step 11/100: training loss=2.50
         * Step 1/100: training loss=5.42
         * Fine-tuning job started
         * Files validated, moving job to queued state
         * Validating training file: file-KaNQn5V9YHlLqVQzo8CUMdIr
         * Created fine-tuning job: ftjob-5WQr0bZ7grvjnY3Or2sqiixl
         *
         * Process finished with exit code 0
         */
    }

    public void fineTuneJobModelChat() {
        Message message1 = Message.builder().role(Message.Role.SYSTEM).content("OnBot是一个聊天机器人。").build();
        Message message2 = Message.builder().role(Message.Role.USER).content("OnBot请问：Chatgpt-java的作者是谁？").build();
        List<Message> messages = new ArrayList<>(2);
        messages.add(message1);
        messages.add(message2);
        ChatCompletion chatCompletion = ChatCompletion
            .builder()
            .messages(messages)
            .model("ft:gpt-3.5-turbo-1106:personal::8K5KwJTU")
            .build();
        ChatCompletionResponse chatCompletionResponse = client.chatCompletion(chatCompletion);
        chatCompletionResponse.getChoices().forEach(e -> {
            System.out.println(e.getMessage());
            //返回值：Message(content=作者是Unfbx，个人网站：https://www.unfbx.com)
        });
    }

    public void models() {
        List<Model> models = client.models();
        System.out.println(models);
    }

    public void fineTune() {
        FineTuneResponse fineTuneResponse = v2.fineTune("file-EHB0Wp3wcZu6tpbwkB6xeiEd");
        System.out.println(fineTuneResponse);
    }


}
