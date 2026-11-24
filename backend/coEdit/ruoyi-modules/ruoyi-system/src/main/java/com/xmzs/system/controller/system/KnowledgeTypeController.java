package com.xmzs.system.controller.system;

import java.util.List;

import com.xmzs.common.excel.utils.ExcelUtil;
import com.xmzs.common.idempotent.annotation.RepeatSubmit;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.web.core.BaseController;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.system.domain.vo.KnowledgeTypeVo;
import com.xmzs.system.domain.bo.KnowledgeTypeBo;
import com.xmzs.system.service.IKnowledgeTypeService;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

/**
 * 知识分类
 *
 * @author zhuzi
 * @date 2024-06-22
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/type")
public class KnowledgeTypeController extends BaseController {

    private final IKnowledgeTypeService knowledgeTypeService;

    /**
     * 查询知识分类列表
     */
    @SaCheckPermission("system:type:list")
    @GetMapping("/list")
    public TableDataInfo<KnowledgeTypeVo> list(KnowledgeTypeBo bo, PageQuery pageQuery) {
        return knowledgeTypeService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出知识分类列表
     */
    @SaCheckPermission("system:type:export")
    @Log(title = "知识分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(KnowledgeTypeBo bo, HttpServletResponse response) {
        List<KnowledgeTypeVo> list = knowledgeTypeService.queryList(bo);
        ExcelUtil.exportExcel(list, "知识分类", KnowledgeTypeVo.class, response);
    }

    /**
     * 获取知识分类详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:type:query")
    @GetMapping("/{id}")
    public R<KnowledgeTypeVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(knowledgeTypeService.queryById(id));
    }

    /**
     * 新增知识分类
     */
    @SaCheckPermission("system:type:add")
    @Log(title = "知识分类", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody KnowledgeTypeBo bo) {
        return toAjax(knowledgeTypeService.insertByBo(bo));
    }

    /**
     * 修改知识分类
     */
    @SaCheckPermission("system:type:edit")
    @Log(title = "知识分类", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody KnowledgeTypeBo bo) {
        return toAjax(knowledgeTypeService.updateByBo(bo));
    }

    /**
     * 删除知识分类
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:type:remove")
    @Log(title = "知识分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(knowledgeTypeService.deleteWithValidByIds(List.of(ids), true));
    }
}
