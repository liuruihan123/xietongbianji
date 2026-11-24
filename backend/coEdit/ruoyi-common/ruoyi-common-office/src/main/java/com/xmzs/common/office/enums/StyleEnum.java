package com.xmzs.common.office.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 标签枚举  博主认为代码里尽量不要出现魔法值，即使是一个，也用枚举去定义下
 * @Version: 1.0
 */
@Getter
@AllArgsConstructor
public enum StyleEnum {

    WIDTH("width"),
    HEIGHT("height"),
    FILE_URL("fileUrl"),
    NEW_FILE_URL("newFileUrl"),
    //word最大宽值
    MAX_WIDTH("650.0");

    private final String value;
}
