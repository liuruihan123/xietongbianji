package com.xmzs.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.baomidou.mybatisplus.annotation.TableField;
import com.xmzs.system.domain.AiQaCase;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * packageName com.xmzs.system.domain.vo
 *
 * @author zhuzi
 * @date 2025/5/10
 * @description TODO
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = LawsVo.class)
public class LawsVo  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String Gid;

    private String AllText;

    private String Title;


}
