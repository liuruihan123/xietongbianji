package com.xmzs.common.office.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
public class Templates implements Serializable {
    private static final long serialVersionUID = 1L;

    //标题
    private String title;
    //基本信息1
//    private String basicInfoOne;
    //富文本1
    private String richtext;



}
