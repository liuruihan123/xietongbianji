package com.xmzs.knowledge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.core.exception.base.BaseException;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.knowledge.chain.loader.ResourceLoader;
import com.xmzs.knowledge.chain.loader.ResourceLoaderFactory;
import com.xmzs.knowledge.domain.Knowledge;
import com.xmzs.knowledge.domain.KnowledgeAttach;
import com.xmzs.knowledge.domain.KnowledgeFragment;
import com.xmzs.knowledge.domain.KnowledgeShare;
import com.xmzs.knowledge.domain.bo.KnowledgeInfoBo;
import com.xmzs.knowledge.domain.vo.KnowledgeInfoVo;
import com.xmzs.knowledge.mapper.KnowledgeMapper;
import com.xmzs.knowledge.request.*;
import com.xmzs.knowledge.response.KnowledgeDetailResponse;
import com.xmzs.knowledge.response.KnowledgeListResponse;
import com.xmzs.knowledge.service.EmbeddingService;
import com.xmzs.knowledge.service.IKnowledgeAttachService;
import com.xmzs.knowledge.service.KnowledgeFragmentService;
import com.xmzs.knowledge.service.KnowledgeService;
import com.xmzs.system.domain.vo.SysOssUploadVo;
import com.xmzs.system.domain.vo.SysOssVo;
import com.xmzs.system.service.ISysOssService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author huangkh
* @description 针对表【knowledge(知识库)】的数据库操作Service实现
* @createDate 2023-06-20 21:01:33
*/
@Service
@AllArgsConstructor
@Slf4j
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeMapper, Knowledge> implements KnowledgeService{

    private final EmbeddingService embeddingService;
    private final IKnowledgeAttachService knowledgeAttachService;
    private final KnowledgeFragmentService knowledgeFragmentService;
    private final ResourceLoaderFactory resourceLoaderFactory;
    private final ISysOssService ossService;


    @Override
    public KnowledgeInfoVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    @Override
    public void saveOne(KnowledgeSaveRequest request, Long userId) {
        if (StringUtils.isBlank(request.getKid())){
            String kid = RandomUtil.randomString(10);
            Knowledge knowledge = new Knowledge();
            knowledge.setKid(kid);
            knowledge.setTypeId(request.getTypeId() );
            knowledge.setUid(userId);
            knowledge.setKname(request.getKname());
            knowledge.setDescription(request.getDescription());
            knowledge.setCreateTime(LocalDateTime.now());
            save(knowledge);
            embeddingService.createSchema(kid);
        }else {
            Knowledge knowledge = this.getById(request.getId());
            knowledge.setKid(request.getKid());
            knowledge.setTypeId(request.getTypeId() );
            knowledge.setUid(request.getUid());
            knowledge.setKname(request.getKname());
            knowledge.setDescription(request.getDescription());
            saveOrUpdate(knowledge);
        }
    }


    /**
     * 修改知识
     */
    @Override
    public Boolean updateByBo(KnowledgeInfoBo bo) {
        Knowledge update = MapstructUtils.convert(bo, Knowledge.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }
    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Knowledge entity){
        //TODO 做一些数据校验,如唯一约束
    }

    @Override
    public TableDataInfo<KnowledgeInfoVo> selectPageKnowledgeList(KnowledgeInfoBo knowledge, PageQuery pageQuery) {
        Page<KnowledgeInfoVo> page = baseMapper.selectPageKnowledgeList(pageQuery.build(), this.buildQueryWrapper(knowledge));
        return TableDataInfo.build(page);
    }

    private Wrapper<Knowledge> buildQueryWrapper(KnowledgeInfoBo knowledge) {
        Map<String, Object> params = knowledge.getParams();
        QueryWrapper<Knowledge> wrapper = Wrappers.query();
        wrapper.eq(ObjectUtil.isNotEmpty(knowledge.getTypeId()),"k.type_id", knowledge.getTypeId())
            .eq(ObjectUtil.isNotEmpty(params.get("id")),"k.id", params.get("id"))
            .like(com.xmzs.common.core.utils.StringUtils.isNotBlank(knowledge.getKname()),"k.kname",knowledge.getKname())
            .like(com.xmzs.common.core.utils.StringUtils.isNotBlank(knowledge.getDescription()),"k.description",knowledge.getDescription())
           .between(params.get("beginTime") != null && params.get("endTime") != null,
            "k.create_time", params.get("beginTime"), params.get("endTime"));
        return wrapper;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upload(KnowledgeUploadRequest request) {
        storeContent(request.getFile(), request.getKid());
    }

    @Override
    public void storeContent(MultipartFile file, String kid) {
        SysOssVo oss = ossService.upload(file);
        SysOssUploadVo uploadVo = new SysOssUploadVo();
        uploadVo.setUrl(oss.getUrl());
        uploadVo.setFileName(oss.getOriginalName());
        uploadVo.setOssId(oss.getOssId().toString());

        String fileName = file.getOriginalFilename();
        List<String> chunkList = new ArrayList<>();
        KnowledgeAttach knowledgeAttach = new KnowledgeAttach();
        knowledgeAttach.setKid(kid);
        String docId = RandomUtil.randomString(10);
        knowledgeAttach.setDocId(docId);
        knowledgeAttach.setDocName(fileName);
        knowledgeAttach.setDocUrl(oss.getUrl());
        knowledgeAttach.setDocType(fileName.substring(fileName.lastIndexOf(".")+1));
        String content = "";
        ResourceLoader resourceLoader = resourceLoaderFactory.getLoaderByFileType(knowledgeAttach.getDocType());
        List<String> fids = new ArrayList<>();
        try {
            content = resourceLoader.getContent(file.getInputStream());
            chunkList = resourceLoader.getChunkList(content);
            List<KnowledgeFragment> fragments = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            for (int i = 0; i < chunkList.size(); i++) {
                String fid = RandomUtil.randomString(16);
                fids.add(fid);
                KnowledgeFragment knowledgeFragment = new KnowledgeFragment();
                knowledgeFragment.setKid(kid);
                knowledgeFragment.setDocId(docId);
                knowledgeFragment.setFid(fid);
                knowledgeFragment.setIdx(i);
                knowledgeFragment.setContent(chunkList.get(i));
                knowledgeFragment.setCreateTime(now);
                fragments.add(knowledgeFragment);
            }
            knowledgeFragmentService.saveBatch(fragments);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("store content occur exception ",e);
        }
        knowledgeAttach.setContent(content);

        knowledgeAttach.setCreateTime(LocalDateTime.now());
//        knowledgeAttach.setCreateTime(new Date());
        knowledgeAttachService.save(knowledgeAttach);
        embeddingService.storeEmbeddings(chunkList,kid,docId,fids);
    }

    @Override
    public KnowledgeDetailResponse detail(String kid) {
        KnowledgeDetailResponse detail  = new KnowledgeDetailResponse();
        Map<String,Object> map = new HashMap<>();
        map.put("kid",kid);
        List<Knowledge> knowledgeList = this.listByMap(map);
        if (CollectionUtil.isEmpty(knowledgeList)){
            Knowledge knowledge = knowledgeList.get(0);
            BeanUtil.copyProperties(knowledge,detail);
            List<KnowledgeAttach> attachList = knowledgeAttachService.listByMap(map);
            detail.setAttachList(attachList);
            return detail;
        }
       return null;
    }

    @Override
    public void removeAttach(KnowledgeAttachRemoveRequest request) {
        Map<String,Object> map = new HashMap<>();
        map.put("kid",request.getKid());
        map.put("doc_id",request.getDocId());
        knowledgeAttachService.removeByMap(map);
        knowledgeFragmentService.removeByMap(map);
        embeddingService.removeByDocId(request.getKid(),request.getDocId());
    }

    @Override
    public void removeKnowledge(KnowledgeRemoveRequest request) {
        Map<String,Object> map = new HashMap<>();
        map.put("kid",request.getKid());
        Knowledge knowledge = this.getOneByKid(request.getKid());
        this.removeByMap(map);
        if(knowledge!=null){
            // 删除附件和知识片段
            map.put("kid",knowledge.getKid());
            knowledgeAttachService.removeByMap(map);
            knowledgeFragmentService.removeByMap(map);
            embeddingService.removeByKid(knowledge.getId().toString());
        }

    }

    @Override
    public List<KnowledgeListResponse> all(List<Knowledge> mineList, List<KnowledgeShare> shareList) {
        List<KnowledgeListResponse> list = new ArrayList<>();
        for (Knowledge knowledge : mineList) {
            KnowledgeListResponse item = new KnowledgeListResponse();
            BeanUtil.copyProperties(knowledge,item);
            item.setRole("owner");
            Map<String,Object> map = new HashMap<>();
            map.put("kid",knowledge.getKid());
            List<KnowledgeAttach> attachList = knowledgeAttachService.listByMap(map);
            item.setAttachList(attachList);
            list.add(item);
        }
        for (KnowledgeShare knowledgeShare : shareList){
            QueryWrapper<Knowledge> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("kid",knowledgeShare.getKid());
            Knowledge knowledge = getOne(queryWrapper,false);
            if (knowledge != null){
                KnowledgeListResponse item = new KnowledgeListResponse();
                BeanUtil.copyProperties(knowledge,item);
                item.setRole("sharer");
                Map<String,Object> map = new HashMap<>();
                map.put("kid",knowledge.getKid());
                List<KnowledgeAttach> attachList = knowledgeAttachService.listByMap(map);
                item.setAttachList(attachList);
                list.add(item);
            }
        }
        return list;
    }

    @Override
    public PageInfo<Knowledge> pageInfo(KnowledgePageRequest knowledgePageRequest) {
        QueryWrapper<Knowledge> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("kname",knowledgePageRequest.getSearchContent())
                .or().like("description",knowledgePageRequest.getSearchContent())
        ;
        queryWrapper.orderByDesc("create_time");
        PageHelper.startPage(knowledgePageRequest.getPageNum(), knowledgePageRequest.getPageSize());
        PageInfo<Knowledge> pageInfo = new PageInfo<>(list(queryWrapper));
        pageInfo.getList().stream().forEach(item -> {
            if (StringUtils.isBlank(item.getDescription())) {
                item.setDescription("");
            }
        });
        return pageInfo;
    }

    @Override
    public Knowledge getOneByKid(String kid) {
        QueryWrapper<Knowledge> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("kid",kid);
        Knowledge knowledge = this.getOne(queryWrapper, false);
        return knowledge;
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
}




