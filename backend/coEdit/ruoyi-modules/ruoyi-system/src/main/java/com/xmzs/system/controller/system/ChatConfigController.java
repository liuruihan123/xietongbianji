package com.xmzs.system.controller.system;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import cn.hutool.core.util.ObjectUtil;
import com.xmzs.system.service.IChatConfigService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.xmzs.common.web.core.BaseController;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.system.domain.vo.ChatConfigVo;
import com.xmzs.system.domain.bo.ChatConfigBo;

/**
 * 对话配置信息
 *
 * @author Lion Li
 * @date 2024-04-13
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/chat/config")
public class ChatConfigController extends BaseController {

    private final IChatConfigService chatConfigService;

    /**
     * 查询配置信息列表
     */
//    @GetMapping("/list")
//    public List<ChatConfigVo> list(ChatConfigBo bo) {
//        return chatConfigService.queryList(bo);
//    }

    @GetMapping("/list")
    public R<List<ChatConfigVo>> list(ChatConfigBo bo) {
        List<ChatConfigVo> configVoList = chatConfigService.queryList(bo);
        return  R.ok(configVoList);
    }

    /**
     * 获取对话配置信详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<ChatConfigVo> getInfo(@NotNull(message = "主键不能为空")
                                   @PathVariable Long id) {
        return R.ok(chatConfigService.queryById(id));
    }

    /**
     * 新增对话配置信息
     */
    @PostMapping("/add")
    public R<Void> add(@RequestBody List<ChatConfigBo> boList) {
        for (ChatConfigBo chatConfigBo : boList) {
            if(chatConfigBo.getId() == null){
                chatConfigService.insertByBo(chatConfigBo);
            }else {
                chatConfigService.updateByBo(chatConfigBo);
            }
        }
        return toAjax(true);
    }

    /**
     * 修改对话配置信息
     */
    @PutMapping("/edit")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ChatConfigBo bo) {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String ipAddress = localHost.getHostAddress();
            System.out.println("IP地址：" + ipAddress);
            if(ObjectUtil.isEmpty(bo.getUpdateIp())){
                bo.setUpdateIp(ipAddress);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return toAjax(chatConfigService.updateByBo(bo));
    }

    /**
     * 删除对话配置信息
     *
     * @param ids 主键串
     */
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(chatConfigService.deleteWithValidByIds(List.of(ids), true));
    }
}
