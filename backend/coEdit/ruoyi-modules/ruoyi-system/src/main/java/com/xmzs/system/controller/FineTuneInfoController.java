package com.xmzs.system.controller;

import java.util.List;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.xmzs.common.excel.utils.ExcelUtil;
import com.xmzs.common.idempotent.annotation.RepeatSubmit;
import com.xmzs.system.domain.FineTuneAttach;
import com.xmzs.system.domain.bo.FineTuneAttachBo;
import com.xmzs.system.domain.bo.SysRoleBo;
import com.xmzs.system.domain.request.FineTuneAttachRequest;
import com.xmzs.system.domain.vo.FineTuneAttachVo;
import com.xmzs.system.domain.vo.SysOssUploadVo;
import com.xmzs.system.domain.vo.SysOssVo;
import com.xmzs.system.service.ISysOssService;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.web.core.BaseController;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.system.domain.vo.FineTuneInfoVo;
import com.xmzs.system.domain.bo.FineTuneInfoBo;
import com.xmzs.system.service.IFineTuneInfoService;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

/**
 * 精调管理
 *
 * @author zhuzi
 * @date 2024-07-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/tuneInfo")
public class FineTuneInfoController extends BaseController {

    private final IFineTuneInfoService fineTuneInfoService;
    private final ISysOssService ossService;

    /**
     * 查询精调管理列表
     */
    @SaCheckPermission("system:tuneInfo:list")
    @GetMapping("/list")
    public TableDataInfo<FineTuneInfoVo> list(FineTuneInfoBo bo, PageQuery pageQuery) {
        return fineTuneInfoService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出精调管理列表
     */
    @SaCheckPermission("system:tuneInfo:export")
    @Log(title = "精调管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(FineTuneInfoBo bo, HttpServletResponse response) {
        List<FineTuneInfoVo> list = fineTuneInfoService.queryList(bo);
        ExcelUtil.exportExcel(list, "精调管理", FineTuneInfoVo.class, response);
    }

    /**
     * 获取精调管理详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:tuneInfo:query")
    @GetMapping("/{id}")
    public R<FineTuneInfoVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Integer id) {
        return R.ok(fineTuneInfoService.queryById(id));
    }

    /**
     * 新增精调管理
     */
    @SaCheckPermission("system:tuneInfo:add")
    @Log(title = "精调管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<FineTuneInfoBo> add( @RequestBody FineTuneInfoBo bo) {
        Long userId = fineTuneInfoService.getLoginUser().getUserId();
        String ft_id = RandomUtil.randomString(10);
        bo.setFtId(ft_id);
        bo.setUid(userId);
        FineTuneInfoBo fbo =  fineTuneInfoService.insertByBo(bo);
        return R.ok(fbo);
//        return toAjax(fineTuneInfoService.insertByBo(bo));
    }

    /**
     * 修改精调管理
     */
    @SaCheckPermission("system:tuneInfo:edit")
    @Log(title = "精调管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody FineTuneInfoBo bo) {
        return toAjax(fineTuneInfoService.updateByBo(bo));
    }

    /**
     * 删除精调管理
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:tuneInfo:remove")
    @Log(title = "精调管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(fineTuneInfoService.deleteWithValidByIds(List.of(ids), true));
    }



    /**
     * 使用状态修改
     */
    @Log(title = "修改状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeUseStatus")
    public R<Void> changeStatus(@RequestBody FineTuneInfoBo bo) {
        return toAjax(fineTuneInfoService.updateUseStatus(bo.getId(), bo.getUseStatus()));
    }

    @Log(title = "转化内容", businessType = BusinessType.UPDATE)
    @PutMapping("/fileContentToFtContent")
    public R<Void> fileContentToFtContent(@RequestBody FineTuneAttach bo) {
        return toAjax(fineTuneInfoService.fileContentToFtContent(bo.getId(),bo.getPromptsStoreId()));
    }

    @Log(title = "修改转化内容", businessType = BusinessType.UPDATE)
    @PutMapping("/updateFtContent")
    public R<Void> updateFtContent(@RequestBody FineTuneAttach bo) {
        return toAjax(fineTuneInfoService.updateFtContent(bo.getId(), bo.getFtContent()));
    }


    @Log(title = "检测转化内容", businessType = BusinessType.OTHER)
    @PutMapping("/checkFtContent")
    public R<Void> checkFtContent(@RequestBody FineTuneAttach bo) {
        return toAjax(fineTuneInfoService.getContentCheck(bo.getId(), bo.getFtContent()));
    }






//    createFinetuneModel

    @Log(title = "上传微调内容", businessType = BusinessType.UPDATE)
    @PutMapping("/uploadFinetuneData")
    public R<FineTuneAttachBo> uploadFinetuneData(@RequestBody FineTuneAttach bo) {
        FineTuneAttachBo fineTuneAttachBo = fineTuneInfoService.createFinetuneModel(bo.getFtId(), bo.getId());
        return R.ok(fineTuneAttachBo);
    }





    @Log(title = "获取附件状态中..", businessType = BusinessType.OTHER)
        @PutMapping("/getAttachStatus")
    public R<FineTuneAttachVo> getAttachStatus(@RequestBody FineTuneAttach bo) {
        FineTuneAttachVo fineTuneAttachVo = fineTuneInfoService.getAttachStatus(bo.getId());
        return R.ok(fineTuneAttachVo);
    }


    @Log(title = "获取附件微调状态中..", businessType = BusinessType.OTHER)
    @PutMapping("/getAttachFtStatus")
    public R<Integer> getAttachFtStatus(@RequestBody FineTuneAttach bo) {
        int  hasNum = fineTuneInfoService.getTraningStatus(bo.getFtId());
        return R.ok(hasNum);
    }



//    @Log(title = "上传微调文件", businessType = BusinessType.INSERT)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<String> upload(FineTuneAttachRequest request){
        if (ObjectUtil.isNull(request.getFile())) {
            return R.fail("上传文件不能为空");
        }

        fineTuneInfoService.upload(request);
        return R.ok("上传数据成功!");
    }

}
