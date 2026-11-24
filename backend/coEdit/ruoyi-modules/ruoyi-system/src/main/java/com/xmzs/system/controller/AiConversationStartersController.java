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
import com.xmzs.system.domain.vo.AiConversationStartersVo;
import com.xmzs.system.domain.bo.AiConversationStartersBo;
import com.xmzs.system.service.IAiConversationStartersService;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

/**
 * 对话开场
 *
 * @author zuleed
 * @date 2024-11-26
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/conversationStarters")
public class AiConversationStartersController extends BaseController {

    private final IAiConversationStartersService aiConversationStartersService;




    /**
     * 查询对话开场列表
     */
    @GetMapping("/list")
    public TableDataInfo<AiConversationStartersVo> list(AiConversationStartersBo bo, PageQuery pageQuery) {
        return aiConversationStartersService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出对话开场列表
     */
    @SaCheckPermission("system:conversationStarters:export")
    @Log(title = "对话开场", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AiConversationStartersBo bo, HttpServletResponse response) {
        List<AiConversationStartersVo> list = aiConversationStartersService.queryList(bo);
        ExcelUtil.exportExcel(list, "对话开场", AiConversationStartersVo.class, response);
    }

    /**
     * 获取对话开场详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:conversationStarters:query")
    @GetMapping("/{id}")
    public R<AiConversationStartersVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(aiConversationStartersService.queryById(id));
    }

    /**
     * 新增对话开场
     */
    @SaCheckPermission("system:conversationStarters:add")
    @Log(title = "对话开场", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AiConversationStartersBo bo) {
        return toAjax(aiConversationStartersService.insertByBo(bo));
    }

    /**
     * 修改对话开场
     */
    @SaCheckPermission("system:conversationStarters:edit")
    @Log(title = "对话开场", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AiConversationStartersBo bo) {
        return toAjax(aiConversationStartersService.updateByBo(bo));
    }

    /**
     * 删除对话开场
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:conversationStarters:remove")
    @Log(title = "对话开场", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(aiConversationStartersService.deleteWithValidByIds(List.of(ids), true));
    }
}
