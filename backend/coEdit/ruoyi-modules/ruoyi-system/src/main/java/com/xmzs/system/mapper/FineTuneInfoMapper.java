package com.xmzs.system.mapper;

import com.xmzs.system.domain.FineTuneInfo;
import com.xmzs.system.domain.vo.FineTuneInfoVo;
import com.xmzs.common.mybatis.core.mapper.BaseMapperPlus;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 精调管理Mapper接口
 *
 * @author zhuzi
 * @date 2024-07-04
 */
@Component
public interface FineTuneInfoMapper extends BaseMapperPlus<FineTuneInfo, FineTuneInfoVo> {

}
