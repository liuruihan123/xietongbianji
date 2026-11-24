package com.xmzs.knowledge.controller;

import com.xmzs.knowledge.common.ResultData;
import com.xmzs.knowledge.domain.MediaFile;
import com.xmzs.knowledge.request.MediaFileBase64UploadRequest;
import com.xmzs.knowledge.request.MediaFileUploadRequest;
import com.xmzs.knowledge.service.MediaFileService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 媒体文件控制器
 */
@RestController
@AllArgsConstructor
@RequestMapping("media")
public class MediaFileController {

    private final MediaFileService mediaFileService;

    /**
     * 上传媒体文件
     * @param request
     * @return
     */
    @PostMapping(value = "upload")
    public ResultData<MediaFile> upload(MediaFileUploadRequest request){
        MediaFile mediaFile = mediaFileService.upload(request);
        return ResultData.success(mediaFile,"上传媒体文件成功");
    }

    /**
     * 上传base64图片文件
     * @param request
     * @return
     */
    @PostMapping(value = "base64Upload")
    public ResultData<MediaFile> base64Upload(@RequestBody MediaFileBase64UploadRequest request){
        MediaFile mediaFile = mediaFileService.base64Upload(request);
        return ResultData.success(mediaFile,"上传媒体文件成功");
    }
}
