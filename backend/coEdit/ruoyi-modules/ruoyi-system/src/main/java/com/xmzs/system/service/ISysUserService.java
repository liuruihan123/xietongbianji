package com.xmzs.system.service;

import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.system.domain.SysUser;
import com.xmzs.system.domain.bo.SysUserBo;
import com.xmzs.system.domain.vo.SysUserVo;

import java.util.List;

/**
 * 用户 业务层
 *
 * @author Lion Li
 */
public interface ISysUserService {


    TableDataInfo<SysUserVo> selectPageUserList(SysUserBo user, PageQuery pageQuery);

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    List<SysUserVo> selectUserList(SysUserBo user);

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    TableDataInfo<SysUserVo> selectAllocatedList(SysUserBo user, PageQuery pageQuery);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    TableDataInfo<SysUserVo> selectUnallocatedList(SysUserBo user, PageQuery pageQuery);

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
     * @Date 2023/5/18
     * @param openid
     * @return SysUserVo
     **/
//    SysUserVo selectUserByOpenId(String openId);
    SysUserVo selectUserByOpenId(String openid);

    /**
     * unionid
     *
     * @Date 2023/5/18
     * @param unionid
     * @return SysUserVo
     **/
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
     * @param inviteCode 手机号
     * @return 用户对象信息
     */
    public SysUserVo selectUserByInviteCode(String inviteCode);
    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    SysUserVo selectUserById(Long userId);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户对象信息
     */
    SysUserVo selectUserByEmail(String email);

    /**
     * 根据用户ID查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    String selectUserRoleGroup(String userName);

    /**
     * 根据用户ID查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    String selectUserPostGroup(String userName);

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean checkUserNameUnique(SysUserBo user);

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean checkPhoneUnique(SysUserBo user);

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean checkEmailUnique(SysUserBo user);

    /**
     * 校验用户是否允许操作
     *
     * @param userId 用户ID
     */
    void checkUserAllowed(Long userId);

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    void checkUserDataScope(Long userId);

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int insertUser(SysUserBo user);

    /**
     * 注册用户信息
     *
     * @Date 2023/5/18
     * @param user
     * @param tenantId
     * @param inviteCode
     * @return SysUser
     **/
    SysUser registerUser(SysUserBo user, String tenantId,String inviteCode);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUser(SysUserBo user);

    /**
     * 小程序 - 修改用户信息
     *
     * @param user 用户信息
     *
     */
    SysUserVo updateXcxUser(SysUserBo user);

    /**
     * 为新用户赠送会员
     * 针对2025-10-12到2025-11-12期间注册的用户，自动赠送一个月会员
     *
     * @param userId 用户ID
     * @return 赠送结果信息
     */
    String grantNewUserVip(Long userId);

    /**
     * 批量为符合条件的新用户赠送会员
     *
     * @return 批量处理结果
     */
    String batchGrantNewUserVip();

    /**
     * 用户授权角色
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    void insertUserAuth(Long userId, Long[] roleIds);

    /**
     * 修改用户状态
     *
     * @param userId 用户ID
     * @param status 帐号状态
     * @return 结果
     */
    int updateUserStatus(Long userId, String status);

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUserProfile(SysUserBo user);

    /**
     * 修改用户头像
     *
     * @param userId 用户ID
     * @param avatar 头像地址
     * @return 结果
     */
    boolean updateUserAvatar(Long userId, String avatar);

    boolean updateUserName(Long userId, String nickName);

    /**
     * 重置用户密码
     *
     * @param userId   用户ID
     * @param password 密码
     * @return 结果
     */

    int resetUserPwd(Long userId, String password);



    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deleteUserById(Long userId);

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    int deleteUserByIds(Long[] userIds);

    void syncAllOrgAndUsers();


    /**
     * 注意: 此方法仅用于测试,功能完善后需要屏蔽
     *
     * @return 操作结果信息
     */
    String testSaveOrgAndUsers();
}
