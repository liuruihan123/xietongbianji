package com.xmzs.coedit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.coedit.domain.CoEditHistory;
import com.xmzs.coedit.mapper.CoEditHistoryMapper;
import com.xmzs.coedit.service.ICoEditHistoryService;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 编辑历史Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoEditHistoryServiceImpl implements ICoEditHistoryService {

    private final CoEditHistoryMapper baseMapper;

    /**
     * 记录编辑历史
     */
    @Override
    public void recordHistory(Long fileId, Long userId, String userName, String sessionKey,
                             String operationType, String contentBefore, String contentAfter, String positionData) {
        CoEditHistory history = new CoEditHistory();
        history.setFileId(fileId);
        history.setUserId(userId);
        history.setUserName(userName);
        history.setSessionKey(sessionKey);
        history.setOperationType(operationType);
        history.setContentBefore(contentBefore);
        history.setContentAfter(contentAfter);
        history.setPositionData(positionData);
        history.setOperationTime(new Date());

        baseMapper.insert(history);
        log.debug("记录编辑历史: fileId={}, userId={}, operationType={}", fileId, userId, operationType);
    }

    /**
     * 查询文件的编辑历史
     */
    @Override
    public TableDataInfo<CoEditHistory> queryFileHistory(Long fileId, PageQuery pageQuery) {
        LambdaQueryWrapper<CoEditHistory> wrapper = Wrappers.lambdaQuery(CoEditHistory.class)
            .eq(CoEditHistory::getFileId, fileId)
            .orderByDesc(CoEditHistory::getOperationTime);

        Page<CoEditHistory> page = baseMapper.selectPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(page);
    }

    /**
     * 查询用户的编辑历史
     */
    @Override
    public TableDataInfo<CoEditHistory> queryUserHistory(Long userId, PageQuery pageQuery) {
        LambdaQueryWrapper<CoEditHistory> wrapper = Wrappers.lambdaQuery(CoEditHistory.class)
            .eq(CoEditHistory::getUserId, userId)
            .orderByDesc(CoEditHistory::getOperationTime);

        Page<CoEditHistory> page = baseMapper.selectPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(page);
    }

}

