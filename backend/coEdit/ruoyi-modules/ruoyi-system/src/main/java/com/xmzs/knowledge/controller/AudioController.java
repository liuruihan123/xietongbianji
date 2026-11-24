package com.xmzs.knowledge.controller;

import com.xmzs.knowledge.chain.llm.capabilities.generation.audio.AudioChatService;
import com.xmzs.knowledge.common.ResultData;
import com.xmzs.knowledge.common.constant.SysConstants;
import com.xmzs.knowledge.domain.MediaFile;
import com.xmzs.knowledge.request.AudioTranscribeRequest;
import com.xmzs.knowledge.service.MediaFileService;
import com.xmzs.knowledge.service.SpecialNounService;
import com.xmzs.system.domain.SysUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * 语音功能
 */
@RestController
@AllArgsConstructor
@RequestMapping("audio")
public class AudioController {

    private final AudioChatService audioChatService;
    private final MediaFileService mediaFileService;
    private final SpecialNounService specialNounService;

    /**
     * 音频转文本
     * @param httpServletRequest
     * @param request
     * @return
     */
    @PostMapping(value = {"/transcribe"})
    public ResultData<String> transcribe(HttpServletRequest httpServletRequest,@RequestBody AudioTranscribeRequest request) {
        SysUser sysUser = (SysUser) httpServletRequest.getSession().getAttribute(SysConstants.SESSION_LOGIN_USER_KEY);
        MediaFile mediaFile = mediaFileService.getByMfid(request.getMediaId());
        String filePath = mediaFile.getFilePath();
        File audio = new File(filePath);
        String text = audioChatService.audioToText(audio,"请用简体中文输出文本");
        System.out.println("转换前text==" + text);
        String convertedText = specialNounService.match(text);
        return ResultData.success(convertedText,"成功");
    }

}
