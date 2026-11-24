package com.xmzs.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xmzs.common.chat.config.ChatClientConfig;
import com.xmzs.common.chat.domain.request.ChatRequest;
import com.xmzs.common.chat.entity.chat.ChatCompletion;
import com.xmzs.common.chat.entity.chat.ChatCompletionResponse;
import com.xmzs.common.chat.entity.chat.Message;
import com.xmzs.common.chat.entity.chat.ResponseFormat;
import com.xmzs.common.chat.entity.files.UploadFileResponse;
import com.xmzs.common.chat.entity.fineTune.job.FineTuneError;
import com.xmzs.common.chat.entity.fineTune.job.FineTuneJobResponse;
import com.xmzs.common.chat.openai.OpenAiClient;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.core.exception.base.BaseException;
import com.xmzs.common.core.utils.DateUtils;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.system.domain.FineTuneAttach;
import com.xmzs.system.domain.FineTuneInfo;
import com.xmzs.system.domain.ModelSetting;
import com.xmzs.system.domain.SysModel;
import com.xmzs.system.domain.bo.FineTuneAttachBo;
import com.xmzs.system.domain.bo.FineTuneInfoBo;
import com.xmzs.system.domain.request.FineTuneAttachRequest;
import com.xmzs.system.domain.vo.*;
import com.xmzs.system.mapper.*;
import com.xmzs.system.service.IFineTuneAttachService;
import com.xmzs.system.service.IFineTuneInfoService;
import com.xmzs.system.service.ISysOssService;
import com.xmzs.system.util.OfficeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 精调管理Service业务层处理
 *
 * @author zhuzi
 * @date 2024-07-04
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class FineTuneInfoServiceImpl extends ServiceImpl<FineTuneInfoMapper, FineTuneInfo>  implements IFineTuneInfoService  {

    private final FineTuneInfoMapper baseMapper;
    private final FineTuneAttachMapper fineTuneAttachMapper;
    private final SysModelMapper sysModelMapper;
    private final PromptsStoreMapper promptsStoreMapper;
    private final ModelSettingMapper modelSettingMapper;

//    @Autowired
//    private IService<FineTuneInfo> service;
//
//    @Autowired
//    private IService<FineTuneAttach> attachService;

    @Autowired(required = false)
    private ChatClientConfig chatConfig;
    private final ISysOssService ossService;
    private final IFineTuneAttachService fineTuneAttachService;
//    private OpenAiStreamClient openAiStreamClient;

    private OpenAiClient openAiClient;
    @Autowired
    private FineTuneInfoMapper fineTuneInfoMapper;

    /**
     * 查询精调管理
     */
    @Override
    public FineTuneInfoVo queryById(Integer id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询精调管理列表
     */
    @Override
    public TableDataInfo<FineTuneInfoVo> queryPageList(FineTuneInfoBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<FineTuneInfo> lqw = buildQueryWrapper(bo);
        Page<FineTuneInfoVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询精调管理列表
     */
    @Override
    public List<FineTuneInfoVo> queryList(FineTuneInfoBo bo) {
        LambdaQueryWrapper<FineTuneInfo> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<FineTuneInfo> buildQueryWrapper(FineTuneInfoBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<FineTuneInfo> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getFtId()), FineTuneInfo::getFtId, bo.getFtId());
        lqw.eq(StringUtils.isNotBlank(bo.getFtJobId()), FineTuneInfo::getFtJobId, bo.getFtJobId());
        lqw.eq(bo.getUid() != null, FineTuneInfo::getUid, bo.getUid());
        lqw.like(StringUtils.isNotBlank(bo.getFtName()), FineTuneInfo::getFtName, bo.getFtName());
        lqw.like(StringUtils.isNotBlank(bo.getFtModelName()), FineTuneInfo::getFtModelName, bo.getFtModelName());
        lqw.eq(StringUtils.isNotBlank(bo.getBaseModel()), FineTuneInfo::getBaseModel, bo.getBaseModel());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), FineTuneInfo::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getUseStatus()), FineTuneInfo::getUseStatus, bo.getUseStatus());
        return lqw;
    }

    /**
     * 新增精调管理
     */
    @Override
    public FineTuneInfoBo insertByBo(FineTuneInfoBo bo) {

        bo.setStatus("0");
        bo.setUseStatus("0");
        FineTuneInfo add = MapstructUtils.convert(bo, FineTuneInfo.class);
        validEntityBeforeSave(add);

        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
//        FineTuneInfo fineTuneInfo = MapstructUtils.convert(bo, FineTuneInfo.class);
        return bo;
    }

    /**
     * 修改精调管理
     */
    @Override
    public Boolean updateByBo(FineTuneInfoBo bo) {
        FineTuneInfo update = MapstructUtils.convert(bo, FineTuneInfo.class);
        validEntityBeforeSave(update);

        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(FineTuneInfo entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除精调管理
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        //同时删除相关数据
        fineTuneAttachMapper.delete(Wrappers.<FineTuneAttach>lambdaQuery().in(FineTuneAttach::getFtId, ids));
        int row = baseMapper.delete(Wrappers.<FineTuneInfo>lambdaQuery().in(FineTuneInfo::getFtId, ids));
        return row > 0;
    }


    /**
     * 获取当前登录的用户
     *
     * @return
     */
    @Override
    public LoginUser getLoginUser() {
        LoginUser sysUser = LoginHelper.getLoginUser();
        if (sysUser == null) {
            throw new BaseException("用户未登录！");
        }
        return sysUser;
    }

    /**
     * 修改使用状态
     *
     * @param id id
     * @param useStatus useStatus
     * @return 结果
     */
    @Override
    public int updateUseStatus(Integer id, String useStatus) {
        return baseMapper.update(null,
            new LambdaUpdateWrapper<FineTuneInfo>()
                .set(FineTuneInfo::getUseStatus, useStatus)
                .eq(FineTuneInfo::getId, id));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upload(FineTuneAttachRequest request) {
        storeContent(request.getFile(), request.getFtid());
    }


    /**
     * 读取内容
     * @param file
     * @param ftid
     */
    public void storeContent(MultipartFile file, String ftid) {
        SysOssVo oss = ossService.upload(file);
        SysOssUploadVo uploadVo = new SysOssUploadVo();
        uploadVo.setUrl(oss.getUrl());
        uploadVo.setFileName(oss.getOriginalName());
        uploadVo.setOssId(oss.getOssId().toString());
        InputStream inputStream = null;
        try {
             inputStream = file.getInputStream();
        } catch (IOException e) {
            log.info("获取文件流错误："+e.getMessage());
            throw new RuntimeException(e);
        }
        String fileName = file.getOriginalFilename();
        String extName = "";
        if (fileName != null && fileName.contains(".")) {
            extName = fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            extName =  ""; // 如果没有扩展名，返回空字符串或者你可以抛出一个异常
        }

        FineTuneAttach fineTuneAttach = new FineTuneAttach();
        fineTuneAttach.setFtId(ftid);
        fineTuneAttach.setFileId(oss.getUrl());
        fineTuneAttach.setStatus("0");
        fineTuneAttach.setFileName(fileName);
        //文件内容
//        File officeFile  = OfficeUtils.convertMultiPartToFile(file);
        List<String> fileContentList = new ArrayList<String>();
        try {
            fileContentList = OfficeUtils.Office2Html(inputStream,extName);
//            fileContent = OfficeUtils.readHTMLFile(filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String fc = "";
        int exchangeNum = 0;
        for(String s:fileContentList){
            if(s.length()<150){
                continue;
            }
            fc +=s+"</>";
            exchangeNum+=1;
        }
        fineTuneAttach.setFileContent(fc);
        //则待转化内容小于10
        if(exchangeNum<10){
            fineTuneAttach.setStatus("97");
            fineTuneAttach.setMessages("您的文件案例数量为："+exchangeNum+"个,最少需要10个才能满足一次训练最低要求，"+"请删除该附件后增加案例在继续上传！");
        }
        boolean flag = fineTuneAttachMapper.insert(fineTuneAttach) > 0;
    }

    /**
     * 获取转化后的内容
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int fileContentToFtContent(Long id,String promptsStoreId) {
        FineTuneAttachVo fineTuneAttachVo = fineTuneAttachService.queryById(id);
        List<String> contens = Arrays.asList(fineTuneAttachVo.getFileContent().split("</>")) ;

        //查询生成要求
        PromptsStoreVo promptsStoreVo =  promptsStoreMapper.selectVoById(Long.valueOf(promptsStoreId));

        //计算可能等待时间
        int  second = 25*(contens.size())/60;
        log.info("内容转化任务正在队列中,初步预计需要"+second+"分钟,请稍后....");

        fineTuneAttachMapper.update(null,
            new LambdaUpdateWrapper<FineTuneAttach>()
                .set(FineTuneAttach::getStatus, "4")
                .set(FineTuneAttach::getMessages, "内容转化任务正在队列中,初步预计需要"+second+"分钟,请稍后....")
                .eq(FineTuneAttach::getId, id));

        Thread thread = new Thread(() -> {


            try {

                int row = 0;
                String ftAllcontent = "";

                ChatRequest chatRequest = null;

                ChatCompletion chatCompletion =null;
//                String systemRole = "你是一个内容格式转化Ai工具，你可以将一切收到内容转化为符合chatgpt微调的格式，  " +
//                    "格式如下：{\"messages\": [{\"role\": \"system\", \"content\": \"xxxxx.\"}, {\"role\": \"assistant\", \"content\": \"xxxx\"}, {\"role\": \"user\", \"content\": \"xxxxx\"}]}，" +
//                    "内容结构必须是assistant担任面试官负责提问引出面试者解决问题，user担任面试者回答内容。" +
//                    "其中 system 角色固定为 you are  Marketing analysts 。" +
//                    "--------" +
//                    "请不要做任何解释以及格式以外的内容！";
//                String systemRole = "你是一个内容格式转化Ai工具，你可以将一切收到内容转化为符合chatgpt微调的格式。  ";
                String systemRole = StringUtils.isEmpty(promptsStoreVo.getSystemRole())?"你是一个内容格式转化Ai工具，你可以将一切收到内容转化为符合chatgpt微调的格式。  ":promptsStoreVo.getSystemRole();
                List<Message> messages =null;

                Message message1 = Message.builder().role(Message.Role.SYSTEM).content(systemRole).build();
                Message message2 = null;
                for(String li:contens){
                    if(li.length()<100){
                        continue;
                    }
//                    String userprompts = "使用 <Data></Data> 标记中的内容作为需要处理的内容:\n" +
//                        "<Data>\n" +
//                        li +
//                        "</Data>\n" +
//                        "回答要求：\n" +
//                        "- 将内容转化为面试官与面试者的对话，面试官多以友善引导态度。\n" +
//                        "- 将内容整理成符合chatgpt微调的格式并输出json格式。 格式如下：{\"messages\": [{\"role\": \"system\", \"content\": \"xxxxx.\"}, {\"role\": \"assistant\", \"content\": \"xxxx\"}, {\"role\": \"user\", \"content\": \"xxxxx\"}]}\n" +
//                        "- 注意微调格式结尾中 必须以assistant结尾作为对话结束的描述。\n"+
//                        "- 输出结果中需要包含system、assistant、user三种结构的多组对话内容，覆盖所有内容。\n" +
//                        "- 内容形式必须是assistant担任面试官负责提问引出面试者解决问题，user担任面试者回答内容。\n"+
//                        "- 保持答案与 <Data></Data> 中描述的一致,可适当调整内容以保证符合对话流畅度。\n" +
//                        "- 其中需要 system 角色，角色根据处理的内容来确定他是一个什么方面的面试官（必须英文）， 请不要做任何解释以及格式以外的内容！" ;
                    String userprompts = "使用 <Data></Data> 标记中的内容作为需要处理的内容:\n" +
                        "<Data>\n" +
                        li +
                        "</Data>\n" +
                        "回答要求：\n" +promptsStoreVo.getUserPrompts();

                    message2 = Message.builder().role(Message.Role.USER).content(userprompts).build();
                    messages = new ArrayList<>(2);
                    messages.add(message1);
                    messages.add(message2);

//                     chatCompletion = ChatCompletion
//                        .builder()
//                        .messages(messages)
//                        .responseFormat(ResponseFormat.builder().type(ResponseFormat.Type.JSON_OBJECT.getName()).build())
//                        .model("gpt-4o-mini-2024-07-18")
//                        .build();
                    if(ObjectUtils.isEmpty(promptsStoreVo.getTemperature())){
                        promptsStoreVo.setTemperature(0.5);
                    }
                    if(ObjectUtils.isEmpty(promptsStoreVo.getTopP())){
                        promptsStoreVo.setTopP(1.0);
                    }
                    if(ObjectUtils.isEmpty(promptsStoreVo.getPresencePenalty())){
                        promptsStoreVo.setPresencePenalty(0.0);
                    }
                    if(ObjectUtils.isEmpty(promptsStoreVo.getFrequencyPenalty())){
                        promptsStoreVo.setFrequencyPenalty(0.0);
                    }


                    chatCompletion = ChatCompletion
                        .builder()
                        .messages(messages)
                        .responseFormat(ResponseFormat.builder().type(ResponseFormat.Type.JSON_OBJECT.getName()).build())
                        .model(promptsStoreVo.getModelId())
                        .temperature(promptsStoreVo.getTemperature())
                        .topP(promptsStoreVo.getTopP())
                        .presencePenalty(promptsStoreVo.getPresencePenalty())
                        .frequencyPenalty(promptsStoreVo.getFrequencyPenalty())
                        .build();
                    li = li.replaceAll("</>","");
                    ftAllcontent += this.getSwitchContent(li,chatCompletion)+"</>";
                }
                //压缩json格式
                ftAllcontent = ftAllcontent.replaceAll("\\r\\n|\\r|\\n", "");

                //转化本地检测
                int hasMsgNum =  this.countOccurrences(ftAllcontent,"\"messages\"");

                //低于10个不行
                if(hasMsgNum<10){
                       fineTuneAttachMapper.update(null,
                        new LambdaUpdateWrapper<FineTuneAttach>()
                            .set(FineTuneAttach::getStatus, "99")
//                   .set(FineTuneAttach::getFtContent, ftAllcontent)
                            .set(FineTuneAttach::getMessages, "您正确提供了"+hasMsgNum+"个案例，但是每轮训练最少需要10个案例，请删除本次上传附件，在您补充至10个案例在继续上传转化。")
                            .eq(FineTuneAttach::getId, id));
                    return ;
                }
                ftAllcontent = ftAllcontent.replaceAll("</>","\n");
                //检测格式
                String errormsg =  "";
                try {
                    errormsg =  new DatasetChecker().getFineTuneFile(ftAllcontent);
                }catch (Exception e){
                    log.info("发现格式有误！");
                }
                log.info("errormsg=="+errormsg);

                //检查json格式
                DataJsonChecker.ValidationResult ValidationResult =  DataJsonChecker.validateJson(ftAllcontent);
                errormsg += DataJsonChecker.printValidationResult(ValidationResult);


                if(StringUtils.isEmpty(errormsg)){
                     fineTuneAttachMapper.update(null,
                        new LambdaUpdateWrapper<FineTuneAttach>()
                            .set(FineTuneAttach::getStatus, "1")
                            .set(FineTuneAttach::getFtContent, ftAllcontent)
                            .set(FineTuneAttach::getMessages, "已转化，请检查修改转化内容并上传")
                            .eq(FineTuneAttach::getId, id));
                }else{
                     fineTuneAttachMapper.update(null,
                        new LambdaUpdateWrapper<FineTuneAttach>()
                            .set(FineTuneAttach::getStatus, "99")
                            .set(FineTuneAttach::getFtContent, ftAllcontent)
                            .set(FineTuneAttach::getMessages,   "错误原因："+errormsg+",\n解决办法:点击查看转化后内容，修改错误，点击重新检测。直到无错误提示为止。")
                            .eq(FineTuneAttach::getId, id));
                }


            }catch (Exception e){
                log.info("error:"+e.getMessage());
                fineTuneAttachMapper.update(null,
                    new LambdaUpdateWrapper<FineTuneAttach>()
                        .set(FineTuneAttach::getStatus, "99")
                        .set(FineTuneAttach::getMessages, "内容转化失败，原因："+e.getMessage())
                        .eq(FineTuneAttach::getId, id));
            }


//
        });
        thread.start();

        return 1;

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public FineTuneAttachVo getAttachStatus(Long id) {
        FineTuneAttachVo fineTuneAttachVo = fineTuneAttachService.queryById(id);
        return fineTuneAttachVo;
    }

    /**
     * 检测训练状态
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
//    String ftId, Long finetuneAttachId
    public int getTraningStatus(String ftId){
        log.info("检测状态中...");

        //查询进行中状态 刷新
        List<FineTuneAttachVo> attachVoList =  queryAttachLodingList(ftId);
        int reflashSize = attachVoList.size();
            FineTuneInfo fineTuneInfo = null;
        FineTuneAttach fineTuneAttach = null;
        FineTuneJobResponse  retrieveFineTuneResponse2 = null;
        FineTuneAttachBo fineTuneAttachBo =null;
        if (chatConfig == null) {
            log.warn("ChatClientConfig is not available, skipping fine-tune refresh");
            return 0;
        }
        openAiClient = chatConfig.getOpenAiClient();
        if (openAiClient == null) {
            log.warn("OpenAiClient is not available, skipping fine-tune refresh");
            return 0;
        }
        for(FineTuneAttachVo attachVo:attachVoList){
            fineTuneInfo = getFirstInfoByftId(attachVo.getFtId());
            fineTuneAttach = new FineTuneAttach();
            BeanUtils.copyProperties(attachVo,fineTuneAttach);
            //训练中
            if("3".equals(attachVo.getStatus())){
                 fineTuneAttachBo = new FineTuneAttachBo();
                 retrieveFineTuneResponse2 = openAiClient.retrieveFineTuneJob(fineTuneAttach.getFtJobId());
                 String fileStatus = retrieveFineTuneResponse2.getStatus();
                 log.info("当前微调状态："+fileStatus);
                 if("succeeded".equals(fileStatus)){
                     String ftmodelName = retrieveFineTuneResponse2.getFineTunedModel();
                     log.info("ftmodelName::{}",ftmodelName);
                     fineTuneInfo.setFtModelName(ftmodelName);
                     fineTuneInfo.setStatus("1");
                     fineTuneAttachBo.setStatus("2");
                     fineTuneAttach.setStatus("2");
                 }
                 String infostatus = fineTuneInfo.getStatus();
                 Boolean isUpdate = false;
                 if(!"2".equals(infostatus)){
                     isUpdate = false;
                 }else{
                     isUpdate = true;
                 }
                 handleStatusData(fileStatus, fineTuneAttachBo,fineTuneAttach,retrieveFineTuneResponse2,fineTuneInfo, isUpdate);

            }else if("4".equals(attachVo.getStatus())){
                //转化中
                getContentCheck(attachVo.getId(),attachVo.getFtContent());
            }
        }

        return reflashSize;
    }


    //查询附件进行中状态
    public List<FineTuneAttachVo> queryAttachLodingList(String ftId) {
        LambdaQueryWrapper<FineTuneAttach> lqw = buildQueryAttachWrapper(ftId);
        return fineTuneAttachMapper.selectVoList(lqw);
    }
    //查询进行中状态
    private LambdaQueryWrapper<FineTuneAttach> buildQueryAttachWrapper(String ftId) {
        LambdaQueryWrapper<FineTuneAttach> lqw = Wrappers.lambdaQuery();
        lqw.eq( FineTuneAttach::getStatus,3)
            .or().eq( FineTuneAttach::getStatus,4);
        lqw.eq(StringUtils.isNotBlank(ftId), FineTuneAttach::getFtId,ftId);
        return lqw;
    }




    @Override
    @Transactional(rollbackFor = Exception.class)
    public int  getContentCheck(Long id,String ftContent) {
//        FineTuneAttachVo fineTuneAttachVo = fineTuneAttachService.queryById(id);

        //检测格式
        String errormsg =  "";
        try {
            errormsg =  new DatasetChecker().getFineTuneFile(ftContent);
        }catch (Exception e){
            log.info("发现格式有误！");
        }
        log.info("errormsg=="+errormsg);

        //检查json格式
        DataJsonChecker.ValidationResult ValidationResult =  DataJsonChecker.validateJson(ftContent);
        errormsg += DataJsonChecker.printValidationResult(ValidationResult);

        if(StringUtils.isEmpty(errormsg)){
          return   fineTuneAttachMapper.update(null,
                new LambdaUpdateWrapper<FineTuneAttach>()
                    .set(FineTuneAttach::getStatus, "1")
                    .set(FineTuneAttach::getFtContent, ftContent)
                    .set(FineTuneAttach::getMessages, "已成功转化，请开始训练数据！")
                    .eq(FineTuneAttach::getId, id));
        }else{
            return fineTuneAttachMapper.update(null,
                new LambdaUpdateWrapper<FineTuneAttach>()
                    .set(FineTuneAttach::getStatus, "99")
                    .set(FineTuneAttach::getFtContent, ftContent)
                    .set(FineTuneAttach::getMessages,   "错误原因："+errormsg+",\n解决办法:点击查看转化后内容，修改错误，点击重新检测。直到无错误提示为止。")
                    .eq(FineTuneAttach::getId, id));
        }

    }


    /**
     * 修改转化后的内容
     * @param id
     * @param ftContent
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFtContent(Long id, String ftContent) {
        return fineTuneAttachMapper.update(null,
            new LambdaUpdateWrapper<FineTuneAttach>()
                .set(FineTuneAttach::getFtContent, ftContent)
                .eq(FineTuneAttach::getId, id));
    }

    /**
     * 每个案例一篇例子
     * @param content
     * @return
     */
    public  String getSwitchContent(String content,ChatCompletion chatCompletion){

        String switchContent = this.chat(chatCompletion);
        log.info("转化内容："+switchContent);

        //剔除格式以外内容
        Pattern pattern = Pattern.compile("\\{.*\\}");
        Matcher matcher = pattern.matcher(switchContent);
        if (matcher.find()) {
            log.info(matcher.group(0));
            switchContent = matcher.group(0);
        }
        switchContent = switchContent.replace("```","").replace("```json","");
        return switchContent;

    }

    private static Integer su = 0;
    /**
     *
     * gpt非流式输出（带重试机制）
     * @param chatCompletion
     * @return
     */
    public String chat(ChatCompletion chatCompletion) {
        return chatWithRetry(chatCompletion, 3, 1000);
    }

    /**
     * 带重试机制的chat方法
     * @param chatCompletion 聊天完成请求
     * @param maxRetries 最大重试次数
     * @param baseDelayMs 基础延迟时间（毫秒）
     * @return AI响应内容
     */
    private String chatWithRetry(ChatCompletion chatCompletion, int maxRetries, long baseDelayMs) {
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.info("尝试调用OpenAI API，第{}次尝试", attempt);

                if (chatConfig == null) {
                    log.warn("ChatClientConfig is not available, skipping OpenAI call");
                    return null;
                }
                openAiClient = chatConfig.getOpenAiClient();
                if (openAiClient == null) {
                    log.warn("OpenAiClient is not available, skipping OpenAI call");
                    return null;
                }
                ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);

                // 获取token使用情况
                long totalTokens = chatCompletionResponse.getUsage().getTotalTokens();
                long inputTokens = chatCompletionResponse.getUsage().getPromptTokens();
                long outputTokens = chatCompletionResponse.getUsage().getCompletionTokens();

                log.info("次数："+su++);
                log.info("本次花费，总token -TotalTokens-token:{}",totalTokens);
                log.info("本次花费，输出token-CompletionTokens-token:{}",outputTokens);
                log.info("本次花费，输入token-PromptTokens-token:{}",inputTokens);

                // 计算点数花费 - 根据公式：40点数 = 400k输入tokens = 50k输出tokens
                // 输入token点数计算：1k输入tokens = 0.1点数
                double inputPointsCost = (inputTokens / 1000.0) * 0.1;
                // 输出token点数计算：1k输出tokens = 0.8点数
                double outputPointsCost = (outputTokens / 1000.0) * 0.8;
                // 总点数花费
                double totalPointsCost = inputPointsCost + outputPointsCost;
                // 保留4位小数
                totalPointsCost = Math.round(totalPointsCost * 10000.0) / 10000.0;

                log.info("本次点数花费详情 - 输入token点数: {}, 输出token点数: {}, 总点数: {}",
                         Math.round(inputPointsCost * 10000.0) / 10000.0,
                         Math.round(outputPointsCost * 10000.0) / 10000.0,
                         totalPointsCost);

                log.info("OpenAI API调用成功，第{}次尝试", attempt);
                return chatCompletionResponse.getChoices().get(0).getMessage().getContent();

            } catch (Exception e) {
                lastException = e;
                String errorType = getErrorType(e);
                log.warn("OpenAI API调用失败，第{}次尝试，错误类型: {}, 错误信息: {}",
                        attempt, errorType, e.getMessage());

                // 如果是最后一次尝试，不再重试
                if (attempt == maxRetries) {
                    log.error("OpenAI API调用失败，已达到最大重试次数: {}", maxRetries, e);
                    break;
                }

                // 计算延迟时间（指数退避）
                long delayMs = baseDelayMs * (long) Math.pow(2, attempt - 1);
                log.info("等待 {} 毫秒后进行第{}次重试", delayMs, attempt + 1);

                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("重试等待被中断", ie);
                    break;
                }
            }
        }

        // 所有重试都失败了，返回友好的错误信息
        String errorMessage = generateFriendlyErrorMessage(lastException);
        log.error("OpenAI API调用最终失败: {}", errorMessage);
        return errorMessage;
    }


    /**
     * 读取html内容
     * @param filePath
     * @return
     */
    public static String readHTMLFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }



    /**
     *
     * 微调数据训练
     *
     * 微调数据加/累加
     * @param ftId
     * @param finetuneAttachId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FineTuneAttachBo createFinetuneModel(String ftId, Long finetuneAttachId){
        FineTuneAttachBo fineTuneAttachBo = new FineTuneAttachBo();
        FineTuneInfo fineTuneInfo = getFirstInfoByftId(ftId);
        FineTuneAttach fineTuneAttach = fineTuneAttachService.getFirstattachByftId(finetuneAttachId);
        String jsonlFileContent = fineTuneAttach.getFtContent();
        boolean isUpdateData = false;
        String fineTuneJobId = "";
        //基座模型根据创建或者修改来决定
//        String baseModel = "gpt-4o-mini-2024-07-18";
        String baseModel = fineTuneInfo.getBaseModel();
        if(fineTuneInfo.getStatus()=="1"||!StringUtils.isEmpty(fineTuneInfo.getFtModelName())){
            baseModel = fineTuneInfo.getFtModelName();
            isUpdateData = true;
        }else{
            isUpdateData = false;
        }
log.info("训练的基座模型：{}"+baseModel);
        try {
            // 创建一个临时文件
            File tempFile = File.createTempFile("finetune"+ DateUtils.dateTime(), ".jsonl");
            log.info("文件地址："+tempFile.getAbsoluteFile());
            // 将字符串写入临时文件
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                writer.write(jsonlFileContent);
            }
            //微调模型创建
//            openAiStreamClient =  chatConfig.getOpenAiStreamClient();
            if (chatConfig == null) {
                log.error("ChatClientConfig is not available, cannot create fine-tune");
                throw new BaseException("ChatClientConfig is not available");
            }
            openAiClient = chatConfig.getOpenAiClient();
            if (openAiClient == null) {
                log.error("OpenAiClient is not available, cannot create fine-tune");
                throw new BaseException("OpenAiClient is not available");
            }
            //上传
            UploadFileResponse uploadFileResponse = openAiClient.uploadFile("fine-tune",tempFile);
            log.info("上传后文件信息："+uploadFileResponse);
            String fileId = String.valueOf(uploadFileResponse.getId());
            log.info("文件的id："+fileId);
            fineTuneAttach.setFileId(fileId);

            //更新文件信息  同时把状态设置为进行中
            fineTuneAttachMapper.update(null,
                new LambdaUpdateWrapper<FineTuneAttach>()
                    .set(FineTuneAttach::getFtFileId,fileId)
                    .set(FineTuneAttach::getStatus,"3")
                    .set(FineTuneAttach::getMessages,"训练任务进行中，请稍后查看..")
                    .eq(FineTuneAttach::getId, finetuneAttachId));


            //创建job
            FineTuneJobResponse fineTuneJobResponse = openAiClient.fineTuneJob(fileId,baseModel);

            fineTuneJobId = fineTuneJobResponse.getId();
            fineTuneInfo.setFtJobId(fineTuneJobId);
            log.info("微调jobId:"+fineTuneJobId);

            //更新作业id信息
            baseMapper.update(null,
                new LambdaUpdateWrapper<FineTuneInfo>()
                    .set(FineTuneInfo::getFtJobId, fineTuneJobId)
                    .eq(FineTuneInfo::getId, fineTuneInfo.getId()));
            fineTuneAttachMapper.update(null,
                new LambdaUpdateWrapper<FineTuneAttach>()
                    .set(FineTuneAttach::getFtJobId,fineTuneJobId)
                    .eq(FineTuneAttach::getId, fineTuneAttach.getId()));


            //验证文件需要训练一段时间后
            try {Thread.sleep(5000);}catch (Exception e){}

            //验证文件
            FineTuneJobResponse retrieveFineTuneResponse = openAiClient.retrieveFineTuneJob(fineTuneJobId);

            //查看状态
            String status = retrieveFineTuneResponse.getStatus();
            if("succeeded".equals(status)){
                String ftModelName = retrieveFineTuneResponse.getFineTunedModel();
                log.info("modelname:{}",ftModelName);
                fineTuneInfo.setFtModelName(ftModelName);
            }

            log.info("status:"+status);

            if("validating_files".equals(status)){
                log.info("验证中。。。");
                //此时需要循环验证
                String finalFineTuneJobId = fineTuneJobId;
                boolean finalIsUpdateData = isUpdateData;
                FineTuneAttachBo finalFineTuneAttachBo = fineTuneAttachBo;
                log.info("处理验证中的状态。");
                Thread thread = new Thread(() -> {
                    Boolean isValidating = true;
                    log.info("开启特殊状态循环检测。");
                    String fileStatus = "";
                    while (isValidating){
                        log.info("进入循环验证微调状态中，当前状态："+status);
                        try {
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        FineTuneJobResponse retrieveFineTuneResponse2 = null;
                        if (chatConfig != null) {
                            openAiClient = chatConfig.getOpenAiClient();
                            if (openAiClient != null) {
                                retrieveFineTuneResponse2 = openAiClient.retrieveFineTuneJob(finalFineTuneJobId);
                            }
                        }
                        if (retrieveFineTuneResponse2 != null) {
                            fileStatus = retrieveFineTuneResponse2.getStatus();
                        } else {
                            log.warn("Cannot retrieve fine-tune status, OpenAI client not available");
                            break; // Exit the loop if we can't get status
                        }
                        log.info("当前微调状态："+fileStatus);
                        if(!"validating_files".equals(fileStatus)&&!"running".equals(fileStatus)){
                            log.info("该状态会跳出循环："+fileStatus);
                            if("succeeded".equals(fileStatus)){
                                String ftModelName = retrieveFineTuneResponse2.getFineTunedModel();
                                log.info("训练后的新模型：{}",ftModelName);
                                fineTuneInfo.setFtModelName(ftModelName);
                                handleStatusData(fileStatus, finalFineTuneAttachBo,fineTuneAttach,retrieveFineTuneResponse2,fineTuneInfo, finalIsUpdateData);

                            }
                            isValidating = false;
                            break;
                        }
                    }
                    log.info("跳出循环：");
                    if(!"succeeded".equals(fileStatus)){
                        handleStatusData(fileStatus, finalFineTuneAttachBo,fineTuneAttach,retrieveFineTuneResponse,fineTuneInfo, finalIsUpdateData);

                    }
                });
                thread.start();
            }else{
                log.info("已知状态-根据状态做数据处理。");
                //根据状态做数据处理
                fineTuneAttachBo = handleStatusData(status,fineTuneAttachBo,fineTuneAttach,retrieveFineTuneResponse,fineTuneInfo,isUpdateData);

            }

            return fineTuneAttachBo;
//            tempFile.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
            log.info("error:"+e.getMessage());
        }


        return fineTuneAttachBo;
    }





    public FineTuneAttachBo handleStatusData(String status,FineTuneAttachBo fineTuneAttachBo,
                                              FineTuneAttach fineTuneAttach,
                                              FineTuneJobResponse retrieveFineTuneResponse,
                                              FineTuneInfo fineTuneInfo,
                                              Boolean isUpdateData){
        log.info("状态数据处理中，状态："+status);
        switch (status){
            case "validating_files":
                fineTuneAttachBo = handleValidating_files(fineTuneAttachBo,fineTuneAttach);
            break;
            case "running":
                fineTuneAttachBo = handleValidating_files(fineTuneAttachBo,fineTuneAttach);
                break;
            case "failed":
                fineTuneAttachBo = handleFailed(fineTuneAttachBo,fineTuneAttach,retrieveFineTuneResponse);
                break;
            case "succeeded":
                fineTuneAttachBo =  handleSucceeded(fineTuneAttachBo,fineTuneAttach,retrieveFineTuneResponse,fineTuneInfo,isUpdateData);
                break;
            default:
                //如果其他未知状态则视为失败
                fineTuneAttachBo = handleFailed(fineTuneAttachBo,fineTuneAttach,retrieveFineTuneResponse);
                break;
        }

        return fineTuneAttachBo;
    }


    // 当训练状态为 validating_files时
    public FineTuneAttachBo handleValidating_files(FineTuneAttachBo fineTuneAttachBo,
                                                   FineTuneAttach fineTuneAttach){
        //正在验证中
        fineTuneAttachBo.setStatus("3");
        fineTuneAttachBo.setMessages("正在训练中，请稍后刷新状态查看！");

        fineTuneAttachMapper.update(null,
            new LambdaUpdateWrapper<FineTuneAttach>()
                .set(FineTuneAttach::getStatus, "3")
                .set(FineTuneAttach::getFtFileId,fineTuneAttach.getFtFileId())
                .set(FineTuneAttach::getFtJobId,fineTuneAttach.getFtJobId())
                .set(FineTuneAttach::getMessages, "正在训练中，请稍后刷新状态查看！")
                .eq(FineTuneAttach::getId, fineTuneAttach.getId()));
        return fineTuneAttachBo;
    }

    // 当训练状态为 failed时
    public FineTuneAttachBo handleFailed(FineTuneAttachBo fineTuneAttachBo,
                                         FineTuneAttach fineTuneAttach,
                                         FineTuneJobResponse retrieveFineTuneResponse){
        //获取错误消息
        FineTuneError fineTuneError =  retrieveFineTuneResponse.getError();
        String errormsg = fineTuneError.getMessage();

        log.info("所有消息："+retrieveFineTuneResponse);

        //更新附件信息
        fineTuneAttachBo.setStatus("98");

         errormsg = "训练失败，请检查微调内容格式,根据错误修改内容并重新训练！详情错误："+errormsg;
        fineTuneAttachBo.setMessages(errormsg);

        fineTuneAttachMapper.update(null,
            new LambdaUpdateWrapper<FineTuneAttach>()
                .set(FineTuneAttach::getStatus, "98")
                .set(FineTuneAttach::getMessages, errormsg)
                .eq(FineTuneAttach::getId, fineTuneAttach.getId()));

        return fineTuneAttachBo;
    }


    //当训练状态为  succeeded 时
    public FineTuneAttachBo handleSucceeded(FineTuneAttachBo fineTuneAttachBo,
                                         FineTuneAttach fineTuneAttach,
                                         FineTuneJobResponse retrieveFineTuneResponse,
                                            FineTuneInfo fineTuneInfo,
                                            Boolean isUpdateData){

        String ftModelName = retrieveFineTuneResponse.getFineTunedModel();
       log.info("微调模型名称："+ftModelName);
        if(!StringUtils.isEmpty(ftModelName)&&!"null".equals(ftModelName)){
            saveNewFinetuneModelInfo(fineTuneInfo,fineTuneAttach,isUpdateData);
        }
        //更新附件信息
        fineTuneAttachBo.setStatus("2");

        String successmsg = "训练成功！";
        fineTuneAttachBo.setMessages(successmsg);

        return fineTuneAttachBo;
    }



    //成功创建之后处理数据库保存相关信息
    public boolean saveNewFinetuneModelInfo(FineTuneInfo fineTuneInfo,FineTuneAttach fineTuneAttach,Boolean isUpdateData) {

        log.info("正在保存模型信息：{}",fineTuneInfo.getFtModelName());
        //更新模型信息  zuleed++ 不论是否更新模型累增数据，按照openai的标准都=每次训练都会创建新模型
//        if(!isUpdateData){
            baseMapper.update(null,
                new LambdaUpdateWrapper<FineTuneInfo>()
                    .set(FineTuneInfo::getStatus, "1")
                    .set(FineTuneInfo::getFtModelName, fineTuneInfo.getFtModelName())
                    .eq(FineTuneInfo::getId, fineTuneInfo.getId()));
//        }

        //新增模型
//        if(!isUpdateData){
            SysModel sysModel = new SysModel();
            sysModel.setModelName(fineTuneInfo.getFtModelName());
            sysModel.setModelDescribe(fineTuneInfo.getFtName()+DateUtils.dateTimeNow("yyyy-MM-dd_HH:mm:ss"));
            sysModel.setModelPrice(0.2);
            sysModel.setModelType("1");
            String showStatus = fineTuneInfo.getUseStatus()=="0"?"1":"0";
            sysModel.setModelShow(showStatus);
            sysModelMapper.insert(sysModel);
//        }
//        sysModelMapper

        //更新最新模型设置里面 训练模型到最新版本
//        modelSettingMapper.update(null,
//            new LambdaUpdateWrapper<ModelSetting>()
//                .set(ModelSetting::getStatus, "2")
//                .set(ModelSetting::getMessages, "已成功上传数据！")
//                .eq(ModelSetting::getId, fineTuneAttach.getId())
//        )
        //更新附件信息
        fineTuneAttachMapper.update(null,
            new LambdaUpdateWrapper<FineTuneAttach>()
                .set(FineTuneAttach::getStatus, "2")
                .set(FineTuneAttach::getMessages, "已成功上传数据！")
                .eq(FineTuneAttach::getId, fineTuneAttach.getId()));


        return true;
    }

    public static void main(String[] args) {
        System.out.println(DateUtils.dateTimeNow("yyyy-MM-dd_HH:mm:ss"));
    }
    /**
     * 根据微调id查询微调数据
     * @param ftId
     * @return
     */
    public FineTuneInfo getFirstInfoByftId(String ftId) {
        QueryWrapper<FineTuneInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ft_id", ftId); // 设置查询条件，字段名应该与数据库中的列名一致
        queryWrapper.last("LIMIT 1"); // 限制查询结果只返回一条记录
        return this.getOne(queryWrapper); // 执行查询并返回结果
    }

