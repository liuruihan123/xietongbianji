package com.xmzs.system.controller;

import java.util.List;

import cn.hutool.core.util.ObjectUtil;
import com.xmzs.common.excel.utils.ExcelUtil;
import com.xmzs.common.idempotent.annotation.RepeatSubmit;
import com.xmzs.system.domain.vo.AiQaTemplateVo;
import com.xmzs.system.service.IAiQaTemplateService;
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
import com.xmzs.system.domain.vo.ModelSettingVo;
import com.xmzs.system.domain.bo.ModelSettingBo;
import com.xmzs.system.service.IModelSettingService;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

/**
 * 功能参数
 *
 * @author zhuzi
 * @date 2024-08-27
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/modelsetting")
public class ModelSettingController extends BaseController {

    private final IModelSettingService modelSettingService;

    private final IAiQaTemplateService aiQaTemplateService;

    /**
     * 查询功能参数列表
     */
    @SaCheckPermission("system:modelsetting:list")
    @GetMapping("/list")
    public TableDataInfo<ModelSettingVo> list(ModelSettingBo bo, PageQuery pageQuery) {
        return modelSettingService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出功能参数列表
     */
    @SaCheckPermission("system:modelsetting:export")
    @Log(title = "功能参数", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ModelSettingBo bo, HttpServletResponse response) {
        List<ModelSettingVo> list = modelSettingService.queryList(bo);
        ExcelUtil.exportExcel(list, "功能参数", ModelSettingVo.class, response);
    }


    /**
     * 获取问答模版详细信息
     *
     * @param qaid 主键
     */
    @GetMapping("/checktemplate/{qaid}")
    public R<Void> getSelQaInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long qaid) {
        AiQaTemplateVo aiQaTemplateVo = aiQaTemplateService.queryById(qaid);
        if(!ObjectUtil.isNull(aiQaTemplateVo)){
          boolean hasDatatem =  aiQaTemplateVo.getTemplateContent().contains("{{data}}");
          if(hasDatatem){
              return R.fail("模版中包含{{data}} ,非知识库不可选择带有{{data}} 变量 ");
          }
        }else{
            return R.fail("请检查模版是否存在");
        }
        return R.ok();
    }


    /**
     * 获取功能参数详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:modelsetting:query")
    @GetMapping("/{id}")
    public R<ModelSettingVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Integer id) {
        return R.ok(modelSettingService.queryById(id));
    }

    /**
     * 新增功能参数
     */
    @SaCheckPermission("system:modelsetting:add")
    @Log(title = "功能参数", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ModelSettingBo bo) {
        return toAjax(modelSettingService.insertByBo(bo));
    }

    /**
     * 修改功能参数
     */
    @SaCheckPermission("system:modelsetting:edit")
    @Log(title = "功能参数", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ModelSettingBo bo) {
        return toAjax(modelSettingService.updateByBo(bo));
    }

    /**
     * 删除功能参数
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:modelsetting:remove")
    @Log(title = "功能参数", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Integer[] ids) {
        return toAjax(modelSettingService.deleteWithValidByIds(List.of(ids), true));
    }
}
