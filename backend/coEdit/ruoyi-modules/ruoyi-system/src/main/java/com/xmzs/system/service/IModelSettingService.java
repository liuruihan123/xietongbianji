package com.xmzs.system.service;

import com.xmzs.system.domain.ModelSetting;
import com.xmzs.system.domain.vo.ModelSettingVo;
import com.xmzs.system.domain.bo.ModelSettingBo;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 功能参数Service接口
 *
 * @author zhuzi
 * @date 2024-08-27
 */
public interface IModelSettingService {

    /**
     * 查询功能参数
     */
    ModelSettingVo queryById(Integer id);

    /**
     * 查询功能参数列表
     */
    TableDataInfo<ModelSettingVo> queryPageList(ModelSettingBo bo, PageQuery pageQuery);

    /**
     * 查询功能参数列表
     */
    List<ModelSettingVo> queryList(ModelSettingBo bo);

    /**
     * 新增功能参数
     */
    Boolean insertByBo(ModelSettingBo bo);

    /**
     * 修改功能参数
     */
    Boolean updateByBo(ModelSettingBo bo);

    /**
     * 校验并批量删除功能参数信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
