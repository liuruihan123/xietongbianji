package com.xmzs.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.core.utils.DateUtils;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StreamUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.excel.core.ExcelResult;
import com.xmzs.common.excel.utils.ExcelUtil;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.helper.DataPermissionHelper;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.common.tenant.helper.TenantHelper;
import com.xmzs.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xmzs.system.domain.SysUser;
import com.xmzs.system.domain.bo.SysDeptBo;
import com.xmzs.system.domain.bo.SysUserBo;
import com.xmzs.system.domain.request.UserRequest;
import com.xmzs.system.domain.vo.*;
import com.xmzs.system.listener.SysUserImportListener;
import com.xmzs.system.mapper.SysUserMapper;
import com.xmzs.system.service.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {

    private final ISysUserService userService;
    private final ISysRoleService roleService;
    private final ISysPostService postService;
    private final ISysDeptService deptService;
    private final ISysTenantService tenantService;
    private final ISysOssService ossService;
    private final SysUserMapper sysUserMapper;
    /**
     * 获取用户列表
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/list")
    public TableDataInfo<SysUserVo> list(SysUserBo user, PageQuery pageQuery) {
        return userService.selectPageUserList(user, pageQuery);
    }

    /**
     * 搜索用户(用于邀请成员)
     * 不需要特殊权限，所有登录用户都可以搜索
     */
    @GetMapping("/search")
    public TableDataInfo<SysUserVo> searchUsers(@RequestParam(required = false) String keyword, PageQuery pageQuery) {
        // 如果 keyword 为空，返回空列表
        if (keyword == null || keyword.trim().isEmpty()) {
            return TableDataInfo.build(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>());
        }

        final String finalKeyword = keyword.trim();

        return DataPermissionHelper.ignore(() -> {
            // 查询用户
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser> wrapper = com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery();
            wrapper.eq(SysUser::getDelFlag, com.xmzs.common.core.constant.UserConstants.USER_NORMAL)
                .and(w -> w.like(SysUser::getUserName, "%" + finalKeyword + "%").or().like(SysUser::getNickName, "%" + finalKeyword + "%"))
                .orderByDesc(SysUser::getCreateTime);

            com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> page = pageQuery.build();
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> userPage = sysUserMapper.selectPage(page, wrapper);

            // 转换为 VO
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUserVo> voPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
            voPage.setRecords(com.xmzs.common.core.utils.MapstructUtils.convert(userPage.getRecords(), SysUserVo.class));

            return com.xmzs.common.mybatis.core.page.TableDataInfo.build(voPage);
        });
    }

    /**
     * 导出用户列表
     */
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:user:export")
    @PostMapping("/export")
    public void export(SysUserBo user, HttpServletResponse response) {
        List<SysUserVo> list = userService.selectUserList(user);
        List<SysUserExportVo> listVo = MapstructUtils.convert(list, SysUserExportVo.class);
        ExcelUtil.exportExcel(listVo, "用户数据", SysUserExportVo.class, response);
    }

    /**
     * 导入数据
     *
     * @param file          导入文件
     * @param updateSupport 是否更新已存在数据
     */
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @SaCheckPermission("system:user:import")
    @PostMapping(value = "/importData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> importData(@RequestPart("file") MultipartFile file, boolean updateSupport) throws Exception {
        ExcelResult<SysUserImportVo> result = ExcelUtil.importExcel(file.getInputStream(), SysUserImportVo.class, new SysUserImportListener(updateSupport));
        return R.ok(result.getAnalysis());
    }

    /**
     * 获取导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "用户数据", SysUserImportVo.class, response);
    }


    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getInfo")
    public R<LoginVo> getInfo() {
//        UserInfoVo userInfoVo = new UserInfoVo();
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (TenantHelper.isEnable() && LoginHelper.isSuperAdmin()) {
            // 超级管理员 如果重新加载用户信息需清除动态租户
            TenantHelper.clearDynamic();
        }
        if(loginUser.getUserId()==null){
            throw new NotLoginException("no login","","");
        }
        SysUserVo user = userService.selectUserById(loginUser.getUserId());
        if(ObjectUtil.isNull(user)){
            throw new NotLoginException("user does not exist....","","");
        }
//        }
//        userInfoVo.setUser(user);
//        userInfoVo.setPermissions(loginUser.getMenuPermission());
//        userInfoVo.setRoles(loginUser.getRolePermission());


        LoginVo loginVo = new LoginVo();
        loginVo.setToken(loginUser.getToken());
        loginVo.setUserInfo(LoginHelper.getLoginUser());

        return R.ok(loginVo);
    }



    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getInfo2")
    public R<UserInfoVo> getInfo2() {
        UserInfoVo userInfoVo = new UserInfoVo();
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (TenantHelper.isEnable() && LoginHelper.isSuperAdmin()) {
            // 超级管理员 如果重新加载用户信息需清除动态租户
            TenantHelper.clearDynamic();
        }
        if(loginUser.getUserId()==null){
            throw new NotLoginException("no login","","");
        }
        SysUserVo user = userService.selectUserById(loginUser.getUserId());
        if(ObjectUtil.isNull(user)){
            throw new NotLoginException("user does not exist....","","");
        }
        //检测会员是否过期
//        if("1".equals(user.getUserGrade())){
//           Long vipExpiryDate =  user.getVipExpiryDate();
//           if(vipExpiryDate==null){
//               user.setUserGrade("0");
//               sysUserMapper.update(null,
//                   new LambdaUpdateWrapper<SysUser>()
//                       .set(SysUser::getUserGrade, "0")
//                       .eq(SysUser::getUserId, loginUser.getUserId()));
//           }else{
//               boolean hasExpiey = DateUtils.isExpired(vipExpiryDate);
//               if(hasExpiey){
//                   user.setUserGrade("0");
//                   sysUserMapper.update(null,
//                       new LambdaUpdateWrapper<SysUser>()
//                           .set(SysUser::getUserGrade, "0")
//                           .eq(SysUser::getUserId, loginUser.getUserId()));
//               }
//
//           }
//
//        }
        userInfoVo.setUser(user);
        userInfoVo.setPermissions(loginUser.getMenuPermission());
        userInfoVo.setRoles(loginUser.getRolePermission());

        return R.ok(userInfoVo);
    }

    /**
     * 根据用户编号获取详细信息
     *
     * @param userId 用户ID
     */
    @SaCheckPermission("system:user:query")
    @GetMapping(value = {"/", "/{userId}"})
    public R<SysUserInfoVo> getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        userService.checkUserDataScope(userId);
        SysUserInfoVo userInfoVo = new SysUserInfoVo();
        List<SysRoleVo> roles = roleService.selectRoleAll();
        userInfoVo.setRoles(LoginHelper.isSuperAdmin(userId) ? roles : StreamUtils.filter(roles, r -> !r.isSuperAdmin()));
        userInfoVo.setPosts(postService.selectPostAll());
        if (ObjectUtil.isNotNull(userId)) {
            SysUserVo sysUser = userService.selectUserById(userId);
            userInfoVo.setUser(sysUser);
            userInfoVo.setRoleIds(StreamUtils.toList(sysUser.getRoles(), SysRoleVo::getRoleId));
            userInfoVo.setPostIds(postService.selectPostListByUserId(userId));
        }
        return R.ok(userInfoVo);
    }

    /**
     * 新增用户
     */
    @SaCheckPermission("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysUserBo user) {
        if (!userService.checkUserNameUnique(user)) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        if (TenantHelper.isEnable()) {
            if (!tenantService.checkAccountBalance(TenantHelper.getTenantId())) {
                return R.fail("当前租户下用户名额不足，请联系管理员");
            }
        }
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysUserBo user) {
        userService.checkUserAllowed(user.getUserId());
        userService.checkUserDataScope(user.getUserId());
        if (!userService.checkUserNameUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        return toAjax(userService.updateUser(user));
    }

    /**
     * 修改用户名称
     */
    @Log(title = "修改用户名称", businessType = BusinessType.UPDATE)
    @PostMapping("/editName")
    public R<Void> editName(@RequestBody @Validated  UserRequest userRequest) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        userService.updateUserName(loginUser.getUserId(), userRequest.getNickName());
        return R.ok("操作成功!");
    }

    /**
     * 修改用户头像
     */
    @Log(title = "修改用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/edit/avatar")
    public R<String> editAvatar(@RequestPart("file") MultipartFile file) {
        if (ObjectUtil.isNull(file)) {
            return R.fail("上传文件不能为空");
        }
        LoginUser loginUser = LoginHelper.getLoginUser();
        // 获取当前登录用户
        SysOssVo oss = ossService.upload(file);
        userService.updateUserAvatar(loginUser.getUserId(), oss.getUrl());
        return R.ok(oss.getUrl());
    }

    /**
     * 小程序-修改用户
     */
    @PostMapping("/edit/xcxUser")
    public R<SysUserVo> editXcxUser(@RequestBody SysUserBo user) {
        return R.ok(userService.updateXcxUser(user));
    }

    /**
     * 删除用户
     *
     * @param userIds 角色ID串
     */
    @SaCheckPermission("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public R<Void> remove(@PathVariable Long[] userIds) {
        if (ArrayUtil.contains(userIds, LoginHelper.getUserId())) {
            return R.fail("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @SaCheckPermission("system:user:resetPwd")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public R<Void> resetPwd(@RequestBody SysUserBo user) {
        userService.checkUserAllowed(user.getUserId());
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        return toAjax(userService.resetUserPwd(user.getUserId(), user.getPassword()));
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysUserBo user) {
        userService.checkUserAllowed(user.getUserId());
        userService.checkUserDataScope(user.getUserId());
        return toAjax(userService.updateUserStatus(user.getUserId(), user.getStatus()));
    }

    /**
     * 根据用户编号获取授权角色
     *
     * @param userId 用户ID
     */
    @SaCheckPermission("system:user:query")
    @GetMapping("/authRole/{userId}")
    public R<SysUserInfoVo> authRole(@PathVariable Long userId) {
        SysUserVo user = userService.selectUserById(userId);
        List<SysRoleVo> roles = roleService.selectRolesByUserId(userId);
        SysUserInfoVo userInfoVo = new SysUserInfoVo();
        userInfoVo.setUser(user);
        userInfoVo.setRoles(LoginHelper.isSuperAdmin(userId) ? roles : StreamUtils.filter(roles, r -> !r.isSuperAdmin()));
        return R.ok(userInfoVo);
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户Id
     * @param roleIds 角色ID串
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public R<Void> insertAuthRole(Long userId, Long[] roleIds) {
        userService.checkUserDataScope(userId);
        userService.insertUserAuth(userId, roleIds);
        return R.ok();
    }

    /**
     * 获取部门树列表
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/deptTree")
    public R<List<Tree<Long>>> deptTree(SysDeptBo dept) {
        return R.ok(deptService.selectDeptTreeList(dept));
    }

    /**
     * 为新用户赠送会员
     * 针对2025-10-12到2025-11-12期间注册的用户，自动赠送一个月会员
     *
     * @param userId 用户ID
     */
//    @SaCheckPermission("system:user:edit")
    @Log(title = "用户领取免费会员", businessType = BusinessType.UPDATE)
    @PostMapping("/grantNewUserVip/{userId}")
    public R<String> grantNewUserVip(@PathVariable Long userId) {
        String result = userService.grantNewUserVip(userId);
        if (result.contains("成功")) {
            return R.ok(result);
        } else {
            return R.fail(result);
        }
    }

    /**
     * 批量为符合条件的新用户赠送会员
     * 针对2025-10-12到2025-11-12期间注册的用户，批量赠送一个月会员
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/batchGrantNewUserVip")
    public R<String> batchGrantNewUserVip() {
        String result = userService.batchGrantNewUserVip();
        return R.ok(result);
    }




    /**
     * 测试保存组织和用户信息(硬编码数据)
     * 注意: 此方法仅用于测试,功能完善后需要屏蔽
     * 访问路径: GET /system/user/testSaveOrgAndUsers
     */
    @Log(title = "测试保存组织和用户", businessType = BusinessType.INSERT)
    @GetMapping("/testSaveOrgAndUsers")
    public R<String> testSaveOrgAndUsers() {
        String result = userService.testSaveOrgAndUsers();
        if (result.contains("成功")) {
            return R.ok(result);
        } else {
            return R.fail(result);
        }
    }



}
