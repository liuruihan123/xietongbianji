package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.bo.CoFavoriteBo;
import com.xmzs.coedit.domain.vo.CoFavoriteVo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Map;

/**
 * 收藏Service接口
 *
 * @author zuleed
 * @date 2025-11-08
 */
public interface ICoFavoriteService {

    /**
     * 查询收藏
     *
     * @param id 收藏主键
     * @return 收藏
     */
    CoFavoriteVo queryById(Long id);

    /**
     * 查询收藏列表
     *
     * @param bo 收藏
     * @param pageQuery 分页参数
     * @return 收藏集合
     */
    TableDataInfo<CoFavoriteVo> queryPageList(CoFavoriteBo bo, PageQuery pageQuery);

    /**
     * 查询收藏列表
     *
     * @param bo 收藏
     * @return 收藏集合
     */
    List<CoFavoriteVo> queryList(CoFavoriteBo bo);

    /**
     * 添加收藏
     *
     * @param bo 收藏
     * @return 是否成功
     */
    Boolean addFavorite(CoFavoriteBo bo);

    /**
     * 删除收藏
     *
     * @param id 收藏主键
     * @return 是否成功
     */
    Boolean removeFavorite(Long id);

    /**
     * 更新收藏
     *
     * @param bo 收藏
     * @return 是否成功
     */
    Boolean updateFavorite(CoFavoriteBo bo);

    /**
     * 检查是否已收藏
     *
     * @param userId 用户ID
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 是否已收藏
     */
    Boolean isFavorited(Long userId, Integer targetType, Long targetId);

    /**
     * 根据目标获取收藏信息
     *
     * @param userId 用户ID
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 收藏信息
     */
    CoFavoriteVo getFavoriteByTarget(Long userId, Integer targetType, Long targetId);

    /**
     * 批量添加收藏
     *
     * @param boList 收藏列表
     * @return 是否成功
     */
    Boolean batchAddFavorite(List<CoFavoriteBo> boList);

    /**
     * 批量删除收藏
     *
     * @param ids 收藏主键集合
     * @return 是否成功
     */
    Boolean batchRemoveFavorite(List<Long> ids);

    /**
     * 检查并更新收藏有效性
     *
     * @param favoriteId 收藏ID
     * @return 是否有效
     */
    Boolean checkAndUpdateValidity(Long favoriteId);

    /**
     * 批量检查用户的所有收藏有效性
     *
     * @param userId 用户ID
     * @return 失效的收藏数量
     */
    Integer batchCheckValidity(Long userId);

    /**
     * 获取失效的收藏列表
     *
     * @param userId 用户ID
     * @return 失效的收藏列表
     */
    List<CoFavoriteVo> getInvalidFavorites(Long userId);

    /**
     * 根据文件ID查询是否被收藏
     *
     * @param fileId 文件ID
     * @return 是否已收藏
     */
    Boolean isFileFavorited(Long fileId);

}