//    /**
//     * 根据微调附件找到微调数据
//     * @param attachId
//     * @return
//     */
//    public FineTuneAttach getFirstattachByftId(Long attachId) {
//        QueryWrapper<FineTuneAttach> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("id", attachId); // 设置查询条件，字段名应该与数据库中的列名一致
//        queryWrapper.last("LIMIT 1"); // 限制查询结果只返回一条记录
//        return attachService.getOne(queryWrapper); // 执行查询并返回结果
//    }

    public static int countOccurrences(String str, String word) {
        int count = 0;
        int fromIndex = 0;

        while ((fromIndex = str.indexOf(word, fromIndex)) != -1) {
            count++;
            fromIndex += word.length(); // 移动到下一个位置
        }

        return count;
    }

    /**
     * 获取异常类型描述
     * @param e 异常对象
     * @return 错误类型描述
     */
    private String getErrorType(Exception e) {
        if (e == null) {
            return "未知错误";
        }

        String message = e.getMessage();
        String className = e.getClass().getSimpleName();

        // SSL握手异常
        if (e instanceof javax.net.ssl.SSLHandshakeException ||
            (message != null && message.contains("SSLHandshakeException"))) {
            return "SSL握手失败";
        }

        // HTTP/2协议错误
        if (message != null && message.contains("StreamResetException")) {
            if (message.contains("PROTOCOL_ERROR")) {
                return "HTTP/2协议错误";
            } else if (message.contains("CANCEL")) {
                return "请求被取消";
            } else {
                return "HTTP/2流重置";
            }
        }

        // 连接超时
        if (message != null && (message.contains("timeout") || message.contains("Timeout"))) {
            return "连接超时";
        }

        // 连接被拒绝
        if (message != null && message.contains("Connection refused")) {
            return "连接被拒绝";
        }

        // 网络不可达
        if (message != null && message.contains("Network is unreachable")) {
            return "网络不可达";
        }

        // 其他网络异常
        if (className.contains("IOException") || className.contains("SocketException")) {
            return "网络IO异常";
        }

        return className + "异常";
    }

    /**
     * 生成友好的错误信息
     * @param e 异常对象
     * @return 友好的错误信息
     */
    private String generateFriendlyErrorMessage(Exception e) {
        if (e == null) {
            return "AI服务暂时不可用，请稍后重试";
        }

        String message = e.getMessage();

        // SSL握手异常
        if (e instanceof javax.net.ssl.SSLHandshakeException ||
            (message != null && message.contains("SSLHandshakeException"))) {
            return "网络连接安全验证失败，可能是网络环境问题，请检查网络设置后重试";
        }

        // HTTP/2协议错误
        if (message != null && message.contains("StreamResetException")) {
            if (message.contains("PROTOCOL_ERROR")) {
                return "网络协议错误，服务器通信异常，请稍后重试";
            } else if (message.contains("CANCEL")) {
                return "请求被服务器取消，可能是服务器负载过高，请稍后重试";
            } else {
                return "网络连接中断，请检查网络状态后重试";
            }
        }

        // 连接超时
        if (message != null && (message.contains("timeout") || message.contains("Timeout"))) {
            return "网络连接超时，请检查网络状态或稍后重试";
        }

        // 连接被拒绝
        if (message != null && message.contains("Connection refused")) {
            return "无法连接到AI服务，服务可能暂时不可用，请稍后重试";
        }

        // 网络不可达
        if (message != null && message.contains("Network is unreachable")) {
            return "网络不可达，请检查网络连接后重试";
        }

        // 默认友好信息
        return "AI服务暂时遇到问题，已自动重试但仍然失败，请稍后重试或联系技术支持";
    }
}



