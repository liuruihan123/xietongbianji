package com.xmzs.system.domain.vo;

import lombok.Data;

/**
 * 租户列表
 *
 * @author Lion Li
 */
@Data
public class TenantListVo {

    private String tenantId;

    private String companyName;

    private String domain;

    /**
     * 从 SysTenantVo 转换为 TenantListVo
     */
    public static TenantListVo fromSysTenantVo(SysTenantVo sysTenantVo) {
        if (sysTenantVo == null) {
            return null;
        }
        TenantListVo tenantListVo = new TenantListVo();
        tenantListVo.setTenantId(sysTenantVo.getTenantId());
        tenantListVo.setCompanyName(sysTenantVo.getCompanyName());
        tenantListVo.setDomain(sysTenantVo.getDomain());
        return tenantListVo;
    }

}
