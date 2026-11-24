package com.xmzs.knowledge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.knowledge.domain.Knowledge;
import com.xmzs.knowledge.domain.KnowledgeShare;
import com.xmzs.knowledge.domain.bo.KnowledgeInfoBo;
import com.xmzs.knowledge.domain.vo.KnowledgeInfoVo;
import com.xmzs.knowledge.request.*;
import com.xmzs.knowledge.response.KnowledgeDetailResponse;
import com.xmzs.knowledge.response.KnowledgeListResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
* @author huangkh
* @description 针对表【knowledge(知识库)】的数据库操作Service
* @createDate 2023-06-20 21:01:33
*/
public interface KnowledgeService extends IService<Knowledge> {

    KnowledgeInfoVo queryById(Long id);

    void saveOne(KnowledgeSaveRequest request, Long userId);
    Boolean updateByBo(KnowledgeInfoBo bo);
    TableDataInfo<KnowledgeInfoVo> selectPageKnowledgeList(KnowledgeInfoBo knowledge, PageQuery pageQuery);

    void upload(KnowledgeUploadRequest request);

    void storeContent(MultipartFile file, String kid) throws IOException;

    KnowledgeDetailResponse detail(String kid);

    void removeAttach(KnowledgeAttachRemoveRequest request);

    void removeKnowledge(KnowledgeRemoveRequest request);

    List<KnowledgeListResponse> all(List<Knowledge> mineList, List<KnowledgeShare> shareList);

    PageInfo<Knowledge> pageInfo(KnowledgePageRequest knowledgePageRequest);

    Knowledge getOneByKid(String kid);

    LoginUser getLoginUser();


}
