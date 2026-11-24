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
import com.xmzs.system.domain.vo.ChatMessageVo;
import com.xmzs.system.domain.bo.ChatMessageBo;
import com.xmzs.system.service.IChatMessageService;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

/**
 * 聊天消息
 *
 * @author Lion Li
 * @date 2024-04-16
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/message")
public class ChatMessageController extends BaseController {

    private final IChatMessageService chatMessageService;

    /**
     * 查询聊天消息列表
     */
    @SaCheckPermission("system:message:list")
    @GetMapping("/list")
    public TableDataInfo<ChatMessageVo> list(ChatMessageBo bo, PageQuery pageQuery) {
        return chatMessageService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出聊天消息列表
     */
    @SaCheckPermission("system:message:export")
    @Log(title = "聊天消息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ChatMessageBo bo, HttpServletResponse response) {
        List<ChatMessageVo> list = chatMessageService.queryList(bo);
        ExcelUtil.exportExcel(list, "聊天消息", ChatMessageVo.class, response);
    }

    /**
     * 获取聊天消息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:message:query")
    @GetMapping("/{id}")
    public R<ChatMessageVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(chatMessageService.queryById(id));
    }

    /**
     * 新增聊天消息
     */
    @SaCheckPermission("system:message:add")
    @Log(title = "聊天消息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ChatMessageBo bo) {
        return toAjax(chatMessageService.insertByBo(bo));
    }

    /**
     * 修改聊天消息
     */
    @SaCheckPermission("system:message:edit")
    @Log(title = "聊天消息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ChatMessageBo bo) {
        return toAjax(chatMessageService.updateByBo(bo));
    }

    /**
     * 删除聊天消息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:message:remove")
    @Log(title = "聊天消息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(chatMessageService.deleteWithValidByIds(List.of(ids), true));
    }
}
