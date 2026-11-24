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
import com.xmzs.knowledge.domain.bo.KnowledgeFragmentBo;
import com.xmzs.knowledge.domain.vo.KnowledgeFragmentVo;
import com.xmzs.knowledge.service.KnowledgeFragmentService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识片段
 *
 * @author zhuzi
 * @date 2024-06-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/knowledge/fragment")
public class KnowledgeFragmentController extends BaseController {

    private final KnowledgeFragmentService knowledgeFragmentService;

    /**
     * 查询知识片段列表
     */
    @SaCheckPermission("knowledge:fragment:list")
    @GetMapping("/list")
    public TableDataInfo<KnowledgeFragmentVo> list(KnowledgeFragmentBo bo, PageQuery pageQuery) {
        return knowledgeFragmentService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出知识片段列表
     */
    @SaCheckPermission("knowledge:fragment:export")
    @Log(title = "知识片段", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(KnowledgeFragmentBo bo, HttpServletResponse response) {
        List<KnowledgeFragmentVo> list = knowledgeFragmentService.queryList(bo);
        ExcelUtil.exportExcel(list, "知识片段", KnowledgeFragmentVo.class, response);
    }

    /**
     * 获取知识片段详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("knowledge:fragment:query")
    @GetMapping("/{id}")
    public R<KnowledgeFragmentVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(knowledgeFragmentService.queryById(id));
    }

    /**
     * 新增知识片段
     */
    @SaCheckPermission("knowledge:fragment:add")
    @Log(title = "知识片段", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody KnowledgeFragmentBo bo) {
        return toAjax(knowledgeFragmentService.insertByBo(bo));
    }

    /**
     * 修改知识片段
     */
    @SaCheckPermission("knowledge:fragment:edit")
    @Log(title = "知识片段", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody KnowledgeFragmentBo bo) {
        return toAjax(knowledgeFragmentService.updateByBo(bo));
    }

    /**
     * 删除知识片段
     *
     * @param ids 主键串
     */
    @SaCheckPermission("knowledge:fragment:remove")
    @Log(title = "知识片段", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(knowledgeFragmentService.deleteWithValidByIds(List.of(ids), true));
    }
}
