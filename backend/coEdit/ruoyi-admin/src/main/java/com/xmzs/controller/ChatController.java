package com.xmzs.controller;


import com.xmzs.common.chat.domain.request.ChatRequest;
import com.xmzs.common.chat.domain.request.Dall3Request;
import com.xmzs.common.chat.entity.Tts.TextToSpeech;
import com.xmzs.common.chat.entity.files.UploadFileResponse;
import com.xmzs.common.chat.entity.images.Item;
import com.xmzs.common.chat.entity.whisper.WhisperResponse;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.core.exception.base.BaseException;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.system.domain.bo.ChatMessageBo;
import com.xmzs.system.domain.bo.ModelSettingBo;
import com.xmzs.system.domain.vo.ChatMessageVo;
import com.xmzs.system.service.IChatMessageService;
import com.xmzs.system.service.ISseService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：
 *
 * @author https:www.unfbx.com
 * @date 2023-03-01
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ISseService ISseService;

    private final IChatMessageService chatMessageService;

    /**
     * 聊天接口
     */
    @PostMapping("/chat")
    @ResponseBody
    public SseEmitter sseChat(@RequestBody @Valid ChatRequest chatRequest, HttpServletResponse response) {
        return ISseService.sseChat(chatRequest);
    }


    @GetMapping("/getFixedMessage")
    @ResponseBody
    public R<Map<String,Object>> getFixedMessageInfo(@NotBlank(message = "startersId not null") String startersId,@NotBlank(message = "starterContent not null") String starterContent) {
        return R.ok(ISseService.getFixedMessage(startersId,starterContent));
    }


    /**
     * 上传文件
     */
    @PostMapping("/v1/upload")
    @ResponseBody
    public UploadFileResponse upload(@RequestPart("file") MultipartFile file) {
        return ISseService.upload(file);
    }


    /**
     * 语音转文本
     *
     * @param file
     */
    @PostMapping("/audio")
    @ResponseBody
    public WhisperResponse audio(@RequestParam("file") MultipartFile file) {
        WhisperResponse whisperResponse = ISseService.speechToTextTranscriptionsV2(file);
        return whisperResponse;
    }

    /**
     * 获取模型配置信息
     *
     * @param busiType 业务类型
     */
    @GetMapping("/getBusiModelConfig")
    @ResponseBody
    public R< Map<String,Object>> getBusiModelConfig(@NotBlank(message = "busiType not null") String busiType) {
        ModelSettingBo modelSettingBo = new ModelSettingBo();
        int busiTypeCode = 0;
        switch (busiType){
            case "intv":
                busiTypeCode = 0;
                break;
            case "company":
                busiTypeCode = 2;
                break;
            case "point":
                busiTypeCode = 3;
                break;
        }
        modelSettingBo.setBusType(busiTypeCode);
        Map<String,Object> modelSettingMap = ISseService.getModelSettingInfo(busiTypeCode);
        // 检查返回的集合是否为空
        if (modelSettingMap == null || modelSettingMap.isEmpty()) {
            return R.fail(new HashMap<>());
        }
        return R.ok(modelSettingMap);
    }
    /**
     * 文本转语音
     *
     * @param textToSpeech
     */
    @PostMapping("/speech")
    @ResponseBody
    public ResponseEntity<Resource> speech(@RequestBody TextToSpeech textToSpeech) {
        return ISseService.textToSpeed(textToSpeech);
    }


    @PostMapping("/dall3")
    @ResponseBody
    public R<List<Item>> dall3(@RequestBody @Valid Dall3Request request) {
        return R.ok(ISseService.dall3(request));
    }

    /**
     * 聊天记录
     */
    @PostMapping("/chatList")
    @ResponseBody
    public R<TableDataInfo<ChatMessageVo>> list(@RequestBody @Valid ChatMessageBo chatRequest, @RequestBody PageQuery pageQuery) {
        // 默认查询当前登录用户消息记录
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (loginUser == null) {
            throw new BaseException("用户未登录！");
        }
        chatRequest.setUserId(loginUser.getUserId());
        TableDataInfo<ChatMessageVo> chatMessageVoTableDataInfo = chatMessageService.queryPageList(chatRequest, pageQuery);
        return R.ok(chatMessageVoTableDataInfo);
    }

}
