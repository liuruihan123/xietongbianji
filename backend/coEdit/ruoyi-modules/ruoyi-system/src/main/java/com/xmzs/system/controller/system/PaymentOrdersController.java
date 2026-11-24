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
import com.xmzs.system.domain.vo.PaymentOrdersVo;
import com.xmzs.system.domain.bo.PaymentOrdersBo;
import com.xmzs.system.service.IPaymentOrdersService;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

/**
 * 支付订单
 *
 * @author Lion Li
 * @date 2024-04-16
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/orders")
public class PaymentOrdersController extends BaseController {

    private final IPaymentOrdersService paymentOrdersService;

    /**
     * 查询支付订单列表
     */
//    @SaCheckPermission("system:orders:list")
    @GetMapping("/list")
    public TableDataInfo<PaymentOrdersVo> list(PaymentOrdersBo bo, PageQuery pageQuery) {
        return paymentOrdersService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出支付订单列表
     */
    @SaCheckPermission("system:orders:export")
    @Log(title = "支付订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PaymentOrdersBo bo, HttpServletResponse response) {
        List<PaymentOrdersVo> list = paymentOrdersService.queryList(bo);
        ExcelUtil.exportExcel(list, "支付订单", PaymentOrdersVo.class, response);
    }

    /**
     * 获取支付订单详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:orders:query")
    @GetMapping("/{id}")
    public R<PaymentOrdersVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(paymentOrdersService.queryById(id));
    }

    /**
     * 新增支付订单
     */
    @SaCheckPermission("system:orders:add")
    @Log(title = "支付订单", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PaymentOrdersBo bo) {
        return toAjax(paymentOrdersService.insertByBo(bo));
    }

    /**
     * 修改支付订单
     */
    @SaCheckPermission("system:orders:edit")
    @Log(title = "支付订单", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PaymentOrdersBo bo) {
        return toAjax(paymentOrdersService.updateByBo(bo));
    }

    /**
     * 删除支付订单
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:orders:remove")
    @Log(title = "支付订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(paymentOrdersService.deleteWithValidByIds(List.of(ids), true));
    }
}
