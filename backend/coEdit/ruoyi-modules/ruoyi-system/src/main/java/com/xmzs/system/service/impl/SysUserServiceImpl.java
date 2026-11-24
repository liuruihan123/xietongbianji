package com.xmzs.system.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.common.core.config.SsoConfig;
import com.xmzs.common.core.constant.CacheNames;
import com.xmzs.common.core.constant.UserConstants;
import com.xmzs.common.core.exception.ServiceException;
import com.xmzs.common.core.service.UserService;
import com.xmzs.common.core.utils.DateUtils;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StreamUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.config.CustomSnowflakeIdGenerator;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.helper.DataBaseHelper;
import com.xmzs.common.satoken.utils.LoginHelper;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import com.xmzs.system.domain.SysDept;
import com.xmzs.system.domain.SysUser;
import com.xmzs.system.domain.SysUserPost;
import com.xmzs.system.domain.SysUserRole;
import com.xmzs.system.domain.bo.SysUserBo;
import com.xmzs.system.domain.vo.SysDeptVo;
import com.xmzs.system.domain.vo.SysPostVo;
import com.xmzs.system.domain.vo.SysRoleVo;
import com.xmzs.system.domain.vo.SysUserVo;
import com.xmzs.system.mapper.*;
import com.xmzs.system.service.ISysUserService;
import com.xmzs.system.service.SysLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 用户 业务层处理
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysUserServiceImpl implements ISysUserService, UserService {

    private final SysUserMapper baseMapper;
    private final SysDeptMapper deptMapper;
    private final SysRoleMapper roleMapper;
    private final SysPostMapper postMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysUserPostMapper userPostMapper;

    @Override
    public TableDataInfo<SysUserVo> selectPageUserList(SysUserBo user, PageQuery pageQuery) {
        Page<SysUserVo> page = baseMapper.selectPageUserList(pageQuery.build(), this.buildQueryWrapper(user));
        return TableDataInfo.build(page);
    }

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUserVo> selectUserList(SysUserBo user) {
        return baseMapper.selectUserList(this.buildQueryWrapper(user));
    }

    private Wrapper<SysUser> buildQueryWrapper(SysUserBo user) {
        Map<String, Object> params = user.getParams();
        QueryWrapper<SysUser> wrapper = Wrappers.query();
        wrapper.eq("u.del_flag", UserConstants.USER_NORMAL)
            .eq(ObjectUtil.isNotNull(user.getUserId()), "u.user_id", user.getUserId())
            .eq(ObjectUtil.isNotNull(user.getUserGrade()), "u.user_grade", user.getUserGrade())
            .like(StringUtils.isNotBlank(user.getUserName()), "u.user_name", user.getUserName())
            .eq(StringUtils.isNotBlank(user.getStatus()), "u.status", user.getStatus())
            .like(StringUtils.isNotBlank(user.getPhonenumber()), "u.phonenumber", user.getPhonenumber())
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                "u.create_time", params.get("beginTime"), params.get("endTime"))
            .and(ObjectUtil.isNotNull(user.getDeptId()), w -> {
                List<SysDept> deptList = deptMapper.selectList(new LambdaQueryWrapper<SysDept>()
                    .select(SysDept::getDeptId)
                    .apply(DataBaseHelper.findInSet(user.getDeptId(), "ancestors")));
                List<Long> ids = StreamUtils.toList(deptList, SysDept::getDeptId);
                ids.add(user.getDeptId());
                w.in("u.dept_id", ids);
            }).orderByDesc("u.create_time");
        return wrapper;
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public TableDataInfo<SysUserVo> selectAllocatedList(SysUserBo user, PageQuery pageQuery) {
        QueryWrapper<SysUser> wrapper = Wrappers.query();
        wrapper.eq("u.del_flag", UserConstants.USER_NORMAL)
            .eq(ObjectUtil.isNotNull(user.getRoleId()), "r.role_id", user.getRoleId())
            .like(StringUtils.isNotBlank(user.getUserName()), "u.user_name", user.getUserName())
            .eq(StringUtils.isNotBlank(user.getStatus()), "u.status", user.getStatus())
            .like(StringUtils.isNotBlank(user.getPhonenumber()), "u.phonenumber", user.getPhonenumber());
        Page<SysUserVo> page = baseMapper.selectAllocatedList(pageQuery.build(), wrapper);
        return TableDataInfo.build(page);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public TableDataInfo<SysUserVo> selectUnallocatedList(SysUserBo user, PageQuery pageQuery) {
        List<Long> userIds = userRoleMapper.selectUserIdsByRoleId(user.getRoleId());
        QueryWrapper<SysUser> wrapper = Wrappers.query();
        wrapper.eq("u.del_flag", UserConstants.USER_NORMAL)
            .and(w -> w.ne("r.role_id", user.getRoleId()).or().isNull("r.role_id"))
            .notIn(CollUtil.isNotEmpty(userIds), "u.user_id", userIds)
            .like(StringUtils.isNotBlank(user.getUserName()), "u.user_name", user.getUserName())
            .like(StringUtils.isNotBlank(user.getPhonenumber()), "u.phonenumber", user.getPhonenumber());
        Page<SysUserVo> page = baseMapper.selectUnallocatedList(pageQuery.build(), wrapper);
        return TableDataInfo.build(page);
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUserVo selectUserByUserName(String userName) {
        return baseMapper.selectUserByUserName(userName);
    }

    /**
     * 通过OpenId查询用户
     *
     * @param openid 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUserVo selectUserByOpenId(String openid) {
        return baseMapper.selectUserByOpenId(openid);
    }


    /**
     * 通过unionid查询用户
     *
     * @param unionid unionid
     * @return 用户对象信息
     */
    @Override
    public SysUserVo selectUserByUnionid(String unionid) {
        return baseMapper.selectUserByUnionid(unionid);
    }




    /**
     * 通过手机号查询用户
     *
     * @param phonenumber 手机号
     * @return 用户对象信息
     */
    @Override
    public SysUserVo selectUserByPhonenumber(String phonenumber) {
        return baseMapper.selectUserByPhonenumber(phonenumber);
    }

    /**
     * 通过邀请码查询用户
     *
     * @param inviteCode 手机号
     * @return 用户对象信息
     */
    @Override
    public SysUserVo selectUserByInviteCode(String inviteCode) {
        return baseMapper.selectUserByInviteCode(inviteCode);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUserVo selectUserById(Long userId) {
        return baseMapper.selectUserById(userId);
    }

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户对象信息
     */
    @Override
    public SysUserVo selectUserByEmail(String email) {
        LambdaQueryWrapper<SysUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysUser::getEmail, email);
        SysUser user = baseMapper.selectOne(lqw);
        if (ObjectUtil.isNull(user)) {
            return null;
        }
        return MapstructUtils.convert(user, SysUserVo.class);
    }

    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName) {
        List<SysRoleVo> list = roleMapper.selectRolesByUserName(userName);
        if (CollUtil.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return StreamUtils.join(list, SysRoleVo::getRoleName);
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(String userName) {
        List<SysPostVo> list = postMapper.selectPostsByUserName(userName);
        if (CollUtil.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return StreamUtils.join(list, SysPostVo::getPostName);
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean checkUserNameUnique(SysUserBo user) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysUser>()
            .eq(SysUser::getUserName, user.getUserName())
            .ne(ObjectUtil.isNotNull(user.getUserId()), SysUser::getUserId, user.getUserId()));
        return !exist;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     */
    @Override
    public boolean checkPhoneUnique(SysUserBo user) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysUser>()
            .eq(SysUser::getPhonenumber, user.getPhonenumber())
            .ne(ObjectUtil.isNotNull(user.getUserId()), SysUser::getUserId, user.getUserId()));
        return !exist;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     */
    @Override
    public boolean checkEmailUnique(SysUserBo user) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysUser>()
            .eq(SysUser::getEmail, user.getEmail())
            .ne(ObjectUtil.isNotNull(user.getUserId()), SysUser::getUserId, user.getUserId()));
        return !exist;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param userId 用户ID
     */
    @Override
    public void checkUserAllowed(Long userId) {
        if (ObjectUtil.isNotNull(userId) && LoginHelper.isSuperAdmin(userId)) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(Long userId) {
        if (ObjectUtil.isNull(userId)) {
            return;
        }
        if (LoginHelper.isSuperAdmin()) {
            return;
        }
        if (ObjectUtil.isNull(baseMapper.selectUserById(userId))) {
            throw new ServiceException("没有权限访问用户数据！");
        }
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(SysUserBo user) {
        user.setInviteCode(RandomUtil.randomString(6));
        SysUser sysUser = MapstructUtils.convert(user, SysUser.class);
        if(StringUtils.isEmpty(sysUser.getInviteCode())){
            sysUser.setInviteCode(RandomUtil.randomString(6));
        }
        // 新增用户信息
        int rows = baseMapper.insert(sysUser);
        user.setUserId(sysUser.getUserId());
        if(sysUser.getUserGrade()==null){
            // 新增用户岗位关联
            insertUserPost(user, false);
            // 新增用户与角色管理
            insertUserRole(user, false);
        }

        return rows;
    }

    /**
     * 注册用户信息  增加邀请注册信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public SysUser registerUser(SysUserBo user, String tenantId,String inviteCode) {
        user.setCreateBy(user.getUserId());
        user.setUpdateBy(user.getUserId());
        SysUser sysUser = MapstructUtils.convert(user, SysUser.class);
        if (sysUser != null) {
            sysUser.setTenantId(tenantId);
        }
        if(StringUtils.isEmpty(sysUser.getInviteCode())){
            sysUser.setInviteCode(RandomUtil.randomString(6));
        }
        baseMapper.insert(sysUser);

        return sysUser;
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(SysUserBo user) {
        // 新增用户与角色管理
        //insertUserRole(user, true);
        // 新增用户与岗位管理
        //insertUserPost(user, true);
        SysUser sysUser = MapstructUtils.convert(user, SysUser.class);
        // 防止错误更新后导致的数据误删除
        int flag = baseMapper.updateById(sysUser);
        if (flag < 1) {
            throw new ServiceException("修改用户" + user.getUserName() + "信息失败");
        }
        return flag;
    }

    /**
     * 小程序 - 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public SysUserVo updateXcxUser(SysUserBo user) {
        baseMapper.updateXcxUser(user);
//        return baseMapper.selectUserByOpenId(user.getOpenid());
        SysUserVo userVo = baseMapper.selectUserByUnionid(user.getUnionid());
        if(ObjectUtil.isEmpty(userVo)){
            userVo = baseMapper.selectUserByOpenId(user.getOpenid());
        }
        return userVo;
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUserAuth(Long userId, Long[] roleIds) {
        insertUserRole(userId, roleIds, true);
    }

    /**
     * 修改用户状态
     *
     * @param userId 用户ID
     * @param status 帐号状态
     * @return 结果
     */
    @Override
    public int updateUserStatus(Long userId, String status) {
        return baseMapper.update(null,
            new LambdaUpdateWrapper<SysUser>()
                .set(SysUser::getStatus, status)
                .eq(SysUser::getUserId, userId));
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUserBo user) {
        return baseMapper.update(null,
            new LambdaUpdateWrapper<SysUser>()
                .set(ObjectUtil.isNotNull(user.getNickName()), SysUser::getNickName, user.getNickName())
                .set(SysUser::getPhonenumber, user.getPhonenumber())
                .set(SysUser::getEmail, user.getEmail())
                .set(SysUser::getSex, user.getSex())
                .eq(SysUser::getUserId, user.getUserId()));
    }

    /**
     * 修改用户头像
     *
     * @param userId 用户ID
     * @param avatar 头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(Long userId, String avatar) {
        return baseMapper.update(null,
            new LambdaUpdateWrapper<SysUser>()
                .set(SysUser::getAvatar, avatar)
                .eq(SysUser::getUserId, userId)) > 0;
    }

    @Override
    public boolean updateUserName(Long userId, String nickName) {
        return baseMapper.update(null,
            new LambdaUpdateWrapper<SysUser>()
                .set(SysUser::getNickName, nickName)
                .eq(SysUser::getUserId, userId)) > 0;
    }

    /**
     * 重置用户密码
     *
     * @param userId   用户ID
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(Long userId, String password) {
        return baseMapper.update(null,
            new LambdaUpdateWrapper<SysUser>()
                .set(SysUser::getPassword, password)
                .eq(SysUser::getUserId, userId));
    }


    /**
     * 新增用户角色信息
     *
     * @param user  用户对象
     * @param clear 清除已存在的关联数据
     */
    private void insertUserRole(SysUserBo user, boolean clear) {
        this.insertUserRole(user.getUserId(), user.getRoleIds(), clear);
    }

    /**
     * 新增用户岗位信息
     *
     * @param user  用户对象
     * @param clear 清除已存在的关联数据
     */
    private void insertUserPost(SysUserBo user, boolean clear) {
        Long[] posts = user.getPostIds();
        if (ArrayUtil.isNotEmpty(posts)) {
            if (clear) {
                // 删除用户与岗位关联
                userPostMapper.delete(new LambdaQueryWrapper<SysUserPost>().eq(SysUserPost::getUserId, user.getUserId()));
            }
            // 新增用户与岗位管理
            List<SysUserPost> list = StreamUtils.toList(List.of(posts), postId -> {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                return up;
            });
            userPostMapper.insertBatch(list);
        }
    }

    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     * @param clear   清除已存在的关联数据
     */
    private void insertUserRole(Long userId, Long[] roleIds, boolean clear) {
        if (ArrayUtil.isNotEmpty(roleIds)) {
            // 判断是否具有此角色的操作权限
            List<SysRoleVo> roles = roleMapper.selectRoleList(new LambdaQueryWrapper<>());
            if (CollUtil.isEmpty(roles)) {
                throw new ServiceException("没有权限访问角色的数据");
            }
            List<Long> roleList = StreamUtils.toList(roles, SysRoleVo::getRoleId);
            if (!LoginHelper.isSuperAdmin(userId)) {
                roleList.remove(UserConstants.SUPER_ADMIN_ID);
            }
            List<Long> canDoRoleList = StreamUtils.filter(List.of(roleIds), roleList::contains);
            if (CollUtil.isEmpty(canDoRoleList)) {
                throw new ServiceException("没有权限访问角色的数据");
            }
            if (clear) {
                // 删除用户与角色关联
                userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
            }
            // 新增用户与角色管理
            List<SysUserRole> list = StreamUtils.toList(canDoRoleList, roleId -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                return ur;
            });
            userRoleMapper.insertBatch(list);
        }
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserById(Long userId) {
        // 删除用户与角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        // 删除用户与岗位表
        userPostMapper.delete(new LambdaQueryWrapper<SysUserPost>().eq(SysUserPost::getUserId, userId));
        // 防止更新失败导致的数据删除
        int flag = baseMapper.deleteById(userId);
        if (flag < 1) {
            throw new ServiceException("删除用户失败!");
        }
        return flag;
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            checkUserAllowed(userId);
            checkUserDataScope(userId);
        }
        List<Long> ids = List.of(userIds);
        // 删除用户与角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, ids));
        // 删除用户与岗位表
        userPostMapper.delete(new LambdaQueryWrapper<SysUserPost>().in(SysUserPost::getUserId, ids));
        // 防止更新失败导致的数据删除
        int flag = baseMapper.deleteBatchIds(ids);
        if (flag < 1) {
            throw new ServiceException("删除用户失败!");
        }
        return flag;
    }

    @Override
    public void syncAllOrgAndUsers() {
        log.info("开始同步所有组织及用户信息...");
        try {
            String visitToken = fetchVisitTokenFromRemote();
            if (visitToken == null) {
                throw new ServiceException("无法获取 visit_token，同步组织及用户信息中止");
            }

            Map<String, Object> params = new HashMap<>();
            params.put("visit_token", visitToken);
            params.put("system_id", ssoConfig.getSystemId());

            // 设置请求超时（10秒）
            String resultStr = HttpUtil.get(ssoConfig.getAllOrgUserUrl(), params, 10000);
            log.debug("获取组织及用户信息响应: {}", resultStr);

            JSONObject json = JSONObject.parseObject(resultStr);
            if (!"0".equals(json.getString("code"))) {
                String msg = json.getString("msg");
                log.warn("获取组织及用户信息失败: {}", msg);
                throw new ServiceException("获取组织及用户信息失败：" + msg);
            }

            saveOrgInfo(json);
            saveUserInfo(json);
            log.info("同步组织及用户信息完成");
        } catch (ServiceException e) {
            log.error("同步组织及用户信息失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("同步组织及用户信息发生未知异常", e);
            throw new ServiceException("同步组织及用户信息异常，请检查日志");
        }
    }
    // ------------------ 保存组织 ------------------
    private void saveOrgInfo(JSONObject rootJson) {
        JSONArray orgs = rootJson.getJSONObject("data").getJSONArray("orgs");
        if (orgs == null || orgs.isEmpty()) return;
        CustomSnowflakeIdGenerator idGenerator = new CustomSnowflakeIdGenerator(0, 0);
        for (Object org : orgs) {
            JSONObject orgJson = (JSONObject) org;
            String id = orgJson.getString("id");
            if (StringUtils.isBlank(id)) continue;
            String parentId = orgJson.getString("parentId");
            if ("-1".equals(parentId)) parentId = "0";

            // 查询组织是否存在
            Long deptId = deptMapper.selectDeptIdByOrgId(id);

            SysDept dept = new SysDept();
            dept.setOrgId(id);
            dept.setParentOrdId(parentId);
            dept.setDeptName(orgJson.getString("orgFullName"));
            dept.setOrderNum(orgJson.getInteger("sortnumber"));
            dept.setUpdateTime(DateUtils.getNowDate());

            if (deptId != null) {
                // 组织存在，执行更新
                dept.setDeptId(deptId);
                deptMapper.updateById(dept);
                log.info("组织[orgId={}]已存在，更新信息", id);
            } else {
                // 组织不存在，执行插入
                Number number = idGenerator.nextId(dept);
                dept.setDeptId(number.longValue());
                dept.setCreateTime(DateUtils.getNowDate());
                deptMapper.insert(dept);
                log.info("组织[orgId={}]不存在，新增组织", id);
            }
        }
    }
    // ------------------ 保存用户 ------------------
    private void saveUserInfo(JSONObject rootJson) {
        JSONArray users = rootJson.getJSONObject("data").getJSONArray("users");
        if (users == null || users.isEmpty()) return;
        for (Object user : users) {
            JSONObject userJson = (JSONObject) user;

            String acc = userJson.getString("personLoginName");
            if (StringUtils.isBlank(acc)) continue;
            String dn = userJson.getString("dn");
            String orgId = parseOrgIdFromDn(dn);

            // 通过外部组织ID（UUID）查询内部部门ID
            Long deptId = deptMapper.selectDeptIdByOrgId(orgId);
            if (deptId == null) {
                throw new ServiceException(String.format(
                    "用户 [%s] 所属组织 [%s] 在系统中不存在，请先同步组织数据！",
                    acc, orgId
                ));
            }

            // 查询用户是否存在
            SysUser existingUser = baseMapper.selectOne(
                Wrappers.lambdaQuery(SysUser.class)
                    .eq(SysUser::getUserName, acc)
                    .last("LIMIT 1")
            );

            SysUser userInfo = new SysUser();
            userInfo.setUserName(acc);
            userInfo.setNickName(userJson.getString("personTrueName"));
            userInfo.setOpenid(userJson.getString("personOpenId"));
            userInfo.setPhonenumber(userJson.getString("phoneNum"));
            userInfo.setEmail(userJson.getString("mail"));
            userInfo.setDeptId(deptId);
            userInfo.setSsoOrgId(orgId);
            String sex = userJson.getString("sex");
            userInfo.setSex(String.valueOf(StringUtils.isBlank(sex) || "null".equals(sex) ? 1 : Integer.parseInt(sex)));

            if (existingUser != null) {
                // 用户存在，执行更新
                userInfo.setUserId(existingUser.getUserId());
                baseMapper.updateById(userInfo);
                log.info("用户[userName={}]已存在，更新信息", acc);
            } else {
                // 用户不存在，执行插入
                userInfo.setPassword(BCrypt.hashpw("123456", BCrypt.gensalt()));
                // 系统必填冗余字段
                userInfo.setSubMsgCount(1000);
                baseMapper.insert(userInfo);
                log.info("用户[userName={}]不存在，新增用户", acc);
            }
        }
    }

    // 从 DN 字符串提取组织 ID（兼容原逻辑）
    private String parseOrgIdFromDn(String dn) {
        if (StringUtils.isBlank(dn)) return "";
        String[] parts = dn.split(",");
        if (parts.length < 3) return "";

        StringBuilder orgId = new StringBuilder();
        for (int i = 2; i < parts.length; i++) {
            if (i == 2) {
                orgId.append(parts[i]).append("@");
            } else if (i == parts.length - 1) {
                orgId.append(parts[i]);
            } else {
                orgId.append(parts[i]).append(".");
            }
        }
        return orgId.toString();
    }

    /**
     * 从classpath加载测试数据文件
     *
     * @param filePath 文件路径（相对于classpath）
     * @return 文件内容字符串，读取失败返回null
     */
    private String loadTestDataFromFile(String filePath) {
        String textContent = null;
        try {
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                log.error("测试数据文件不存在: {}", filePath);
                return null;
            }

            // 读取文件内容
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.FileReader(file)
            );

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();

            textContent = content.toString();
        } catch (Exception e) {
            log.error("读取测试数据文件失败: {}", filePath, e);
        }
        return textContent;
    }

    // ------------------ 获取配置文件 ------------------
    private final SsoConfig ssoConfig;
    // ------------------ 获取票据 ------------------
    private String fetchVisitTokenFromRemote() {
        try {
            HttpResponse response = HttpRequest.post(ssoConfig.getVisitTokenUrl())
                .form("client_id", ssoConfig.getClientId())
                .form("client_secret", ssoConfig.getClientSecret())
                .timeout(3000)
                .execute();

            String result = response.body();
            log.debug("获取 visit_token 响应: {}", result);

            JSONObject json = JSONObject.parseObject(result);
            if ("0".equals(json.getString("code"))) {
                return json.getString("visit_token");
            } else {
                log.warn("获取 visit_token 失败: {}", result);
            }
        } catch (Exception e) {
            log.error("获取 visit_token 异常", e);
        }
        return null;
    }

    @Cacheable(cacheNames = CacheNames.SYS_USER_NAME, key = "#userId")
    @Override
    public String selectUserNameById(Long userId) {
        SysUser sysUser = baseMapper.selectOne(new LambdaQueryWrapper<SysUser>()
            .select(SysUser::getUserName).eq(SysUser::getUserId, userId));
        return ObjectUtil.isNull(sysUser) ? null : sysUser.getUserName();
    }

    /**
     * 为新用户赠送会员
     * 针对2025-10-12到2025-11-12期间注册的用户，自动赠送一个月会员
     *
     * @param userId 用户ID
     * @return 赠送结果信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String grantNewUserVip(Long userId) {
        if (ObjectUtil.isNull(userId)) {
            return "用户ID不能为空";
        }

        // 查询用户信息
        SysUserVo user = baseMapper.selectUserById(userId);
        if (ObjectUtil.isNull(user)) {
            return "用户不存在";
        }

        // 检查用户注册时间是否在指定范围内（2025-10-12 到 2025-11-12）
        if (ObjectUtil.isNull(user.getCreateTime())) {
            return "用户注册时间为空，无法判断是否符合赠送条件";
        }

        LocalDateTime registerTime = user.getCreateTime().toInstant()
            .atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime startDate = LocalDateTime.of(2025, 10, 12, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 12, 12, 23, 59, 59);

        if (registerTime.isBefore(startDate) || registerTime.isAfter(endDate)) {
            return String.format("用户注册时间为%s，不在赠送范围内（2025-10-12 至 2025-11-12）",
                registerTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        // 检查用户是否已经是会员（避免重复赠送）
        if ("1".equals(user.getUserGrade())) {
            // 检查会员是否还有效
            if (ObjectUtil.isNotNull(user.getVipExpiryDate()) && user.getVipExpiryDate() > System.currentTimeMillis()) {
                return "用户已经是有效会员，无需重复赠送";
            }
        }

        // 计算新的会员到期时间（当前时间 + 30天）
        long currentTime = System.currentTimeMillis();
        long oneMonthInMillis = 30L * 24 * 60 * 60 * 1000; // 30天的毫秒数
        long newVipExpiryDate = currentTime + oneMonthInMillis;

        // 更新用户会员信息
        int updateResult = baseMapper.update(null,
            new LambdaUpdateWrapper<SysUser>()
                .set(SysUser::getUserGrade, "1")
                .set(SysUser::getVipExpiryDate, newVipExpiryDate)
                .eq(SysUser::getUserId, userId));

        if (updateResult > 0) {
            LocalDateTime expiryDateTime = LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(newVipExpiryDate), ZoneId.systemDefault());
            log.info("成功为用户[{}]赠送一个月会员，到期时间：{}", userId,
                expiryDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return String.format("成功赠送一个月会员！会员到期时间：%s",
                expiryDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            return "赠送会员失败，请稍后重试";
        }
    }

    /**
     * 批量为符合条件的新用户赠送会员
     *
     * @return 批量处理结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String batchGrantNewUserVip() {
        // 查询2025-10-12到2025-11-12期间注册的用户
        LocalDateTime startDate = LocalDateTime.of(2025, 10, 12, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 11, 12, 23, 59, 59);

        List<SysUser> eligibleUsers = baseMapper.selectList(
            new LambdaQueryWrapper<SysUser>()
                .ge(SysUser::getCreateTime, startDate)
                .le(SysUser::getCreateTime, endDate)
                .ne(SysUser::getUserGrade, "1") // 排除已经是会员的用户
                .eq(SysUser::getDelFlag, "0") // 只查询未删除的用户
        );

        if (CollUtil.isEmpty(eligibleUsers)) {
            return "没有找到符合条件的用户";
        }

        int successCount = 0;
        int failCount = 0;
        StringBuilder resultBuilder = new StringBuilder();

        for (SysUser user : eligibleUsers) {
            try {
                String result = grantNewUserVip(user.getUserId());
                if (result.contains("成功")) {
                    successCount++;
                } else {
                    failCount++;
                    resultBuilder.append(String.format("用户[%s]处理失败：%s\n", user.getUserId(), result));
                }
            } catch (Exception e) {
                failCount++;
                resultBuilder.append(String.format("用户[%s]处理异常：%s\n", user.getUserId(), e.getMessage()));
                log.error("为用户[{}]赠送会员时发生异常", user.getUserId(), e);
            }
        }

        String summary = String.format("批量处理完成！总计：%d个用户，成功：%d个，失败：%d个",
            eligibleUsers.size(), successCount, failCount);

        if (failCount > 0) {
            summary += "\n失败详情：\n" + resultBuilder.toString();
        }

        log.info("批量赠送会员完成：{}", summary);
        return summary;
    }


   public static void main(String[] args) {
    String filePath = "E:\\my_project\\coEdit\\user.json";

    try {
        java.io.File file = new java.io.File(filePath);
        if (!file.exists()) {
            log.error("测试数据文件不存在: {}", filePath);
            return;
        }

        // 读取文件内容
        java.io.BufferedReader reader = new java.io.BufferedReader(
            new java.io.FileReader(file)
        );

        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        reader.close();

        System.out.println(content.toString());
    } catch (Exception e) {
        log.error("读取测试数据文件失败: {}", filePath, e);
    }
}


    //----------------

    /**
     * 测试保存组织和用户信息(硬编码数据)
     * 注意: 此方法仅用于测试,功能完善后需要屏蔽
     *
     * @return 操作结果信息
     */
    @Override
    public String testSaveOrgAndUsers() {
        log.info("开始执行测试保存组织和用户信息...");
        try {
            // 从classpath读取测试数据文件
            String jsonStr = loadTestDataFromFile("E:\\my_project\\coEdit\\user.json");
            if (jsonStr == null) {
                return "测试数据文件读取失败";
            }

            // 解析JSON
            JSONObject json = JSONObject.parseObject(jsonStr);

            // 验证JSON格式
            if (!"0".equals(json.getString("code"))) {
                String msg = json.getString("message");
                log.warn("JSON数据code不为0: {}", msg);
                return "JSON数据格式错误: " + msg;
            }

            // 调用保存组织信息方法
            saveOrgInfo(json);
            log.info("组织信息保存完成");

            // 调用保存用户信息方法
            saveUserInfo(json);
            log.info("用户信息保存完成");

            return "测试数据保存成功! 已保存2个组织和2个用户";
        } catch (Exception e) {
            log.error("测试保存组织和用户信息失败", e);
            return "保存失败: " + e.getMessage();
        }
    }




}



