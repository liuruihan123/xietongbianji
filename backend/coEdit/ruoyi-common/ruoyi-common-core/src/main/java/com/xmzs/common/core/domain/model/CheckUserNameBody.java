package com.xmzs.common.core.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户注册对象
 *
 * @author Lion Li
 */
@Data
public class CheckUserNameBody {

    /**
     * 用户名
     */
    @NotBlank(message = "{user.userName.not.blank}")
    // @Length(min = UserConstants.USERNAME_MIN_LENGTH, max = UserConstants.USERNAME_MAX_LENGTH, message = "{user.userName.length.valid}")
    private String userName;

}
