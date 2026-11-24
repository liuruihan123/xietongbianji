package com.xmzs.system.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xmzs.common.office.WordToHtml;
import com.xmzs.knowledge.chain.loader.ResourceLoader;
import com.xmzs.knowledge.chain.loader.ResourceLoaderFactory;
import com.xmzs.knowledge.domain.KnowledgeAttach;
import com.xmzs.knowledge.domain.KnowledgeFragment;
import com.xmzs.system.domain.request.UploadAttachRequest;
import com.xmzs.system.domain.vo.QaCaseTreeNode;
import com.xmzs.system.domain.vo.SysOssUploadVo;
import com.xmzs.system.domain.vo.SysOssVo;
import com.xmzs.system.service.ISysOssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.xmzs.system.domain.bo.AiQaCaseBo;
import com.xmzs.system.domain.vo.AiQaCaseVo;
import com.xmzs.system.domain.AiQaCase;
import com.xmzs.system.mapper.AiQaCaseMapper;
import com.xmzs.system.service.IAiQaCaseService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 对话案例Service业务层处理
 *
 * @author zuleed
 * @date 2024-12-27
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AiQaCaseServiceImpl implements IAiQaCaseService {

    private final AiQaCaseMapper baseMapper;
    private final ISysOssService ossService;
    private final ResourceLoaderFactory resourceLoaderFactory;


    /**
     * 查询对话案例
     */
    @Override
    public AiQaCaseVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询对话案例列表
     */
    @Override
    public TableDataInfo<AiQaCaseVo> queryPageList(AiQaCaseBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AiQaCase> lqw = buildQueryWrapper(bo);
        Page<AiQaCaseVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }


    /**
     * 查询对话案例列表
     */
    @Override
    public List<AiQaCaseVo> queryList(AiQaCaseBo bo) {
        LambdaQueryWrapper<AiQaCase> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<AiQaCase> buildQueryWrapper(AiQaCaseBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<AiQaCase> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getTitle()), AiQaCase::getTitle, bo.getTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), AiQaCase::getStatus, bo.getStatus());
        lqw.like(StringUtils.isNotBlank(bo.getSourceName()), AiQaCase::getSourceName, bo.getSourceName());
        return lqw;
    }


    //构建树形结果
    public List<QaCaseTreeNode> getTreeData() {
        AiQaCaseBo aiQaCaseBo = new AiQaCaseBo();
        aiQaCaseBo.setStatus("0");
        List<AiQaCaseVo> queryList = queryList(aiQaCaseBo);
        Map<String, QaCaseTreeNode> treeMap = new HashMap<>();

        // 构建一级节点
        for (AiQaCaseVo aiQaCase : queryList) {
            String sourceId = aiQaCase.getSourceId();
            String sourceName = aiQaCase.getSourceName();
            QaCaseTreeNode parentNode = treeMap.computeIfAbsent(sourceId, k -> {
                QaCaseTreeNode node = new QaCaseTreeNode();
                node.setValue(sourceId);
                node.setLabel(sourceName);
                return node;
            });

            // 构建二级节点
            QaCaseTreeNode childNode = new QaCaseTreeNode();
            childNode.setValue(aiQaCase.getId().toString());
            childNode.setLabel(aiQaCase.getTitle());
            parentNode.getChildren().add(childNode);
        }

        return new ArrayList<>(treeMap.values());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upload(UploadAttachRequest request) {
        storeContent(request.getFile(), request.getId());
    }

    private void storeContent(MultipartFile file, String id) {
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
        List<Map<String,Object>> docInfoMapList = new ArrayList<Map<String,Object>>();
        try {
            docInfoMapList =   WordToHtml.getDocxInfo(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AiQaCase add = null;
        for (Map<String, Object> docInfo : docInfoMapList) {
            // 获取标题
            String title = (String) docInfo.get("title");
            // 获取内容
            String content = (String) docInfo.get("content");
            add = new AiQaCase();
            add.setTitle(title);
            add.setCaseContent(content);
            add.setStatus("0");
            add.setSourceUrl(oss.getUrl());
            add.setSourceName(oss.getOriginalName());
            add.setSourceId(oss.getOssId().toString());
            baseMapper.insert(add);
        }

    }



    /**
     * 新增对话案例
     */
    @Override
    public Boolean insertByBo(AiQaCaseBo bo) {
        AiQaCase add = MapstructUtils.convert(bo, AiQaCase.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改对话案例
     */
    @Override
    public Boolean updateByBo(AiQaCaseBo bo) {
        AiQaCase update = MapstructUtils.convert(bo, AiQaCase.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AiQaCase entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除对话案例
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
