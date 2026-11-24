package com.xmzs.system.service.impl;

import com.xmzs.common.core.service.ConfigService;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xmzs.system.config.base.BaseConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.xmzs.system.domain.bo.ChatConfigBo;
import com.xmzs.system.domain.vo.ChatConfigVo;
import com.xmzs.system.domain.ChatConfig;
import com.xmzs.system.mapper.ChatConfigMapper;
import com.xmzs.system.service.IChatConfigService;

import java.util.List;
import java.util.Collection;

/**
 * 对话配置信息
Service业务层处理
 *
 * @author Lion Li
 * @date 2024-04-13
 */
@RequiredArgsConstructor
@Service
public class ChatConfigServiceImpl implements IChatConfigService, ConfigService {


    private final ChatConfigMapper baseMapper;

    private final BaseConfig baseConfig;

    /**
     * 查询对话配置信息

     */
    @Override
    public ChatConfigVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    @Override
    public ChatConfigVo queryByConfigName(String configName){
        ChatConfigBo bo = new ChatConfigBo();
        bo.setConfigName(configName);
        LambdaQueryWrapper<ChatConfig> lqw = buildQueryWrapper(bo);
        ChatConfigVo chatConfigVo = baseMapper.selectVoOne(lqw);
        return chatConfigVo;
    }

    /**
     * 查询对话配置信息
列表
     */
    @Override
    public TableDataInfo<ChatConfigVo> queryPageList(ChatConfigBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChatConfig> lqw = buildQueryWrapper(bo);
        Page<ChatConfigVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询对话配置信息
列表
     */
    @Override
    public List<ChatConfigVo> queryList(ChatConfigBo bo) {
        LambdaQueryWrapper<ChatConfig> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ChatConfig> buildQueryWrapper(ChatConfigBo bo) {
        LambdaQueryWrapper<ChatConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getCategory()), ChatConfig::getCategory, bo.getCategory());
        lqw.like(StringUtils.isNotBlank(bo.getConfigName()), ChatConfig::getConfigName, bo.getConfigName());
        lqw.eq(StringUtils.isNotBlank(bo.getConfigValue()), ChatConfig::getConfigValue, bo.getConfigValue());
        // 根据 id 升序排序
        lqw.orderByAsc(ChatConfig::getId);
        return lqw;
    }

    /**
     * 新增对话配置信息

     */
    @Override
    public Boolean insertByBo(ChatConfigBo bo) {
        ChatConfig add = MapstructUtils.convert(bo, ChatConfig.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        baseConfig.reset(bo.getCategory());
        return flag;
    }

    /**
     * 修改对话配置信息

     */
    @Override
    public Boolean updateByBo(ChatConfigBo bo) {
        ChatConfig update = MapstructUtils.convert(bo, ChatConfig.class);
        validEntityBeforeSave(update);
        baseConfig.reset(bo.getCategory());
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ChatConfig entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除对话配置信息

     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 根据配置类型和配置key获取值
     * 注意：此方法直接查询实体，不使用 MapStruct 转换，避免启动时转换器未初始化的问题
     *
     * @param category 配置类型
     * @param configKey 配置key
     * @return 配置值
     */
    @Override
    public String getConfigValue(String category, String configKey) {
        LambdaQueryWrapper<ChatConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(category), ChatConfig::getCategory, category);
        lqw.eq(StringUtils.isNotBlank(configKey), ChatConfig::getConfigName, configKey);
        // 直接查询实体，避免使用 selectVoOne 触发 MapStruct 转换
        ChatConfig chatConfig = baseMapper.selectOne(lqw);
        if (chatConfig != null) {
            return chatConfig.getConfigValue();
        } else {
            return "";
        }
    }
}
