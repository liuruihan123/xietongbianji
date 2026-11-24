package com.xmzs.system.service;

import com.xmzs.system.domain.vo.VoiceRoleVo;
import com.xmzs.system.domain.bo.VoiceRoleBo;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.system.request.RoleListDto;
import com.xmzs.system.request.RoleRequest;
import com.xmzs.system.request.SimpleGenerateRequest;
import com.xmzs.system.response.SimpleGenerateDataResponse;
import com.xmzs.system.response.rolelist.ContentResponse;
import com.xmzs.system.response.rolelist.RoleListResponse;
import com.xmzs.system.response.rolelist.RoleListVO;

import java.util.Collection;
import java.util.List;

/**
 * 配音角色Service接口
 *
 * @author Lion Li
 * @date 2024-03-19
 */
public interface IVoiceRoleService {

    /**
     * 查询配音角色
     */
    VoiceRoleVo queryById(Long id);

    /**
     * 查询配音角色列表
     */
    TableDataInfo<VoiceRoleVo> queryPageList(VoiceRoleBo bo, PageQuery pageQuery);

    /**
     * 查询配音角色列表
     */
    List<VoiceRoleVo> queryList(VoiceRoleBo bo);

    /**
     * 新增配音角色
     */
    Boolean insertByBo(RoleRequest roleRequest);

    /**
     * 生成音频
     */
    SimpleGenerateDataResponse simpleGenerate(SimpleGenerateRequest simpleGenerateRequest);

    /**
     * 修改配音角色
     */
    Boolean updateByBo(VoiceRoleBo bo);

    /**
     * 校验并批量删除配音角色信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 查询市场角色
     *
     * @return 角色列表
     */
    List<RoleListVO> roleList();

    /**
     * 收藏市场角色
     *
     */
    void copyRole(RoleListDto roleListDto);
}
