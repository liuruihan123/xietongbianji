package com.xmzs.common.core.domain.model;

import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 短信登录对象
 *
 * @author Lion Li
 */

@Data
public class LanguageBody {

    /**
     * 配置项
     */
    @NotBlank(message = "参数不能为空")
    @Pattern(regexp = "service_cn|privacy_cn|service_en|privacy_en|about_cn|about_en|question_cn|question_en", message = "该内容不被授权使用")
    private String configName;


}
