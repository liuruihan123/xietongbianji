package com.xmzs.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xmzs.system.domain.FineTuneInfo;
import com.xmzs.system.domain.SysDept;
import com.xmzs.system.mapper.FineTuneInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.xmzs.system.domain.bo.FineTuneAttachBo;
import com.xmzs.system.domain.vo.FineTuneAttachVo;
import com.xmzs.system.domain.FineTuneAttach;
import com.xmzs.system.mapper.FineTuneAttachMapper;
import com.xmzs.system.service.IFineTuneAttachService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 微调附件Service业务层处理
 *
 * @author zhuzi
 * @date 2024-07-04
 */
@RequiredArgsConstructor
@Service
public class FineTuneAttachServiceImpl extends ServiceImpl<FineTuneAttachMapper, FineTuneAttach>  implements IFineTuneAttachService {

    private final FineTuneAttachMapper baseMapper;

    /**
     * 查询微调附件
     */
    @Override
    public FineTuneAttachVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询微调附件列表
     */
    @Override
    public TableDataInfo<FineTuneAttachVo> queryPageList(FineTuneAttachBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<FineTuneAttach> lqw = buildQueryWrapper(bo);
        Page<FineTuneAttachVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询微调附件列表
     */
    @Override
    public List<FineTuneAttachVo> queryList(FineTuneAttachBo bo) {
        LambdaQueryWrapper<FineTuneAttach> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<FineTuneAttach> buildQueryWrapper(FineTuneAttachBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<FineTuneAttach> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getFtId()), FineTuneAttach::getFtId, bo.getFtId());
        lqw.eq(StringUtils.isNotBlank(bo.getPromptsStoreId()), FineTuneAttach::getPromptsStoreId, bo.getPromptsStoreId());
        lqw.eq(StringUtils.isNotBlank(bo.getFtFileId()), FineTuneAttach::getFtFileId, bo.getFtFileId());
        lqw.eq(StringUtils.isNotBlank(bo.getFileName()), FineTuneAttach::getFileName, bo.getFileName());
        lqw.eq(bo.getFileId() != null, FineTuneAttach::getFileId, bo.getFileId());
        lqw.eq(bo.getFtJobId() != null, FineTuneAttach::getFtJobId, bo.getFtJobId());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), FineTuneAttach::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getFileContent()), FineTuneAttach::getFileContent, bo.getFileContent());
        lqw.eq(StringUtils.isNotBlank(bo.getFtContent()), FineTuneAttach::getFtContent, bo.getFtContent());
        lqw.eq(StringUtils.isNotBlank(bo.getMessages()), FineTuneAttach::getMessages, bo.getMessages());
        lqw.orderByDesc(FineTuneAttach::getCreateTime);
        return lqw;
    }

    /**
     * 新增微调附件
     */
    @Override
    public Boolean insertByBo(FineTuneAttachBo bo) {
        FineTuneAttach add = MapstructUtils.convert(bo, FineTuneAttach.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改微调附件
     */
    @Override
    public Boolean updateByBo(FineTuneAttachBo bo) {
        FineTuneAttach update = MapstructUtils.convert(bo, FineTuneAttach.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(FineTuneAttach entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除微调附件
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }


    /**
     * 根据微调附件找到微调数据
     * @param attachId
     * @return
     */
    public FineTuneAttach getFirstattachByftId(Long attachId) {
        QueryWrapper<FineTuneAttach> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", attachId); // 设置查询条件，字段名应该与数据库中的列名一致
        queryWrapper.last("LIMIT 1"); // 限制查询结果只返回一条记录
        return this.getOne(queryWrapper); // 执行查询并返回结果
    }
}
