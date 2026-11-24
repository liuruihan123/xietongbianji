package com.xmzs.common.core.domain.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * packageName com.xmzs.common.core
 *
 * @author zhuzi
 * @date 2024/11/27
 * @description TODO
 */
@Data
@NoArgsConstructor
public class LoginScanValidate implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    @NotNull(message = "code参数缺失")
    @NotEmpty(message = "code不能为空")
    private String code;

    @NotNull(message = "二维码已失效或不存在,请重新操作")
    @NotEmpty(message = "二维码已失效或不存在,请重新操作")
    private String state;
}
