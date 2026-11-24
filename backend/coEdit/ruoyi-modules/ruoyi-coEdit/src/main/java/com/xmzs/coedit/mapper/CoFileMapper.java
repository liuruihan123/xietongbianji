package com.xmzs.coedit.mapper;

import com.xmzs.coedit.domain.CoFile;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.common.mybatis.core.mapper.BaseMapperPlus;

/**
 * 文件Mapper接口
 *
 * @author zuleed
 * @date 2025-11-04
 */
public interface CoFileMapper extends BaseMapperPlus<CoFile, CoFileVo> {

    /**
     * 清空文件表
     */
    void clearAll();
}

