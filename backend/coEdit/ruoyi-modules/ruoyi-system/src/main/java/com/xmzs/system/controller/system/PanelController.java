package com.xmzs.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.web.core.BaseController;
import com.xmzs.system.domain.vo.PaymentOrdersVo;
import com.xmzs.system.service.IPanelService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * packageName com.xmzs.system.controller.system
 *
 * @author zhuzi
 * @date 2024/12/16
 * @description 面板分析页面
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/panel")
public class PanelController extends BaseController {

    private final IPanelService panelService;

    /**
     * 获取仪表盘卡片数据
     *
     * @param params
     *       * params-> type      today, week, month, year 或 free（代表根据 beginTime、endTime 计算时间）
     *      * params-> beginTime 开始时间
     *      * params-> endTime   结束时间
     */
    @GetMapping("/cardData")
    public R<Map<String, Object>> getCardData(@RequestParam Map<String, String> params) {
        String type = params.get("type");
        String beginTime = params.get("beginTime");
        String endTime = params.get("endTime");

        Map<String,Object> resMap = panelService.getHeaderCountInfo(type,beginTime,endTime);
        return R.ok(resMap);
    }
}
