package com.xmzs.knowledge.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.excel.utils.ExcelUtil;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.web.core.BaseController;
import com.xmzs.knowledge.common.annotation.RepeatSubmit;
import com.xmzs.knowledge.domain.bo.KnowledgeAttachBo;
import com.xmzs.knowledge.domain.vo.KnowledgeAttachVo;
import com.xmzs.knowledge.service.IKnowledgeAttachService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库文件
 *
 * @author zhuzi
 * @date 2024-06-27
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/knowledge/knowledgeAttach")
public class KnowledgeAttachController extends BaseController {

    private final IKnowledgeAttachService knowledgeAttachService;

    /**
     * 查询知识库文件列表
     */
    @SaCheckPermission("knowledge:knowledgeAttach:list")
    @GetMapping("/list")
    public TableDataInfo<KnowledgeAttachVo> list(KnowledgeAttachBo bo, PageQuery pageQuery) {
        return knowledgeAttachService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出知识库文件列表
     */
    @SaCheckPermission("knowledge:knowledgeAttach:export")
    @Log(title = "知识库文件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(KnowledgeAttachBo bo, HttpServletResponse response) {
        List<KnowledgeAttachVo> list = knowledgeAttachService.queryList(bo);
        ExcelUtil.exportExcel(list, "知识库文件", KnowledgeAttachVo.class, response);
    }

    /**
     * 获取知识库文件详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("knowledge:knowledgeAttach:query")
    @GetMapping("/{id}")
    public R<KnowledgeAttachVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(knowledgeAttachService.queryById(id));
    }

    /**
     * 新增知识库文件
     */
    @SaCheckPermission("knowledge:knowledgeAttach:add")
    @Log(title = "知识库文件", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody KnowledgeAttachBo bo) {
        return toAjax(knowledgeAttachService.insertByBo(bo));
    }

    /**
     * 修改知识库文件
     */
    @SaCheckPermission("knowledge:knowledgeAttach:edit")
    @Log(title = "知识库文件", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody KnowledgeAttachBo bo) {
        return toAjax(knowledgeAttachService.updateByBo(bo));
    }

    /**
     * 删除知识库文件
     *
     * @param ids 主键串
     */
    @SaCheckPermission("knowledge:knowledgeAttach:remove")
    @Log(title = "知识库文件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(knowledgeAttachService.deleteWithValidByIds(List.of(ids), true));
    }
}
