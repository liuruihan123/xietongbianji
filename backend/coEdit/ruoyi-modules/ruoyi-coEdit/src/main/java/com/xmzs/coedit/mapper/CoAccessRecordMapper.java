package com.xmzs.coedit.mapper;

import com.xmzs.coedit.domain.CoAccessRecord;
import com.xmzs.coedit.domain.vo.CoAccessRecordVo;
import com.xmzs.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;

/**
 * 访问记录Mapper接口
 *
 * @author zuleed
 * @date 2025-11-08
 */
public interface CoAccessRecordMapper extends BaseMapperPlus<CoAccessRecord, CoAccessRecordVo> {

    /**
     * 清空所有访问记录
     */
    void clearAll();

    /**
     * Upsert访问记录（插入或更新）
     * 如果记录不存在则插入，存在则更新last_accessed_at并增加view_count
     *
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param userId 用户ID
     * @param workspaceId 工作空间ID
     * @param accessType 访问类型
     * @param accessDuration 访问时长
     * @param accessIp 访问IP
     * @param userAgent 用户代理
     * @return 影响的行数
     */
    int upsertAccess(@Param("targetType") Integer targetType,
                     @Param("targetId") Long targetId,
                     @Param("userId") Long userId,
                     @Param("workspaceId") Long workspaceId,
                     @Param("accessType") Integer accessType,
                     @Param("accessDuration") Integer accessDuration,
                     @Param("accessIp") String accessIp,
                     @Param("userAgent") String userAgent);
}

