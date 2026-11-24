package com.xmzs.coedit.convert;

import com.xmzs.coedit.domain.CoFileVersion;
import com.xmzs.coedit.domain.bo.CoFileVersionBo;
import com.xmzs.coedit.domain.vo.CoFileVersionVo;
import io.github.linpeilie.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * 文件版本转换器
 *
 * @author zuleed
 * @date 2025-11-21
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CoFileVersionConvert extends BaseMapper<CoFileVersion, CoFileVersionVo> {

    /**
     * CoFileVersion 转 CoFileVersionVo
     */
    CoFileVersionVo toVo(CoFileVersion entity);

    /**
     * CoFileVersionBo 转 CoFileVersion
     */
    CoFileVersion toEntity(CoFileVersionBo bo);

}

