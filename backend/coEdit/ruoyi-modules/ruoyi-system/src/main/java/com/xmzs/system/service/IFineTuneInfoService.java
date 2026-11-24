package com.xmzs.system.service;

import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.system.domain.bo.FineTuneAttachBo;
import com.xmzs.system.domain.request.FineTuneAttachRequest;
import com.xmzs.system.domain.vo.FineTuneAttachVo;
import com.xmzs.system.domain.vo.FineTuneInfoVo;
import com.xmzs.system.domain.bo.FineTuneInfoBo;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 精调管理Service接口
 *
 * @author zhuzi
 * @date 2024-07-04
 */
public interface IFineTuneInfoService {

    /**
     * 查询精调管理
     */
    FineTuneInfoVo queryById(Integer id);

    /**
     * 查询精调管理列表
     */
    TableDataInfo<FineTuneInfoVo> queryPageList(FineTuneInfoBo bo, PageQuery pageQuery);

    /**
     * 查询精调管理列表
     */
    List<FineTuneInfoVo> queryList(FineTuneInfoBo bo);

    /**
     * 新增精调管理
     */
    FineTuneInfoBo insertByBo(FineTuneInfoBo bo);

    /**
     * 修改精调管理
     */
    Boolean updateByBo(FineTuneInfoBo bo);

    /**
     * 校验并批量删除精调管理信息
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);


    LoginUser getLoginUser();


    /**
     * 修改使用状态
     *
     * @param Id Id
     * @param useStatus 使用状态
     * @return 结果
     */
    int updateUseStatus(Integer Id, String useStatus);


    void upload(FineTuneAttachRequest request);

    int fileContentToFtContent(Long Id,String promptsStoreId);
    int updateFtContent(Long Id, String useStatus);

    FineTuneAttachVo getAttachStatus(Long id);

    FineTuneAttachBo createFinetuneModel(String ftId, Long finetuneAttachId);

    int getContentCheck(Long id,String ftContent);

    int getTraningStatus(String ftid);

}
