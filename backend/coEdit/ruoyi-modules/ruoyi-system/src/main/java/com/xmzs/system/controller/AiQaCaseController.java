package com.xmzs.system.controller;

import java.util.List;
import com.xmzs.common.excel.utils.ExcelUtil;
import com.xmzs.common.idempotent.annotation.RepeatSubmit;
import com.xmzs.knowledge.common.annotation.AdminRequired;
import com.xmzs.knowledge.request.KnowledgeUploadRequest;
import com.xmzs.system.domain.request.UploadAttachRequest;
import com.xmzs.system.domain.vo.QaCaseTreeNode;
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
import com.xmzs.system.domain.vo.AiQaCaseVo;
import com.xmzs.system.domain.bo.AiQaCaseBo;
import com.xmzs.system.service.IAiQaCaseService;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

/**
 * 对话案例
 *
 * @author zuleed
 * @date 2024-12-27
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/qaCase")
public class AiQaCaseController extends BaseController {

    private final IAiQaCaseService aiQaCaseService;

    /**
     * 查询对话案例列表
     */
    @SaCheckPermission("system:qaCase:list")
    @GetMapping("/list")
    public TableDataInfo<AiQaCaseVo> list(AiQaCaseBo bo, PageQuery pageQuery) {
        return aiQaCaseService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询对话案例列表
     */
    @SaCheckPermission("system:qaCase:list")
    @GetMapping("/getTreelist")
    public List<QaCaseTreeNode> treelist() {
        return aiQaCaseService.getTreeData();
    }


    /**
     * 导入案例附件
     * @param request
     * @return
     */
    @PostMapping(value = "/attach/upload")
    @AdminRequired
    public R<String> upload(UploadAttachRequest request){
        aiQaCaseService.upload(request);
        return R.ok("上传案例附件成功!");
    }
    /**
     * 导出对话案例列表
     */
    @SaCheckPermission("system:qaCase:export")
    @Log(title = "对话案例", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AiQaCaseBo bo, HttpServletResponse response) {
        List<AiQaCaseVo> list = aiQaCaseService.queryList(bo);
        ExcelUtil.exportExcel(list, "对话案例", AiQaCaseVo.class, response);
    }

    /**
     * 获取对话案例详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:qaCase:query")
    @GetMapping("/{id}")
    public R<AiQaCaseVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(aiQaCaseService.queryById(id));
    }

    /**
     * 新增对话案例
     */
    @SaCheckPermission("system:qaCase:add")
    @Log(title = "对话案例", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AiQaCaseBo bo) {
        return toAjax(aiQaCaseService.insertByBo(bo));
    }

    /**
     * 修改对话案例
     */
    @SaCheckPermission("system:qaCase:edit")
    @Log(title = "对话案例", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AiQaCaseBo bo) {
        return toAjax(aiQaCaseService.updateByBo(bo));
    }

    /**
     * 删除对话案例
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:qaCase:remove")
    @Log(title = "对话案例", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(aiQaCaseService.deleteWithValidByIds(List.of(ids), true));
    }
}
