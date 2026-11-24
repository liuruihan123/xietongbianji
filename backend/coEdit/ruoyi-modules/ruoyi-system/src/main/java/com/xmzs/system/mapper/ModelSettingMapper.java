package com.xmzs.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.system.domain.ModelSetting;
import com.xmzs.system.domain.vo.ModelSettingVo;
import com.xmzs.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功能参数Mapper接口
 *
 * @author zhuzi
 * @date 2024-08-27
 */
@Mapper
public interface ModelSettingMapper extends BaseMapperPlus<ModelSetting, ModelSettingVo> {

    Page<ModelSettingVo> selectPageModelSettingList(@Param("page") Page<ModelSetting> page, @Param(Constants.WRAPPER) Wrapper<ModelSetting> queryWrapper);

    List<ModelSettingVo> selectModelList(@Param(Constants.WRAPPER) Wrapper<ModelSetting> queryWrapper);
}
