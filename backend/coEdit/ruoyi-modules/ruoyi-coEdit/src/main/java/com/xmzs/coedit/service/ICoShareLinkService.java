package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.bo.CoShareLinkBo;
import com.xmzs.coedit.domain.vo.CoShareLinkVo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 共享链接Service接口
 *
 * @author zuleed
 * @date 2025-11-04
 */
public interface ICoShareLinkService {

    /**
     * 创建共享链接
     *
     * @param bo 共享链接业务对象
     * @param userId 用户ID
     * @param userName 用户名称
     * @return 共享链接视图对象
     */
    CoShareLinkVo createShareLink(CoShareLinkBo bo, Long userId, String userName);

    /**
     * 根据共享码获取共享链接
     *
     * @param shareCode 共享码
     * @return 共享链接视图对象
     */
    CoShareLinkVo getShareLinkByCode(String shareCode);

    /**
     * 根据ID获取共享链接
     *
     * @param id 共享链接ID
     * @return 共享链接视图对象
     */
    CoShareLinkVo getShareLinkById(Long id);

    /**
     * 根据文件ID获取共享链接列表
     *
     * @param fileId 文件ID
     * @param pageQuery 分页参数
     * @return 共享链接列表
     */
    TableDataInfo<CoShareLinkVo> getShareLinksByFileId(Long fileId, PageQuery pageQuery);

    /**
     * 根据创建者ID获取共享链接列表
     *
     * @param creatorId 创建者ID
     * @param pageQuery 分页参数
     * @return 共享链接列表
     */
    TableDataInfo<CoShareLinkVo> getShareLinksByCreatorId(Long creatorId, PageQuery pageQuery);

    /**
     * 更新共享链接
     *
     * @param bo 共享链接业务对象
     * @return 是否成功
     */
    Boolean updateShareLink(CoShareLinkBo bo);

    /**
     * 禁用共享链接
     *
     * @param id 共享链接ID
     * @return 是否成功
     */
    Boolean disableShareLink(Long id);

    /**
     * 删除共享链接
     *
     * @param id 共享链接ID
     * @return 是否成功
     */
    Boolean deleteShareLink(Long id);

    /**
     * 批量删除共享链接
     *
     * @param ids 共享链接ID列表
     * @return 是否成功
     */
    Boolean deleteShareLinks(List<Long> ids);

    /**
     * 增加访问次数
     *
     * @param shareCode 共享码
     * @return 是否成功
     */
    Boolean incrementAccessCount(String shareCode);

    /**
     * 检查共享链接是否有效
     *
     * @param shareCode 共享码
     * @param password 访问密码（如果需要）
     * @return 是否有效
     */
    Boolean checkShareLinkValid(String shareCode, String password);

    /**
     * 清理过期链接
     *
     * @return 清理数量
     */
    Integer cleanExpiredLinks();

    /**
     * 生成共享码
     *
     * @return 共享码
     */
    String generateShareCode();

}

