package com.unfbx.chatgpt;

import com.xmzs.common.chat.entity.chat.ChatCompletion;
import com.xmzs.common.chat.entity.chat.ChatCompletionResponse;
import com.xmzs.common.chat.entity.chat.Message;
import com.xmzs.common.chat.entity.common.DeleteResponse;
import com.xmzs.common.chat.entity.files.UploadFileResponse;
import com.xmzs.common.chat.entity.fineTune.Event;
import com.xmzs.common.chat.entity.fineTune.FineTune;
import com.xmzs.common.chat.entity.fineTune.FineTuneDeleteResponse;
import com.xmzs.common.chat.entity.fineTune.FineTuneResponse;
import com.xmzs.common.chat.entity.fineTune.job.FineTuneJobEvent;
import com.xmzs.common.chat.entity.fineTune.job.FineTuneJobListResponse;
import com.xmzs.common.chat.entity.fineTune.job.FineTuneJobResponse;
import com.xmzs.common.chat.entity.models.Model;
import com.xmzs.common.chat.entity.moderations.ModerationResponse;
import com.xmzs.common.chat.openai.OpenAiClient;
import com.xmzs.common.chat.openai.OpenAiStreamClient;
import com.xmzs.common.chat.openai.function.KeyRandomStrategy;
import com.xmzs.common.chat.openai.interceptor.DynamicKeyOpenAiAuthInterceptor;
import com.xmzs.common.chat.openai.interceptor.OpenAILogger;
import com.xmzs.common.chat.openai.interceptor.OpenAiResponseInterceptor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 包含微调测试的类
 *
 * @author zhuzi
 * @date 2024/6/29
 * @description TODO
 */
@Slf4j
public class FineTuneTest {
    private OpenAiClient client;
    private OpenAiStreamClient streamClient;


    private OpenAiClient v2;


    @Before
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
            .apiKey(Arrays.asList("sk-proj-YbdeOzyTJ22ge4OA4adIc1a"))
            //自定义key的获取策略：默认KeyRandomStrategy
            //.keyStrategy(new KeyRandomStrategy())
            .keyStrategy(new FirstKeyStrategy())
            .okHttpClient(okHttpClient)
            //自己做了代理就传代理地址，没有可不不传,(关注公众号回复：openai ，获取免费的测试代理地址)
            .apiHost("https://api.openai.com/")
            .build();

