package com.xmzs.knowledge.controller;

import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.idempotent.annotation.RepeatSubmit;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.web.core.BaseController;
import com.xmzs.knowledge.common.ResultData;
import com.xmzs.knowledge.common.annotation.AdminRequired;
import com.xmzs.knowledge.domain.Knowledge;
import com.xmzs.knowledge.domain.KnowledgeAttach;
import com.xmzs.knowledge.domain.KnowledgeFragment;
import com.xmzs.knowledge.domain.bo.KnowledgeInfoBo;
import com.xmzs.knowledge.domain.vo.KnowledgeInfoVo;
import com.xmzs.knowledge.request.*;
import com.xmzs.knowledge.service.IKnowledgeAttachService;
import com.xmzs.knowledge.service.KnowledgeFragmentService;
import com.xmzs.knowledge.service.KnowledgeService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地知识库
 */
@RestController
@AllArgsConstructor
@RequestMapping("knowledge")
public class KnowledgeController extends BaseController {

    private final KnowledgeService knowledgeService;
    private final KnowledgeFragmentService knowledgeFragmentService;
    private final IKnowledgeAttachService knowledgeAttachService;

    /**
     * 保存知识库基本信息
     * @param request
     * @return
     */
    @PostMapping(value = "save")
    @AdminRequired
    public R<String> save(@RequestBody KnowledgeSaveRequest request){
        knowledgeService.saveOne(request,knowledgeService.getLoginUser().getUserId());
        return R.ok("保存知识库基本信息成功!");
    }

    /**
     * 修改知识库基本信息
     */
    @Log(title = "修改知识", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping(value = "update")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody KnowledgeInfoBo bo) {
        return toAjax(knowledgeService.updateByBo(bo));
    }
    /**
     * 上传知识库附件
     * @param request
     * @return
     */
    @PostMapping(value = "attach/upload")
    @AdminRequired
    public R<String> upload(KnowledgeUploadRequest request){
        knowledgeService.upload(request);
        return R.ok("上传知识库附件成功!");
    }

    /**
     * 查询个人所有知识库
     * @param
     * @return
     */
    @GetMapping("list")
    public List<Knowledge> list(){
        Map<String,Object> map = new HashMap<>();
        //无需要个人
//        map.put("uid",knowledgeService.getLoginUser().getUserId());
        return knowledgeService.listByMap(map);
    }

    /**
     * 查询所有知识库
     * @return
     */
    @GetMapping("all")
    public TableDataInfo<KnowledgeInfoVo> all(KnowledgeInfoBo knowledgeInfoBo, PageQuery pageQuery){
//        Map<String,Object> map = new HashMap<>();
//        if(!ObjectUtils.isEmpty(typeId)){
//            map.put("type_id",typeId);
//        }
//        return knowledgeService.listByMap(map);
        return knowledgeService.selectPageKnowledgeList(knowledgeInfoBo,pageQuery);
    }


    /**
     * 获取知识分类详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<KnowledgeInfoVo> detail(@NotNull(message = "主键不能为空")
                                      @PathVariable Long id) {
        KnowledgeInfoBo knowledgeInfoBo = new KnowledgeInfoBo();
//        knowledgeInfoBo.setId(id);
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        knowledgeInfoBo.setParams(map);
        PageQuery pageQuery =new PageQuery();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(1);
//        return R.ok(knowledgeService.queryById(id));
        TableDataInfo<KnowledgeInfoVo> tableDataInfo =  knowledgeService.selectPageKnowledgeList(knowledgeInfoBo,pageQuery);
        KnowledgeInfoVo infoVo = tableDataInfo.getRows().get(0);
        return R.ok(infoVo);
    }

    /**
     * 查询知识附件
     * @return
     */
    @GetMapping("detail/{kid}")
    public List<KnowledgeAttach> detail(@PathVariable(name = "kid") String kid){
        Map<String,Object> map = new HashMap<>();
        map.put("kid",kid);
        return knowledgeAttachService.listByMap(map);
    }

    @PostMapping("attach/remove")
    @AdminRequired
    public R<String> removeAttach(@RequestBody KnowledgeAttachRemoveRequest request){
        knowledgeService.removeAttach(request);
        return R.ok("删除知识库附件成功!");
    }

    @PostMapping("remove")
    public R<String> remove(@RequestBody KnowledgeRemoveRequest request){
        knowledgeService.removeKnowledge(request);
        return R.ok("删除知识库成功!");
    }

    /**
     * 查询知识片段
     *
     * @param docId
     * @return
     */
    @GetMapping("fragment/list/{docId}")
    public List<KnowledgeFragment> fragmentList(@PathVariable(name = "docId") String docId){
        Map<String,Object> map = new HashMap<>();
        map.put("doc_id",docId);
        return knowledgeFragmentService.listByMap(map);
    }

    @PostMapping("fragment/remove")
    @AdminRequired
    public ResultData fragmentRemove(@RequestBody KnowledgeFragmentRemoveRequest request){
        knowledgeFragmentService.removeFragment(request);
        return ResultData.success("删除知识片段成功");
    }

    @PostMapping("fragment/save")
    @AdminRequired
    public ResultData fragmentSave(@RequestBody KnowledgeFragmentSaveRequest request){
        knowledgeFragmentService.saveFragment(request);
        return ResultData.success("保存知识片段成功");
    }
}
