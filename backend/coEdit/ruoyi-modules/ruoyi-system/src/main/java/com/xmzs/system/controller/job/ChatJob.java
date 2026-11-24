package com.xmzs.system.controller.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xmzs.system.config.base.BaseConfig;
import com.xmzs.system.domain.SysUser;
import com.xmzs.system.domain.bo.SysUserBo;
import com.xmzs.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * packageName com.xmzs.system.controller.job
 *
 * @author zhuzi
 * @date 2024/10/16
 * @description TODO
 */
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
@Component
public class ChatJob {

    private final SysUserMapper sysUserMapper;
    private final BaseConfig baseConfig;
    // 每天的 00:00 执行 每日剩余次数
//    @Scheduled(cron = "0 00 00 * * ?")
    @Scheduled(cron = "0 28 9 * * ?")
    public void reportCurrentTime() {
        System.out.println("Hello World");
        //每晚0点发放使用次数
        //免费用户
        SysUserBo userFree = new SysUserBo();
        userFree.setUserGrade("0");

        sysUserMapper.update(null,
            new LambdaUpdateWrapper<SysUser>()
                .set(SysUser::getSubMsgCount, baseConfig.get("usageCount","free"))
                .set(SysUser::getPoints, baseConfig.get("usageCount","initPoints"))
                .eq(SysUser::getUserGrade, 0));

        sysUserMapper.update(null,
            new LambdaUpdateWrapper<SysUser>()
                .set(SysUser::getSubMsgCount, baseConfig.get("usageCount","vip"))
                .set(SysUser::getPoints, baseConfig.get("usageCount","initPoints"))
                .eq(SysUser::getUserGrade, 1));
        updateExpiredMembers();
//        SysUser freeSysUser = sysUserMapper.selectUserList(this.buildQueryWrapper(userFree));
    }




    public void updateExpiredMembers() {
        // 获取当前时间的毫秒表示
        long currentTimeMillis = System.currentTimeMillis();

        // 查询所有会员用户（userGrade == 1）
        List<SysUser> vipUsers = sysUserMapper.selectList(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserGrade, 1)
        );

        // 遍历所有会员用户，检查他们的 vipExpiryDate
        for (SysUser user : vipUsers) {
            long vipExpiryDateMillis;

            // 处理 vipExpiryDate 为秒或毫秒的情况
            if (user.getVipExpiryDate() > 10000000000L) { // 判断是否为毫秒级时间戳
                vipExpiryDateMillis = user.getVipExpiryDate(); // 毫秒级时间戳
            } else {
                vipExpiryDateMillis = user.getVipExpiryDate() * 1000; // 转换为毫秒级时间戳
            }

            // 如果会员到期时间小于当前时间，则更新用户信息
            if (vipExpiryDateMillis < currentTimeMillis) {
                sysUserMapper.update(null,
                    new LambdaUpdateWrapper<SysUser>()
                        .set(SysUser::getUserGrade, 0) // 更新为非会员
                        .set(SysUser::getSubMsgCount, baseConfig.get("usageCount", "free")) // 更新剩余次数
                        .set(SysUser::getPoints, baseConfig.get("usageCount", "initPoints")) // 更新积分
                        .eq(SysUser::getUserId, user.getUserId())); // 根据用户ID更新
            }
        }
    }


//    private Wrapper<SysUser> buildQueryWrapper(SysUserBo user) {
//        Map<String, Object> params = user.getParams();
//        QueryWrapper<SysUser> wrapper = Wrappers.query();
//        wrapper.eq("u.del_flag", UserConstants.USER_NORMAL)
//            .eq(ObjectUtil.isNotNull(user.getUserId()), "u.user_id", user.getUserId())
//            .eq(ObjectUtil.isNotNull(user.getUserGrade()), "u.user_grade", user.getUserGrade())
//            .like(StringUtils.isNotBlank(user.getUserName()), "u.user_name", user.getUserName())
//            .eq(StringUtils.isNotBlank(user.getStatus()), "u.status", user.getStatus())
//            .like(StringUtils.isNotBlank(user.getPhonenumber()), "u.phonenumber", user.getPhonenumber())
//            .between(params.get("beginTime") != null && params.get("endTime") != null,
//                "u.create_time", params.get("beginTime"), params.get("endTime"))
//            .and(ObjectUtil.isNotNull(user.getDeptId()), w -> {
//                List<SysDept> deptList = deptMapper.selectList(new LambdaQueryWrapper<SysDept>()
//                    .select(SysDept::getDeptId)
//                    .apply(DataBaseHelper.findInSet(user.getDeptId(), "ancestors")));
//                List<Long> ids = StreamUtils.toList(deptList, SysDept::getDeptId);
//                ids.add(user.getDeptId());
//                w.in("u.dept_id", ids);
//            });
//        return wrapper;
//    }
}
