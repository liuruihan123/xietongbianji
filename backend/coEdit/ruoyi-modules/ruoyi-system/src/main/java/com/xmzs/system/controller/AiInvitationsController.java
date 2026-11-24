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
import com.xmzs.system.domain.vo.AiInvitationsVo;
import com.xmzs.system.domain.bo.AiInvitationsBo;
import com.xmzs.system.service.IAiInvitationsService;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

/**
 * 邀请管理
 *
 * @author zuleed
 * @date 2024-12-11
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/invitations")
public class AiInvitationsController extends BaseController {

    private final IAiInvitationsService aiInvitationsService;

    /**
     * 查询邀请管理列表
     */
    @SaCheckPermission("system:invitations:list")
    @GetMapping("/list")
    public TableDataInfo<AiInvitationsVo> list(AiInvitationsBo bo, PageQuery pageQuery) {
        return aiInvitationsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出邀请管理列表
     */
    @SaCheckPermission("system:invitations:export")
    @Log(title = "邀请管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AiInvitationsBo bo, HttpServletResponse response) {
        List<AiInvitationsVo> list = aiInvitationsService.queryList(bo);
        ExcelUtil.exportExcel(list, "邀请管理", AiInvitationsVo.class, response);
    }

    /**
     * 获取邀请管理详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:invitations:query")
    @GetMapping("/{id}")
    public R<AiInvitationsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(aiInvitationsService.queryById(id));
    }

    /**
     * 新增邀请管理
     */
    @SaCheckPermission("system:invitations:add")
    @Log(title = "邀请管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AiInvitationsBo bo) {
        return toAjax(aiInvitationsService.insertByBo(bo));
    }

    /**
     * 修改邀请管理
     */
    @SaCheckPermission("system:invitations:edit")
    @Log(title = "邀请管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AiInvitationsBo bo) {
        return toAjax(aiInvitationsService.updateByBo(bo));
    }

    /**
     * 删除邀请管理
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:invitations:remove")
    @Log(title = "邀请管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(aiInvitationsService.deleteWithValidByIds(List.of(ids), true));
    }
}
