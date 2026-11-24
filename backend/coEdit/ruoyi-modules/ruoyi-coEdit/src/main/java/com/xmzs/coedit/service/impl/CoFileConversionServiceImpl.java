package com.xmzs.coedit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmzs.coedit.config.OnlyOfficeConfig;
import com.xmzs.coedit.domain.CoFile;
import com.xmzs.coedit.domain.CoFileConversion;
import com.xmzs.coedit.domain.bo.CoFileConversionBo;
import com.xmzs.coedit.domain.vo.CoFileConversionVo;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.coedit.mapper.CoFileConversionMapper;
import com.xmzs.coedit.service.ICoFileConversionService;
import com.xmzs.coedit.service.ICoFileService;
import com.xmzs.coedit.utils.JwtTokenUtil;
import com.xmzs.common.core.exception.ServiceException;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.satoken.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件转换Service实现
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CoFileConversionServiceImpl implements ICoFileConversionService {

    private final CoFileConversionMapper conversionMapper;
    private final ICoFileService fileService;
    private final OnlyOfficeConfig onlyOfficeConfig;
    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;

    /**
     * 支持的格式映射
     */
    private static final Map<String, List<String>> FORMAT_MAP = new HashMap<>();

    static {
        // Word文档转换
        FORMAT_MAP.put("docx", Arrays.asList("pdf", "odt", "rtf", "txt", "html", "epub"));
        FORMAT_MAP.put("doc", Arrays.asList("pdf", "docx", "odt", "rtf", "txt", "html"));
        FORMAT_MAP.put("odt", Arrays.asList("pdf", "docx", "rtf", "txt", "html"));
        FORMAT_MAP.put("rtf", Arrays.asList("pdf", "docx", "odt", "txt"));
        FORMAT_MAP.put("txt", Arrays.asList("pdf", "docx", "odt", "html"));
        // WPS文字格式转换
        FORMAT_MAP.put("wps", Arrays.asList("pdf", "docx", "odt", "rtf", "txt", "html"));

        // Excel表格转换
        FORMAT_MAP.put("xlsx", Arrays.asList("pdf", "ods", "csv", "html"));
        FORMAT_MAP.put("xls", Arrays.asList("pdf", "xlsx", "ods", "csv", "html"));
        FORMAT_MAP.put("ods", Arrays.asList("pdf", "xlsx", "csv", "html"));
        FORMAT_MAP.put("csv", Arrays.asList("pdf", "xlsx", "ods"));
        // WPS表格格式转换
        FORMAT_MAP.put("et", Arrays.asList("pdf", "xlsx", "ods", "csv", "html"));
        FORMAT_MAP.put("ett", Arrays.asList("pdf", "xlsx", "ods", "csv", "html"));

        // PowerPoint演示转换
        FORMAT_MAP.put("pptx", Arrays.asList("pdf", "odp", "html"));
        FORMAT_MAP.put("ppt", Arrays.asList("pdf", "pptx", "odp", "html"));
        FORMAT_MAP.put("odp", Arrays.asList("pdf", "pptx", "html"));
        // WPS演示格式转换
        FORMAT_MAP.put("dps", Arrays.asList("pdf", "pptx", "odp", "html"));
        FORMAT_MAP.put("dpt", Arrays.asList("pdf", "pptx", "odp", "html"));

        // PDF转换（有限支持）
        FORMAT_MAP.put("pdf", Arrays.asList("docx", "txt"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long convertFile(CoFileConversionBo bo) {
        // 1. 获取源文件信息
        CoFileVo sourceFile = fileService.queryById(bo.getSourceFileId());
        if (sourceFile == null) {
            throw new ServiceException("源文件不存在");
        }

        // 2. 验证目标格式是否支持
        String sourceType = sourceFile.getFileType().toLowerCase();
        String targetType = bo.getTargetFileType().toLowerCase();

        if (!isSupportedConversion(sourceType, targetType)) {
            throw new ServiceException("不支持从 " + sourceType + " 转换到 " + targetType);
        }

        // 3. 创建转换记录
        CoFileConversion conversion = new CoFileConversion();
        conversion.setSourceFileId(sourceFile.getId());
        conversion.setSourceFileName(sourceFile.getFileName());
        conversion.setSourceFileType(sourceType);
        conversion.setSourceFilePath(sourceFile.getFilePath());
        conversion.setSourceFileUrl(sourceFile.getFileUrl());
        conversion.setTargetFileType(targetType);

        // 生成目标文件名
        String targetFileName = bo.getTargetFileName();
        if (targetFileName == null || targetFileName.isEmpty()) {
            String baseName = sourceFile.getFileName();
            int dotIndex = baseName.lastIndexOf('.');
            if (dotIndex > 0) {
                baseName = baseName.substring(0, dotIndex);
            }
            targetFileName = baseName + "." + targetType;
        }
        conversion.setTargetFileName(targetFileName);

        conversion.setConversionKey(IdUtil.fastSimpleUUID());
        conversion.setStatus(0); // 等待中
        conversion.setProgress(0);
        conversion.setUserId(bo.getUserId() != null ? bo.getUserId() : LoginHelper.getUserId());
        conversion.setUserName(bo.getUserName() != null ? bo.getUserName() : LoginHelper.getUserName());
        conversion.setStartTime(new Date());

        conversionMapper.insert(conversion);

        // 4. 异步执行转换
        if (bo.getAsync() != null && bo.getAsync()) {
            executeConversionAsync(conversion.getId());
        } else {
            executeConversion(conversion.getId());
        }

        return conversion.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> batchConvert(List<CoFileConversionBo> boList) {
        List<Long> conversionIds = new ArrayList<>();
        for (CoFileConversionBo bo : boList) {
            try {
                Long id = convertFile(bo);
                conversionIds.add(id);
            } catch (Exception e) {
                log.error("批量转换失败: {}", e.getMessage());
            }
        }
        return conversionIds;
    }

    @Override
    public CoFileConversionVo getConversionStatus(Long id) {
        CoFileConversion conversion = conversionMapper.selectById(id);
        if (conversion == null) {
            throw new ServiceException("转换记录不存在");
        }
        return buildVo(conversion);
    }

    @Override
    public List<String> getSupportedFormats(String sourceFileType) {
        String type = sourceFileType.toLowerCase();
        return FORMAT_MAP.getOrDefault(type, Collections.emptyList());
    }

    @Override
    public Map<String, List<String>> getAllSupportedFormats() {
        return new HashMap<>(FORMAT_MAP);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelConversion(Long id) {
        CoFileConversion conversion = conversionMapper.selectById(id);
        if (conversion == null) {
            throw new ServiceException("转换记录不存在");
        }

        if (conversion.getStatus() == 2 || conversion.getStatus() == 3) {
            throw new ServiceException("转换已完成或已失败，无法取消");
        }

        conversion.setStatus(3);
        conversion.setFailReason("用户取消");
        conversion.setEndTime(new Date());
        conversion.setDuration(conversion.getEndTime().getTime() - conversion.getStartTime().getTime());

        return conversionMapper.updateById(conversion) > 0;
    }

    @Override
    public TableDataInfo<CoFileConversionVo> getConversionHistory(Long fileId, PageQuery pageQuery) {
        LambdaQueryWrapper<CoFileConversion> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CoFileConversion::getSourceFileId, fileId);
        wrapper.orderByDesc(CoFileConversion::getCreateTime);

        Page<CoFileConversionVo> page = conversionMapper.selectVoPage(pageQuery.build(), wrapper);
        page.getRecords().forEach(this::enrichVo);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<CoFileConversionVo> getUserConversionHistory(Long userId, PageQuery pageQuery) {
        LambdaQueryWrapper<CoFileConversion> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CoFileConversion::getUserId, userId);
        wrapper.orderByDesc(CoFileConversion::getCreateTime);

        Page<CoFileConversionVo> page = conversionMapper.selectVoPage(pageQuery.build(), wrapper);
        page.getRecords().forEach(this::enrichVo);
        return TableDataInfo.build(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteConversion(Long id) {
        CoFileConversion conversion = conversionMapper.selectById(id);
        if (conversion == null) {
            throw new ServiceException("转换记录不存在");
        }

        // 删除转换后的文件
        if (conversion.getTargetFilePath() != null) {
            File file = new File(conversion.getTargetFilePath());
            if (file.exists()) {
                file.delete();
            }
        }

        return conversionMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteConversion(List<Long> ids) {
        for (Long id : ids) {
            deleteConversion(id);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer cleanExpiredConversions(Integer days) {
        Date expireDate = new Date(System.currentTimeMillis() - days * 24 * 60 * 60 * 1000L);

        LambdaQueryWrapper<CoFileConversion> wrapper = Wrappers.lambdaQuery();
        wrapper.lt(CoFileConversion::getCreateTime, expireDate);

        List<CoFileConversion> conversions = conversionMapper.selectList(wrapper);

        for (CoFileConversion conversion : conversions) {
            deleteConversion(conversion.getId());
        }

        return conversions.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long retryConversion(Long id) {
        CoFileConversion oldConversion = conversionMapper.selectById(id);
        if (oldConversion == null) {
            throw new ServiceException("转换记录不存在");
        }

        CoFileConversionBo bo = new CoFileConversionBo();
        bo.setSourceFileId(oldConversion.getSourceFileId());
        bo.setTargetFileType(oldConversion.getTargetFileType());
        bo.setTargetFileName(oldConversion.getTargetFileName());
        bo.setUserId(oldConversion.getUserId());
        bo.setUserName(oldConversion.getUserName());

        return convertFile(bo);
    }

    @Override
    public String downloadConvertedFile(Long id) {
        CoFileConversion conversion = conversionMapper.selectById(id);
        if (conversion == null) {
            throw new ServiceException("转换记录不存在");
        }

        if (conversion.getStatus() != 2) {
            throw new ServiceException("转换未完成");
        }

        return conversion.getTargetFileUrl();
    }

    /**
     * 执行转换（同步）
     */
    private void executeConversion(Long conversionId) {
        CoFileConversion conversion = conversionMapper.selectById(conversionId);
        if (conversion == null) {
            return;
        }

        try {
            // 更新状态为转换中
            conversion.setStatus(1);
            conversion.setProgress(10);
            conversionMapper.updateById(conversion);

            // 调用OnlyOffice转换API
            Map<String, Object> result = callConversionApi(conversion);

            // 处理转换结果
            handleConversionResult(conversion, result);

        } catch (Exception e) {
            log.error("转换失败: {}", e.getMessage(), e);
            conversion.setStatus(3);
            conversion.setFailReason(e.getMessage());
            conversion.setEndTime(new Date());
            conversion.setDuration(conversion.getEndTime().getTime() - conversion.getStartTime().getTime());
            conversionMapper.updateById(conversion);
        }
    }

    /**
     * 执行转换（异步）
     */
    private void executeConversionAsync(Long conversionId) {
        new Thread(() -> executeConversion(conversionId)).start();
    }

    /**
     * 调用OnlyOffice转换API
     */
    private Map<String, Object> callConversionApi(CoFileConversion conversion) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("url", conversion.getSourceFileUrl());
        requestBody.put("filetype", conversion.getSourceFileType());
        requestBody.put("outputtype", conversion.getTargetFileType());
        requestBody.put("key", conversion.getConversionKey());
        requestBody.put("async", false);
        requestBody.put("title", conversion.getTargetFileName());

        // 如果启用JWT，添加token
        if (onlyOfficeConfig.getJwtEnabled()) {
            String token = jwtTokenUtil.generateToken(requestBody);
            requestBody.put("token", token);
        }

        String jsonRequest = objectMapper.writeValueAsString(requestBody);
        log.info("发送转换请求: {}", jsonRequest);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(onlyOfficeConfig.getDocumentServerConvertUrl());
            httpPost.setEntity(new StringEntity(jsonRequest, ContentType.APPLICATION_JSON));

            // 如果启用JWT，添加Authorization头
            if (onlyOfficeConfig.getJwtEnabled()) {
                String token = jwtTokenUtil.generateToken(requestBody);
                httpPost.setHeader(onlyOfficeConfig.getJwtHeader(), "Bearer " + token);
            }

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = new String(
                    response.getEntity().getContent().readAllBytes(),
                    StandardCharsets.UTF_8
                );
                log.info("转换响应: {}", responseBody);

                @SuppressWarnings("unchecked")
                Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
                return result;
            }
        }
    }

    /**
     * 处理转换结果
     */
    private void handleConversionResult(CoFileConversion conversion, Map<String, Object> result) throws Exception {
        Integer error = (Integer) result.get("error");

        if (error != null && error == 0) {
            // 转换成功
            String fileUrl = (String) result.get("fileUrl");
            Integer percent = (Integer) result.get("percent");

            if (percent != null && percent == 100 && fileUrl != null) {
                // 下载转换后的文件
                String targetPath = downloadConvertedFile(fileUrl, conversion);

                conversion.setTargetFilePath(targetPath);
                conversion.setTargetFileUrl(fileUrl);
                conversion.setStatus(2);
                conversion.setProgress(100);
                conversion.setEndTime(new Date());
                conversion.setDuration(conversion.getEndTime().getTime() - conversion.getStartTime().getTime());

                // 获取文件大小
                File file = new File(targetPath);
                if (file.exists()) {
                    conversion.setTargetFileSize(file.length());
                }
            } else {
                conversion.setProgress(percent != null ? percent : 50);
            }
        } else {
            // 转换失败
            String message = (String) result.get("message");
            conversion.setStatus(3);
            conversion.setFailReason(message != null ? message : "转换失败");
            conversion.setEndTime(new Date());
            conversion.setDuration(conversion.getEndTime().getTime() - conversion.getStartTime().getTime());
        }

        conversionMapper.updateById(conversion);
    }

    /**
     * 下载转换后的文件
     */
    private String downloadConvertedFile(String fileUrl, CoFileConversion conversion) throws Exception {
        // 创建目标目录
        String storagePath = onlyOfficeConfig.getStoragePath();
        if (storagePath == null || storagePath.isEmpty()) {
            storagePath = System.getProperty("user.dir") + "/upload";
        }

        File storageDir = new File(storagePath, "converted");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        // 生成目标文件路径
        String fileName = conversion.getConversionKey() + "." + conversion.getTargetFileType();
        File targetFile = new File(storageDir, fileName);

        // 下载文件
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(fileUrl);

            try (CloseableHttpResponse response = httpClient.execute(httpGet);
                 InputStream inputStream = response.getEntity().getContent();
                 FileOutputStream outputStream = new FileOutputStream(targetFile)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        }

        return targetFile.getAbsolutePath();
    }

    /**
     * 检查是否支持转换
     */
    private boolean isSupportedConversion(String sourceType, String targetType) {
        List<String> supportedTargets = FORMAT_MAP.get(sourceType);
        return supportedTargets != null && supportedTargets.contains(targetType);
    }

    /**
     * 构建Vo对象
     */
    private CoFileConversionVo buildVo(CoFileConversion conversion) {
        CoFileConversionVo vo = BeanUtil.copyProperties(conversion, CoFileConversionVo.class);
        enrichVo(vo);
        return vo;
    }

    /**
     * 丰富Vo对象
     */
    private void enrichVo(CoFileConversionVo vo) {
        // 状态描述
        switch (vo.getStatus()) {
            case 0:
                vo.setStatusDesc("等待中");
                break;
            case 1:
                vo.setStatusDesc("转换中");
                break;
            case 2:
                vo.setStatusDesc("成功");
                break;
            case 3:
                vo.setStatusDesc("失败");
                break;
            default:
                vo.setStatusDesc("未知");
        }

        // 耗时描述
        if (vo.getDuration() != null) {
            long seconds = vo.getDuration() / 1000;
            if (seconds < 60) {
                vo.setDurationDesc(seconds + "秒");
            } else {
                long minutes = seconds / 60;
                long remainSeconds = seconds % 60;
                vo.setDurationDesc(minutes + "分" + remainSeconds + "秒");
            }
        }
    }
}

