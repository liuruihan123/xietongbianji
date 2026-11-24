package com.xmzs.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
// import com.xmzs.common.chat.config.ChatClientConfig;
// import com.xmzs.common.chat.openai.OpenAiClient;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xmzs.system.domain.bo.ModelSettingBo;
import com.xmzs.system.domain.vo.ModelSettingVo;
import com.xmzs.system.domain.ModelSetting;
import com.xmzs.system.mapper.ModelSettingMapper;
import com.xmzs.system.service.IModelSettingService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 功能参数Service业务层处理
 *
 * @author zhuzi
 * @date 2024-08-27
 */
@RequiredArgsConstructor
@Service
//public class ModelSettingServiceImpl implements IModelSettingService {
public class ModelSettingServiceImpl extends ServiceImpl<ModelSettingMapper, ModelSetting> implements IModelSettingService{

    private final ModelSettingMapper baseMapper;

    /**
     * 查询功能参数
     */
    @Override
    public ModelSettingVo queryById(Integer id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询功能参数列表
     */
    @Override
    public TableDataInfo<ModelSettingVo> queryPageList(ModelSettingBo bo, PageQuery pageQuery) {
//        LambdaQueryWrapper<ModelSetting> lqw = buildQueryWrapper(bo);
//        Page<ModelSettingVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
//        return TableDataInfo.build(result);
        Page<ModelSettingVo> page = baseMapper.selectPageModelSettingList(pageQuery.build(), this.buildQueryWrapper(bo));
        return TableDataInfo.build(page);
    }

    /**
     * 查询功能参数列表
     */
    @Override
    public List<ModelSettingVo> queryList(ModelSettingBo bo) {
//        List<ModelSettingVo> modelList =  baseMapper.selectModelList(this.buildQueryWrapper(bo));
        Wrapper<ModelSetting> lqw = buildQueryWrapper(bo);
        return baseMapper.selectModelList(lqw);
//        Page<ModelSettingVo> page = baseMapper.selectVoPage(pageQuery.build(), this.buildQueryWrapper(knowledge));
//        return TableDataInfo.build(page);
    }

    private Wrapper<ModelSetting> buildQueryWrapper(ModelSettingBo bo) {
        Map<String, Object> params = bo.getParams();
//        LambdaQueryWrapper<ModelSetting> lqw = Wrappers.lambdaQuery();
        QueryWrapper<ModelSetting> lqw = Wrappers.query();

        lqw.eq(StringUtils.isNotBlank(bo.getKid()), "k.kid", bo.getKid());
        lqw.eq(StringUtils.isNotBlank(bo.getUseKnowledge()), "k.use_knowledge", bo.getUseKnowledge());
        lqw.eq(bo.getShortNoOpen() != null, "k.short_no_open", bo.getShortNoOpen());
        lqw.eq(bo.getStandardTemplateId() != null, "k.standard_template_id", bo.getStandardTemplateId());
        lqw.eq(StringUtils.isNotBlank(bo.getAfterTalkCount()) , "k.after_talk_count", bo.getAfterTalkCount());
        lqw.eq(bo.getTtsSpeed() != null, "k.tts_speed", bo.getTtsSpeed());


        lqw.eq(bo.getBusType() != null, "k.bus_type", bo.getBusType());
        lqw.eq(bo.getModelId() != null, "k.model_id", bo.getModelId());
        lqw.like(StringUtils.isNotBlank(bo.getName()), "k.name", bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getSystemRole()), "k.system_role", bo.getSystemRole());
        lqw.eq(bo.getTalkCount() != null, "k.talk_count", bo.getTalkCount());
        lqw.eq(StringUtils.isNotBlank(bo.getTtsVoice()), "k.tts_voice", bo.getTtsVoice());
        lqw.eq(bo.getTemperature() != null, "k.temperature", bo.getTemperature());
        lqw.eq(bo.getTopP() != null, "k.top_p", bo.getTopP());
        lqw.eq(bo.getMaxTokens() != null, "k.max_tokens", bo.getMaxTokens());
        lqw.eq(bo.getPresencePenalty() != null, "k.presence_penalty", bo.getPresencePenalty());
        lqw.eq(bo.getFrequencyPenalty() != null, "k.frequency_penalty", bo.getFrequencyPenalty());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), "k.status", bo.getStatus());
        return lqw;
    }

    /**
     * 新增功能参数
     */
    @Override
    public Boolean insertByBo(ModelSettingBo bo) {
        ModelSetting add = MapstructUtils.convert(bo, ModelSetting.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改功能参数
     */
    @Override
    public Boolean updateByBo(ModelSettingBo bo) {
        ModelSetting update = MapstructUtils.convert(bo, ModelSetting.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ModelSetting entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除功能参数
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }

        return baseMapper.deleteBatchIds(ids) > 0;
    }

}
