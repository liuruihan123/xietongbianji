package com.xmzs.system.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.xmzs.system.domain.AiContactus;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.common.excel.annotation.ExcelDictFormat;
import com.xmzs.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * contactus视图对象 ai_contactus
 *
 * @author zuleed
 * @date 2024-12-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiContactus.class)
public class AiContactusVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @ExcelProperty(value = "编号")
    private Long id;

    /**
     * 渠道信息
     */
    @ExcelProperty(value = "渠道信息")
    private String channel;

    /**
     * 用户留言
     */
    @ExcelProperty(value = "用户留言")
    private String userMessage;

    /**
     * 创建人账号
     */
    @ExcelProperty(value = "创建人账号")
    private String createdUserName;

    /**
     * 创建人
     */
    @ExcelProperty(value = "创建人")
    private String createdBy;


    /**
     * 创建时间
     */
    private Date createTime;
}
