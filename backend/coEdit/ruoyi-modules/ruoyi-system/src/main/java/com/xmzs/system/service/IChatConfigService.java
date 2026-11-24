package com.xmzs.system.service;

import com.xmzs.system.domain.vo.ChatConfigVo;
import com.xmzs.system.domain.bo.ChatConfigBo;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 对话配置信息Service接口
 * @date 2024-04-13
 */
public interface IChatConfigService {

    /**
     * 查询配置信息

     */
    ChatConfigVo queryById(Long id);


    ChatConfigVo queryByConfigName(String configName);

    /**
     * 查询配置信息列表
     */
    TableDataInfo<ChatConfigVo> queryPageList(ChatConfigBo bo, PageQuery pageQuery);

    /**
     * 查询配置信息列表
     */
    List<ChatConfigVo> queryList(ChatConfigBo bo);

    /**
     * 新增配置信息

     */
    Boolean insertByBo(ChatConfigBo bo);

    /**
     * 修改配置信息

     */
    Boolean updateByBo(ChatConfigBo bo);

    /**
     * 校验并批量删除配置信息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
