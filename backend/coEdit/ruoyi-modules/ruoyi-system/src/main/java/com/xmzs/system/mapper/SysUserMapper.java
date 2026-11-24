package com.xmzs.system.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.common.mybatis.annotation.DataColumn;
import com.xmzs.common.mybatis.annotation.DataPermission;
import com.xmzs.common.mybatis.core.mapper.BaseMapperPlus;
import com.xmzs.system.domain.SysUser;
import com.xmzs.system.domain.UserAmount;
import com.xmzs.system.domain.bo.SysUserBo;
import com.xmzs.system.domain.vo.SysUserVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 用户表 数据层
 *
 * @author Lion Li
 */
public interface SysUserMapper extends BaseMapperPlus<SysUser, SysUserVo> {

    @DataPermission({
        @DataColumn(key = "deptName", value = "d.dept_id"),
        @DataColumn(key = "userName", value = "u.user_id")
    })
    Page<SysUserVo> selectPageUserList(@Param("page") Page<SysUser> page, @Param(Constants.WRAPPER) Wrapper<SysUser> queryWrapper);

    /**
     * 根据条件分页查询用户列表
     *
     * @param queryWrapper 查询条件
     * @return 用户信息集合信息
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "d.dept_id"),
        @DataColumn(key = "userName", value = "u.user_id")
    })
    List<SysUserVo> selectUserList(@Param(Constants.WRAPPER) Wrapper<SysUser> queryWrapper);

    /**
     * 根据条件分页查询已配用户角色列表
     *
     * @param queryWrapper 查询条件
     * @return 用户信息集合信息
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "d.dept_id"),
        @DataColumn(key = "userName", value = "u.user_id")
    })
    Page<SysUserVo> selectAllocatedList(@Param("page") Page<SysUser> page, @Param(Constants.WRAPPER) Wrapper<SysUser> queryWrapper);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param queryWrapper 查询条件
     * @return 用户信息集合信息
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "d.dept_id"),
        @DataColumn(key = "userName", value = "u.user_id")
    })
    Page<SysUserVo> selectUnallocatedList(@Param("page") Page<SysUser> page, @Param(Constants.WRAPPER) Wrapper<SysUser> queryWrapper);

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    SysUserVo selectUserByUserName(String userName);

    /**
     * 通过OpenId查询用户
     *
     * @param OpenId 微信用户唯一标识
     * @return 用户对象信息
     */
    SysUserVo selectUserByOpenId(String OpenId);

    SysUserVo selectUserByUnionid(String unionid);

    /**
     * 通过手机号查询用户
     *
     * @param phonenumber 手机号
     * @return 用户对象信息
     */
    SysUserVo selectUserByPhonenumber(String phonenumber);


    /**
     * 通过邀请码查询用户
     *
     * @param inviteCode 邀请码
     * @return 用户对象信息
     */
    SysUserVo selectUserByInviteCode(String inviteCode);



    /**
     * 通过邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户对象信息
     */
    SysUserVo selectUserByEmail(String email);

    /**
     * 通过用户名查询用户(不走租户插件)
     *
     * @param userName 用户名
     * @param tenantId 租户id
     * @return 用户对象信息
     */
    @InterceptorIgnore(tenantLine = "true")
    SysUserVo selectTenantUserByUserName(String userName, String tenantId);

    /**
     * 通过手机号查询用户(不走租户插件)
     *
     * @param phonenumber 手机号
     * @param tenantId    租户id
     * @return 用户对象信息
     */
    @InterceptorIgnore(tenantLine = "true")
    SysUserVo selectTenantUserByPhonenumber(String phonenumber, String tenantId);

    /**
     * 通过邮箱查询用户(不走租户插件)
     *
     * @param email    邮箱
     * @param tenantId 租户id
     * @return 用户对象信息
     */
    @InterceptorIgnore(tenantLine = "true")
    SysUserVo selectTenantUserByEmail(String email, String tenantId);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
//    @DataPermission({
//        @DataColumn(key = "deptName", value = "d.dept_id"),
//        @DataColumn(key = "userName", value = "u.user_id")
//    })
    @InterceptorIgnore(dataPermission = "true")
    SysUserVo selectUserById(Long userId);

    @Override
//    @DataPermission({
//        @DataColumn(key = "deptName", value = "dept_id"),
//        @DataColumn(key = "userName", value = "user_id")
//    })
    @InterceptorIgnore(dataPermission = "true")
    int update(@Param(Constants.ENTITY) SysUser user, @Param(Constants.WRAPPER) Wrapper<SysUser> updateWrapper);

    @Override
//    @DataPermission({
//        @DataColumn(key = "deptName", value = "dept_id"),
//        @DataColumn(key = "userName", value = "user_id")
//    })
    @InterceptorIgnore(dataPermission = "true")
    int updateById(@Param(Constants.ENTITY) SysUser user);


    /**
     * 小程序 -修改用户信息
     *
     * @param user
     *
     */
    void updateXcxUser(SysUserBo user);


    /**
     * 计算注册用户
     * @param beginTime
     * @param endTime
     * @return
     */
    @Select("SELECT count(*) as userCount FROM sys_user WHERE create_time >= STR_TO_DATE(#{beginTime}, '%Y-%m-%d %H:%i:%s') AND create_time <= STR_TO_DATE(#{endTime}, '%Y-%m-%d %H:%i:%s') ")
    Integer userCount(@Param("beginTime") String beginTime, @Param("endTime") String endTime);


    /**
     * 计算付费会员数
     * @param beginTime
     * @param endTime
     * @return
     */
    @Select("SELECT count(*) as userCount FROM sys_user WHERE create_time >= STR_TO_DATE(#{beginTime}, '%Y-%m-%d %H:%i:%s') AND create_time <= STR_TO_DATE(#{endTime}, '%Y-%m-%d %H:%i:%s') AND user_grade = '1'")
    Integer gradeCount(@Param("beginTime") String beginTime, @Param("endTime") String endTime);



    /**
     * 获取最近15天的注册用户
     * @return
     */
    @Select("SELECT DATE(create_time) as date, count(*) as count" +
        " FROM sys_user " +
        "WHERE  create_time >= CURDATE() - INTERVAL #{days} DAY " +
        "GROUP BY DATE(create_time) " +
        "ORDER BY DATE(create_time)")
    List<Map<String, Object>> getLastDaysUser(@Param("days") int days);


    /**
     * 获取用户付费排行
     * @return
     */
    @Select("\n" +
        "SELECT \n" +
        "    u.user_name,\n" +
        "    SUM(CASE \n" +
        "            WHEN o.payment_method = 'stripePay' THEN o.amount * 7.3  \n" +
        "            ELSE o.amount \n" +
        "        END) AS total_amount\n" +
        "FROM \n" +
        "    sys_user u\n" +
        "LEFT JOIN \n" +
        "    (select so.* from sys_pay_order so where so.payment_status='2') o ON u.user_id = o.user_id \n" +
        "GROUP BY \n" +
        "    u.user_id, u.user_name\n" +
        "ORDER BY \n" +
        "    total_amount DESC LIMIT 15  ")
    List<UserAmount> getUserPaymentRank();

    @Select("SELECT COUNT(1) FROM sys_user WHERE user_name = #{acc}")
    int selectCountByUserName(String acc);
}
