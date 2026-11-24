package com.xmzs.knowledge.controller;

import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.idempotent.annotation.RepeatSubmit;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.web.core.BaseController;
import com.xmzs.knowledge.chain.vectorstore.WeaviateVectorStore;
import com.xmzs.knowledge.common.ResultData;
import com.xmzs.knowledge.common.annotation.AdminRequired;
import com.xmzs.knowledge.domain.Knowledge;
import com.xmzs.knowledge.domain.KnowledgeAttach;
import com.xmzs.knowledge.domain.KnowledgeFragment;
import com.xmzs.knowledge.domain.bo.KnowledgeAttachBo;
import com.xmzs.knowledge.domain.bo.KnowledgeFragmentBo;
import com.xmzs.knowledge.domain.bo.KnowledgeInfoBo;
import com.xmzs.knowledge.domain.vo.KnowledgeAttachVo;
import com.xmzs.knowledge.domain.vo.KnowledgeFragmentVo;
import com.xmzs.knowledge.domain.vo.KnowledgeInfoVo;
import com.xmzs.knowledge.request.*;
import com.xmzs.knowledge.service.EmbeddingService;
import com.xmzs.knowledge.service.IKnowledgeAttachService;
import com.xmzs.knowledge.service.KnowledgeFragmentService;
import com.xmzs.knowledge.service.KnowledgeService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主要用于基于数据库内容的恢复向量数据库数据
 *
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("knowledgeRestore")
public class KnowledgeRestoreDataController extends BaseController {

    private final KnowledgeService knowledgeService;
    private final KnowledgeFragmentService knowledgeFragmentService;
    private final IKnowledgeAttachService knowledgeAttachService;
    private final EmbeddingService embeddingService;
    private final WeaviateVectorStore weaviateVectorStore;

    /**
     * 全量恢复
     * @return
     */
    @GetMapping("/start")
    public R<Void> logout() {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(999999);
        TableDataInfo<KnowledgeInfoVo>   knowListPage  = knowledgeService.selectPageKnowledgeList(new KnowledgeInfoBo(),pageQuery);
        List<KnowledgeInfoVo> KnowledgeInfoVos =   knowListPage.getRows();
        KnowledgeAttachBo bo = null;
        KnowledgeFragmentBo fragmentBo = null;
        List<String> chunkList = null;
        List<String> fids = null;
        for(KnowledgeInfoVo vo:KnowledgeInfoVos){
            //创建 Schema
            log.info("正在构建知识库："+vo.getKname());
            embeddingService.createSchema(vo.getKid());
            //查询 KnowledgeFragment
            bo = new KnowledgeAttachBo();
            bo.setKid(vo.getKid());
            List<KnowledgeAttachVo> knowledgeAttachVos =  knowledgeAttachService.queryList(bo);
            //查询附件
            for(KnowledgeAttachVo attachVo:knowledgeAttachVos){
                //查询附件分组信息
                log.info("正在构建知识库附件："+attachVo.getDocName());
                fragmentBo = new KnowledgeFragmentBo();
                fragmentBo.setDocId(attachVo.getDocId());
                chunkList = new ArrayList<>();
                fids = new ArrayList<>();
                List<KnowledgeFragmentVo> knowledgeFragmentVoList =  knowledgeFragmentService.queryList(fragmentBo);
                for(KnowledgeFragmentVo knowledgeFragmentVo:knowledgeFragmentVoList){
                    chunkList.add(knowledgeFragmentVo.getContent());
                    fids.add(knowledgeFragmentVo.getFid());
                }
                log.info("正在构建知识库附件的内容大小："+chunkList.size());
                embeddingService.storeEmbeddings(chunkList,vo.getKid(),attachVo.getDocId(),fids);
            }

        }



        return R.ok("恢复结束！");
    }





