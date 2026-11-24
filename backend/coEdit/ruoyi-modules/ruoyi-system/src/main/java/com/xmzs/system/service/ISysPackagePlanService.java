package com.xmzs.system.service;

import com.xmzs.system.domain.SysPackagePlan;
import com.xmzs.system.domain.vo.SysPackagePlanVo;
import com.xmzs.system.domain.bo.SysPackagePlanBo;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 套餐管理Service接口
 *
 * @author zhuzi
 * @date 2024-06-09
 */
public interface ISysPackagePlanService {

    /**
     * 查询套餐管理
     */
    SysPackagePlanVo queryById(Long id);

    /**
     * 查询套餐管理列表
     */
    TableDataInfo<SysPackagePlanVo> queryPageList(SysPackagePlanBo bo, PageQuery pageQuery);

    /**
     * 查询套餐管理列表
     */
    List<SysPackagePlanVo> queryList(SysPackagePlanBo bo);

    /**
     * 新增套餐管理
     */
    Boolean insertByBo(SysPackagePlanBo bo);

    /**
     * 修改套餐管理
     */
    Boolean updateByBo(SysPackagePlanBo bo);

    /**
     * 校验并批量删除套餐管理信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
