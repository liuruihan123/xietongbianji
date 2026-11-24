package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.CoShareSecurity;
import com.xmzs.coedit.domain.bo.CoShareSecurityBo;

/**
 * 共享安全控制Service接口
 *
 * @author zuleed
 * @date 2025-11-04
 */
public interface ICoShareSecurityService {

    /**
     * 创建安全配置
     *
     * @param bo 安全配置业务对象
     * @return 安全配置
     */
    CoShareSecurity createSecurityConfig(CoShareSecurityBo bo);

    /**
     * 根据共享链接ID获取安全配置
     *
     * @param shareLinkId 共享链接ID
     * @return 安全配置
     */
    CoShareSecurity getSecurityConfigByShareLinkId(Long shareLinkId);

    /**
     * 更新安全配置
     *
     * @param bo 安全配置业务对象
     * @return 是否成功
     */
    Boolean updateSecurityConfig(CoShareSecurityBo bo);

    /**
     * 删除安全配置
     *
     * @param shareLinkId 共享链接ID
     * @return 是否成功
     */
    Boolean deleteSecurityConfig(Long shareLinkId);

    /**
     * 检查IP是否被允许访问
     *
     * @param shareLinkId 共享链接ID
     * @param accessIp 访问IP
     * @return 是否允许
     */
    Boolean checkIpAllowed(Long shareLinkId, String accessIp);

    /**
     * 检查访问是否被限制
     *
     * @param shareCode 共享码
     * @param accessIp 访问IP
     * @return 是否被限制
     */
    Boolean checkAccessRestricted(String shareCode, String accessIp);

}

