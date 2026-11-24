package com.xmzs.controller;

import java.util.List;

import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.system.request.RoleListDto;
import com.xmzs.system.request.RoleRequest;
import com.xmzs.system.request.SimpleGenerateRequest;
import com.xmzs.system.response.SimpleGenerateDataResponse;
import com.xmzs.system.response.rolelist.RoleListVO;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.web.core.BaseController;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.system.domain.vo.VoiceRoleVo;
import com.xmzs.system.domain.bo.VoiceRoleBo;
import com.xmzs.system.service.IVoiceRoleService;

/**
 * 配音角色
 *
 * @author Lion Li
 * @date 2024-03-19
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/voice")
public class VoiceController extends BaseController {

    private final IVoiceRoleService voiceRoleService;

    /**
     * 查询配音角色列表
     */
    @GetMapping("/list")
    public List<VoiceRoleVo> list(VoiceRoleBo bo) {
        bo.setCreateBy(LoginHelper.getUserId());
        return voiceRoleService.queryList(bo);
    }

    /**
     * 获取配音角色详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:role:query")
    @GetMapping("/{id}")
    public R<VoiceRoleVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(voiceRoleService.queryById(id));
    }

    /**
     * 新增配音角色
     */
    @Log(title = "配音角色", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<Void> add(@RequestBody RoleRequest roleRequest) {
        return toAjax(voiceRoleService.insertByBo(roleRequest));
    }

    /**
     * 修改配音角色
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "配音角色", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody VoiceRoleBo bo) {
        return toAjax(voiceRoleService.updateByBo(bo));
    }

    /**
     * 删除配音角色
     *
     * @param ids 主键串
     */
    @Log(title = "配音角色", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(voiceRoleService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 实时语音生成
     */
    @PostMapping("/simpleGenerate")
    public R<SimpleGenerateDataResponse> simpleGenerate(@RequestBody SimpleGenerateRequest simpleGenerateRequest) {
        return R.ok(voiceRoleService.simpleGenerate(simpleGenerateRequest));
    }

    /**
     * 角色市场
     */
    @GetMapping("/roleList")
    public R<List<RoleListVO>> roleList() {
        return R.ok(voiceRoleService.roleList());
    }

    /**
     * 收藏角色
     */
    @PostMapping("/copyRole")
    public R<String> copyRole(@RequestBody RoleListDto roleListDto) {
        voiceRoleService.copyRole(roleListDto);
        return R.ok();
    }
}