    /**
     * 全量恢复
     * @return
     */
    @GetMapping("/start2")
    public R<Void> start2() {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(999999);
        TableDataInfo<KnowledgeInfoVo>   knowListPage  = knowledgeService.selectPageKnowledgeList(new KnowledgeInfoBo(),pageQuery);
        List<KnowledgeInfoVo> KnowledgeInfoVos =   knowListPage.getRows();
        KnowledgeAttachBo bo = null;
        KnowledgeFragmentBo fragmentBo = null;
        List<String> chunkList = null;
        List<String> fids = null;
        log.info("知识库总数量:{}",KnowledgeInfoVos.size());

        for(KnowledgeInfoVo vo:KnowledgeInfoVos){
            //不存在则创建
            if(!weaviateVectorStore.checkKnowledgeBaseExists(vo.getKid())){
                //创建 Schema
                log.info("知识库不存在，正在构建知识库："+vo.getKname());
                embeddingService.createSchema(vo.getKid());
            }

            //查询 KnowledgeFragment
            bo = new KnowledgeAttachBo();
            bo.setKid(vo.getKid());
            List<KnowledgeAttachVo> knowledgeAttachVos =  knowledgeAttachService.queryList(bo);
            //查询附件
            for(KnowledgeAttachVo attachVo:knowledgeAttachVos){
                //查询附件分组信息
                log.info("正在构建知识库附件："+attachVo.getDocName());
                fragmentBo = new KnowledgeFragmentBo();
                fragmentBo.setDocId(attachVo.getDocId());
                chunkList = new ArrayList<>();
                fids = new ArrayList<>();
                List<KnowledgeFragmentVo> knowledgeFragmentVoList =  knowledgeFragmentService.queryList(fragmentBo);

                List<Map<String, Object>>  mapList =   weaviateVectorStore.queryWeaviateByDocId(attachVo.getKid(),attachVo.getDocId());
                System.out.println("mapList size:"+mapList.size());
                log.info("知识库附件未创建向量数据库！");
                if(mapList.size() == 0){
                    for(KnowledgeFragmentVo knowledgeFragmentVo:knowledgeFragmentVoList){
                        //查询是否存在 不存在则插入
                        Map<String, Object>  hasMap =  weaviateVectorStore.queryWeaviateFullData( vo.getKid(),  knowledgeFragmentVo.getFid());
                        if(hasMap==null){
                            //不存在则插入片段
//                            log.info("正在插入片段：kid:{},docId:{},fid:{}",knowledgeFragmentVo.getKid(),knowledgeFragmentVo.getDocId(),knowledgeFragmentVo.getFid());
//                            embeddingService.saveFragment(knowledgeFragmentVo.getKid(),knowledgeFragmentVo.getDocId(),knowledgeFragmentVo.getFid(),knowledgeFragmentVo.getContent());
                            chunkList.add(knowledgeFragmentVo.getContent());
                            fids.add(knowledgeFragmentVo.getFid());
                        }

                    }
                    //
                   log.info("正在插入片段集合：kid:{},docId:{},fid:{}",attachVo.getKid(),attachVo.getDocId());

                    embeddingService.storeEmbeddings(chunkList,vo.getKid(),attachVo.getDocId(),fids);
                }else{
                    log.info("知识库附件已经创建向量数据库！");
//                    for(KnowledgeFragmentVo knowledgeFragmentVo:knowledgeFragmentVoList){
//                        //查询是否存在 不存在则插入
//                        Map<String, Object>  hasMap =  weaviateVectorStore.queryWeaviateFullData( vo.getKid(),  knowledgeFragmentVo.getFid());
//                        if(hasMap==null){
//                            //不存在则插入片段
//                            log.info("正在插入片段：kid:{},docId:{},fid:{}",knowledgeFragmentVo.getKid(),knowledgeFragmentVo.getDocId(),knowledgeFragmentVo.getFid());
//                            embeddingService.saveFragment(knowledgeFragmentVo.getKid(),knowledgeFragmentVo.getDocId(),knowledgeFragmentVo.getFid(),knowledgeFragmentVo.getContent());
//                        }
//
//                    }

                }

            }

        }



        return R.ok("恢复结束！");
    }



    public static boolean hasData(String jsonString,String classname) {
        try {
            // 解析 JSON 字符串
            JSONObject jsonObject = new JSONObject(jsonString);
            // 获取 "data" 对象
            JSONObject dataObject = jsonObject.getJSONObject("data");
            // 获取 "Get" 对象
            JSONObject getObject = dataObject.getJSONObject("Get");
            // 获取 "LocalKnowledgelitl075b0d" 数组
            JSONArray localKnowledgeArray = getObject.getJSONArray(classname);
            // 判断数组是否为空
            return localKnowledgeArray.length() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 如果解析出错，返回 false
        }
    }
}
