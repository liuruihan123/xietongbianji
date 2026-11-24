package com.xmzs.common.office.xlsx;

import org.apache.poi.ss.usermodel.DataFormatter;

import java.util.Locale;

/**
 * @Description : 数据序列化
 * @Author : zxy
 * @Date : 2023/10/30 14:31
 **/
public class XssfDataFormatter extends DataFormatter {

    public XssfDataFormatter(Locale locale) {
        super(locale);

    }

    public XssfDataFormatter() {
        this(Locale.getDefault());
    }


}