        streamClient = OpenAiStreamClient.builder()
            //支持多key传入，请求时候随机选择
            .apiKey(Arrays.asList("sk-proj-Y22ge4OA4adIc1a"))
            //自定义key的获取策略：默认KeyRandomStrategy
            .keyStrategy(new KeyRandomStrategy())
            .authInterceptor(new DynamicKeyOpenAiAuthInterceptor())
            .okHttpClient(okHttpClient)
            //自己做了代理就传代理地址，没有可不不传,(关注公众号回复：openai ，获取免费的测试代理地址)
            .apiHost("https://api.openai.com/")
            .build();
    }

    @Before
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
            .apiKey(Arrays.asList("sk-proj-YbdeOOCe4OA4adIc1a"))
            //自定义key的获取策略：默认KeyRandomStrategy
            //.keyStrategy(new KeyRandomStrategy())
            .keyStrategy(new FirstKeyStrategy())
            .okHttpClient(okHttpClient)
            //自己做了代理就传代理地址，没有可不不传,(关注公众号回复：openai ，获取免费的测试代理地址)
            .apiHost("https://api.openai.com/")
            .build();
    }
    @Test
    public void chat() {
        //聊天模型：gpt-3.5
        Message message = Message.builder().role(Message.Role.USER).content("你好啊我的伙伴！").build();
        ChatCompletion chatCompletion = ChatCompletion
            .builder()
            .messages(Collections.singletonList(message))
            .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
            .build();
        ChatCompletionResponse chatCompletionResponse = v2.chatCompletion(chatCompletion);
        chatCompletionResponse.getChoices().forEach(e -> {
            System.out.println(e.getMessage());
        });
    }
    //第一步 上传文件
    @Test
    public void uploadFile() {
        UploadFileResponse uploadFileResponse = client.uploadFile(new java.io.File("E:\\my_project\\huyifeng\\Interview_app\\int2.jsonl"));
        //file id = file-6KaBdtVlaassk9Y2P5ZjTqIC
        //ftjob-eBYBlcF1ZutjEZrT5oSKsmvO
        //file-KaNQn5V9YHlLqVQzo8CUMdIr
        System.out.println(uploadFileResponse.getId());
    }
    @Test
    public void uploadFileV1() {
        UploadFileResponse uploadFileResponse = v2.uploadFile(new java.io.File("C:\\Users\\***\\Desktop\\2.txt"));
        System.out.println(uploadFileResponse);
    }
    @Test
    public void uploadFileV2() {
        UploadFileResponse uploadFileResponse = v2.uploadFile("fine-tune", new java.io.File("C:\\Users\\***\\Desktop\\2.txt"));
        System.out.println(uploadFileResponse);
    }

    /**
     * 第二步
     * 创建微调job
     */
    @Test
    public void fineTuneJob() {
//        FineTuneJobResponse fineTuneJobResponse = client.fineTuneJob("file-V2b7dsihkRwu1kSVUoe0CqRd");
        FineTuneJobResponse fineTuneJobResponse = client.fineTuneJob("file-l7fpGjfR3cmAgMH6xtFuBItq","gpt-3.5-turbo-1106");
        System.out.println(fineTuneJobResponse.toString());
        //job id = ftjob-5WQr0bZ7grvjnY3Or2sqiixl
    }
    /**
     * 查询 微调job集合 列表
     *
     * @param after 上一个分页请求中最后一个job id，默认值：null
     * @param limit 每次查询数量 默认值：20
     * @return FineTuneJobListResponse #FineTuneResponse
     * @since 1.1.2
     */
    @Test
    public void fineTuneJobs() {
//        FineTuneJobListResponse<FineTuneJobResponse> jobListResponse = client.fineTuneJobs("ftjob-dV26ZQ9STScxPumNJavGo46kt", 5);
        FineTuneJobListResponse<FineTuneJobResponse> jobListResponse = client.fineTuneJobs(null, 20);
        System.out.println(jobListResponse);
    }
    /** 第三步验证  - 检索FineTune作业**/
    @Test
    public void retrieveFineTuneJob() {
        FineTuneJobResponse fineTuneJobResponse = client.retrieveFineTuneJob("ftjob-dV26ZQ9STScxPumNJavGo46k");
        System.out.println("yanzheng:"+fineTuneJobResponse);
        /**
         *失败返回：yanzheng:FineTuneJobResponse(id=ftjob-03PgcUGjrxIohlhX0oswEAVm, createdAt=1723687560, error=FineTuneError(message=The job failed due to an invalid training file. Expected file to have JSONL format, where every line is a valid JSON dictionary. Line 1 is not a dictionary., param=training_file, code=invalid_training_file), fineTunedModel=null, finishedAt=null, hyperparameters=com.xmzs.common.chat.entity.fineTune.job.HyperParameters@1d1f7216, model=gpt-4o-mini-2024-07-18, object=fine_tuning.job, organizationId=org-7bdYzuqfsokIeDfXhWi4Ma22, resultFiles=[], status=failed, trainedTokens=null, trainingFile=file-y27deX6EsHSOfP9F7WzHgn7E, validationFile=null, data=null)
         *验证中返回： FineTuneJobResponse(id=ftjob-03PgcUGjrxIohlhX0oswEAVm, createdAt=1723687560, error=FineTuneError(message=null, param=null, code=null), fineTunedModel=null, finishedAt=null, hyperparameters=com.xmzs.common.chat.entity.fineTune.job.HyperParameters@254c0780, model=gpt-4o-mini-2024-07-18, object=fine_tuning.job, organizationId=org-7bdYzuqfsokIeDfXhWi4Ma22, resultFiles=[], status=validating_files, trainedTokens=null, trainingFile=file-y27deX6EsHSOfP9F7WzHgn7E, validationFile=null, data=null)
         * 成功返回（包含fineTunedModel）：yanzheng:FineTuneJobResponse(id=ftjob-dV26ZQ9STScxPumNJavGo46k, createdAt=1719770600, error=FineTuneError(message=null, param=null, code=null), fineTunedModel=ft:gpt-3.5-turbo-1106:personal::9ftGiglU, finishedAt=1719770927, hyperparameters=com.xmzs.common.chat.entity.fineTune.job.HyperParameters@43b4fe19, model=gpt-3.5-turbo-1106, object=fine_tuning.job, organizationId=org-7bdYzuqfsokIeDfXhWi4Ma22, resultFiles=[file-zYfxZpi0RuYzkxvWFis5AGGT], status=succeeded, trainedTokens=89570, trainingFile=file-V2b7dsihkRwu1kSVUoe0CqRd, validationFile=null, data=null)
         *训练中 status running
         * FineTuneJobResponse(id=ftjob-ZB6AXeKJyOlHFHZzJn9c0gOw, createdAt=1723566477, error=FineTuneError(message=The job failed due to an invalid training file. Expected file to have JSONL format, where every line is a valid JSON dictionary. Line 1 is not a dictionary., param=training_file, code=invalid_training_file), fineTunedModel=null, finishedAt=null, hyperparameters=com.xmzs.common.chat.entity.fineTune.job.HyperParameters@1d1f7216, model=gpt-3.5-turbo-1106, object=fine_tuning.job, organizationId=org-7bdYzuqfsokIeDfXhWi4Ma22, resultFiles=[], status=failed, trainedTokens=null, trainingFile=file-l7fpGjfR3cmAgMH6xtFuBItq, validationFile=null, data=null)
         */
    }
    @Test
    public void cancelFineTuneJob() {
        FineTuneJobResponse fineTuneJobResponse = client.cancelFineTuneJob("ftjob-dV26ZQ9STScxPumNJavGo46k");
        System.out.println(fineTuneJobResponse);
    }
    @Test
    public void fineTuneJobEvents() {
        FineTuneJobListResponse<FineTuneJobEvent> listResponse = client.fineTuneJobEvents("ftjob-dV26ZQ9STScxPumNJavGo46k", null, 20);
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
    @Test
    public void fineTuneJobModelChat() {
        Message message1 = Message.builder().role(Message.Role.SYSTEM).content("You are Marketing analysts.The Coca-Cola Company is trying to boost profitability domestically by raising its prices. It’s focusing on the grocery store market, where the volume is high but the margin is low. What are the economics of raising the prices, and is this a good idea?").build();
        Message message2 = Message.builder().role(Message.Role.USER).content(" So Coke plans to increase profitability by raising prices. It wants to know if that’s a good idea.").build();
        List<Message> messages = new ArrayList<>(2);
        messages.add(message1);
        messages.add(message2);
        ChatCompletion chatCompletion = ChatCompletion
            .builder()
            .messages(messages)
            .model("ft:gpt-3.5-turbo-1106:personal::9ftGiglU")
            .build();
        ChatCompletionResponse chatCompletionResponse = client.chatCompletion(chatCompletion);
        chatCompletionResponse.getChoices().forEach(e -> {
            System.out.println(e.getMessage());
        });
    }
    @Test
    public void models() {
        List<Model> models = client.models();
        System.out.println(models);
    }
    @Test
    public void fineTune() {
        FineTuneResponse fineTuneResponse = v2.fineTune("file-V2b7dsihkRwu1kSVUoe0CqRd");
        System.out.println(fineTuneResponse);
    }
    @Test
    public void fineTuneV2() {
        FineTune fineTune = FineTune.builder()
            .trainingFile("file-V2b7dsihkRwu1kSVUoe0CqRd")
            .suffix("grttttttttt")
            .model(FineTune.Model.ADA.getName())
            .build();
        FineTuneResponse fineTuneResponse = v2.fineTune(fineTune);
        System.out.println(fineTuneResponse);
    }

    @Test
    public void fineTunes() {
        List<FineTuneResponse> fineTuneResponses = v2.fineTunes();
        System.out.println(fineTuneResponses);
    }

    @Test
    public void retrieveFineTune() {
        FineTuneResponse fineTuneResponses = v2.retrieveFineTune("ft-bU0xJzVfrgOjqoy1e9lC2oDP");
        System.out.println(fineTuneResponses);
    }


    @Test
    public void deleteFile() {
        DeleteResponse deleteResponse = v2.deleteFile("file-V2b7dsihkRwu1kSVUoe0CqRd");
        System.out.println(deleteResponse);
    }


    @Test
    public void cancelFineTune() {
        //status发生变化 pending -> cancelled
        FineTuneResponse fineTuneResponses = v2.cancelFineTune("file-V2b7dsihkRwu1kSVUoe0CqRd");
        System.out.println(fineTuneResponses);
    }

    @Test
    public void fineTuneEvents() {
        List<Event> events = v2.fineTuneEvents("file-V2b7dsihkRwu1kSVUoe0CqRd");
        System.out.println(events);
    }
    @Test
    public void deleteFineTuneModel() {
        FineTuneDeleteResponse deleteResponse = v2.deleteFineTuneModel("ada:ft-winter:grttttttttt-2023-02-17-01-29-27");
        System.out.println(deleteResponse);
    }
}
