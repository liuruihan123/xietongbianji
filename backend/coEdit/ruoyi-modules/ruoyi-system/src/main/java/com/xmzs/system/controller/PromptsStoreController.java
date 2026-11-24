package com.xmzs.system.controller;

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
import com.xmzs.system.domain.vo.PromptsStoreVo;
import com.xmzs.system.domain.bo.PromptsStoreBo;
import com.xmzs.system.service.IPromptsStoreService;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

/**
 * 提示词商店
 *
 * @author zuleed
 * @date 2024-10-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/promptsStore")
public class PromptsStoreController extends BaseController {

    private final IPromptsStoreService promptsStoreService;

    /**
     * 查询提示词商店列表
     */
    @SaCheckPermission("system:promptsStore:list")
    @GetMapping("/list")
    public TableDataInfo<PromptsStoreVo> list(PromptsStoreBo bo, PageQuery pageQuery) {
        return promptsStoreService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出提示词商店列表
     */
    @SaCheckPermission("system:promptsStore:export")
    @Log(title = "提示词商店", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PromptsStoreBo bo, HttpServletResponse response) {
        List<PromptsStoreVo> list = promptsStoreService.queryList(bo);
        ExcelUtil.exportExcel(list, "提示词商店", PromptsStoreVo.class, response);
    }

    /**
     * 获取提示词商店详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:promptsStore:query")
    @GetMapping("/{id}")
    public R<PromptsStoreVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Integer id) {
        return R.ok(promptsStoreService.queryById(id));
    }

    /**
     * 新增提示词商店
     */
    @SaCheckPermission("system:promptsStore:add")
    @Log(title = "提示词商店", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PromptsStoreBo bo) {
        return toAjax(promptsStoreService.insertByBo(bo));
    }

    /**
     * 修改提示词商店
     */
    @SaCheckPermission("system:promptsStore:edit")
    @Log(title = "提示词商店", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PromptsStoreBo bo) {
        return toAjax(promptsStoreService.updateByBo(bo));
    }

    /**
     * 删除提示词商店
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:promptsStore:remove")
    @Log(title = "提示词商店", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Integer[] ids) {
        return toAjax(promptsStoreService.deleteWithValidByIds(List.of(ids), true));
    }
}
