package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.bo.CoFileConversionBo;
import com.xmzs.coedit.domain.vo.CoFileConversionVo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Map;

/**
 * 文件转换Service接口
 *
 * @author zuleed
 * @date 2025-11-05
 */
public interface ICoFileConversionService {

    /**
     * 转换文件
     *
     * @param bo 转换业务对象
     * @return 转换记录ID
     */
    Long convertFile(CoFileConversionBo bo);

    /**
     * 批量转换文件
     *
     * @param boList 转换业务对象列表
     * @return 转换记录ID列表
     */
    List<Long> batchConvert(List<CoFileConversionBo> boList);

    /**
     * 获取转换状态
     *
     * @param id 转换记录ID
     * @return 转换记录
     */
    CoFileConversionVo getConversionStatus(Long id);

    /**
     * 获取支持的目标格式
     *
     * @param sourceFileType 源文件类型
     * @return 支持的目标格式列表
     */
    List<String> getSupportedFormats(String sourceFileType);

    /**
     * 获取所有支持的格式映射
     *
     * @return 格式映射
     */
    Map<String, List<String>> getAllSupportedFormats();

    /**
     * 取消转换
     *
     * @param id 转换记录ID
     * @return 是否成功
     */
    Boolean cancelConversion(Long id);

    /**
     * 获取文件的转换历史
     *
     * @param fileId 文件ID
     * @param pageQuery 分页参数
     * @return 转换历史列表
     */
    TableDataInfo<CoFileConversionVo> getConversionHistory(Long fileId, PageQuery pageQuery);

    /**
     * 获取用户的转换历史
     *
     * @param userId 用户ID
     * @param pageQuery 分页参数
     * @return 转换历史列表
     */
    TableDataInfo<CoFileConversionVo> getUserConversionHistory(Long userId, PageQuery pageQuery);

    /**
     * 删除转换记录
     *
     * @param id 转换记录ID
     * @return 是否成功
     */
    Boolean deleteConversion(Long id);

    /**
     * 批量删除转换记录
     *
     * @param ids 转换记录ID列表
     * @return 是否成功
     */
    Boolean batchDeleteConversion(List<Long> ids);

    /**
     * 清理过期的转换记录
     *
     * @param days 保留天数
     * @return 清理数量
     */
    Integer cleanExpiredConversions(Integer days);

    /**
     * 重试失败的转换
     *
     * @param id 转换记录ID
     * @return 新的转换记录ID
     */
    Long retryConversion(Long id);

    /**
     * 下载转换后的文件
     *
     * @param id 转换记录ID
     * @return 文件URL
     */
    String downloadConvertedFile(Long id);
}

