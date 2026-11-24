package com.xmzs.system.domain.vo;

import com.xmzs.common.excel.convert.ExcelDictConvert;
import com.xmzs.system.domain.FineTuneAttach;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.common.excel.annotation.ExcelDictFormat;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 微调附件视图对象 fine_tune_attach
 *
 * @author zhuzi
 * @date 2024-07-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = FineTuneAttach.class)
public class FineTuneAttachVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
    @ExcelProperty(value = "编码")
    private Long id;

    /**
     * 微调id
     */
    @ExcelProperty(value = "微调id")
    private String ftId;

    /**
     * 生成提示词id
     */
    private String promptsStoreId;

    /**
     * 微调文件id
     */
    @ExcelProperty(value = "微调文件id")
    private String ftFileId;

    /**
     * 微调作业id
     */
    @ExcelProperty(value = "文件名称")
    private String ftJobId;

    @ExcelProperty(value = "文件名称")
    private String fileName;

    /**
     * 上传文件id
     */
    @ExcelProperty(value = "上传文件id")
    private String fileId;

    /**
     * 文件状态（0未转化 1已转化 99转化失败)
     */
    @ExcelProperty(value = "文件状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "finetune_attach_status")
    private String status;

    /**
     * 文件内容
     */
    @ExcelProperty(value = "文件内容")
    private String fileContent;

    /**
     * 微调内容
     */
    @ExcelProperty(value = "微调内容")
    private String ftContent;

    /**
     * 错误消息
     */
    @ExcelProperty(value = "错误消息")
    private String messages;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;
}
