<template>
  <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
    <el-tab-pane label="对话配置" name="chat">对话配置</el-tab-pane>
    <el-tab-pane label="通知配置" name="notification">通知配置</el-tab-pane>
    <el-tab-pane label="文档分析配置" name="logicSizeSet">文档分析配置（单位token）谨慎修改！！</el-tab-pane>
    <el-tab-pane label="用户使用次数" name="usageCount">用户使用次数</el-tab-pane>
    <el-tab-pane label="微信开放平台" name="op_channel">微信开放平台</el-tab-pane>
    <el-tab-pane label="微信支付配置" name="wxPay">微信支付配置</el-tab-pane>
    <el-tab-pane label="支付宝支付配置" name="aliPay">支付宝支付配置</el-tab-pane>
    <el-tab-pane label="stripe支付配置" name="stripePay">stripe支付配置</el-tab-pane>
    <el-tab-pane label="VPN配置" name="proxy">VPN配置</el-tab-pane>
    <!-- <el-tab-pane label="声音配置" name="audio">声音配置</el-tab-pane> -->
    <!-- <el-tab-pane label="绘画配置" name="mj">绘画配置</el-tab-pane> -->
    <el-tab-pane label="邮箱配置" name="mail">邮箱配置</el-tab-pane>
    <el-tab-pane label="短信配置" name="sms">短信配置</el-tab-pane>
    <el-tab-pane label="邀请配置" name="invit">邀请配置</el-tab-pane>
    <!-- <el-tab-pane label="百度配置" name="review">百度配置</el-tab-pane> -->
    <!-- <el-tab-pane label="支付平台配置" name="pay">支付平台配置-暂时不用</el-tab-pane> -->
    <!-- <el-tab-pane label="微信配置" name="weixin">微信配置</el-tab-pane> -->
    <el-tab-pane label="系统配置" name="sys">系统配置</el-tab-pane>
    <el-tab-pane label="协议政策" name="protocol">协议政策</el-tab-pane>
    <el-tab-pane label="关于我们" name="about">关于我们</el-tab-pane>
    <el-tab-pane label="常见问题" name="question">常见问题</el-tab-pane>
  </el-tabs>

  <!-- 配置项的输入框 -->
  <div v-for="item in configList" :key="item.id" class="config-item">
    <el-form-item :label="item.configDict" label-width="400px">
      <template
        v-if="['protocol', 'about', 'question'].includes(item.category) || 
              ['privateCertContent','privateKeyContent'].includes(item.configName)"
      >
        <editor v-model="item.configValue" :min-height="192" />
      </template>
      <template v-else-if="item.configName === 'interface_version'">
        <el-select v-model="item.configValue" style="width: 700px" placeholder="请选择接口版本">
          <el-option label="V2" value="V2" />
          <el-option label="V3" value="V3" />
        </el-select>
      </template>
      <template v-else>
        <el-input
          class="config-iput"
          v-model="item.configValue"
          :placeholder="item.configDict"
          :type="isPasswordVisible[item.id] ? 'text' : (item.configName.toLowerCase().includes('key') ? 'password' : 'text')"
          label-position="left"
          style="width: 700px"
        >
          <template #append v-if="item.configName.toLowerCase().includes('key')">
            <el-icon class="cursor-pointer" @click="togglePasswordVisibility(item.id)">
              <component :is="isPasswordVisible[item.id] ? 'View' : 'Hide'" />
            </el-icon>
          </template>
        </el-input>
      </template>
    </el-form-item>
  </div>
  <div class="mb-4" style="margin-left: 200px">
    <el-button type="primary" @click="saveConfig">保存</el-button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { listConfig,updateConfig } from "@/api/system/chatconfig"; // 假设这是你的API方法
import type, { TabsPaneContext } from 'element-plus'
import { ConfigVO } from '@/api/system/chatconfig/types';
import { ComponentInternalInstance } from "vue";
import { DateModelType } from 'element-plus';
import Editor from "@/components/Editor/index.vue";
import { View, Hide } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const activeName = ref('chat');
// let configList = ref([]);
let configList = ref<ConfigVO[]>([]);


const query = ref({
  id: '',
  category: 'chat',
  configName: '',
  configValue: '',
  description: '',
  remark: '',
  updateIp: ''
});

// 页面加载时获取默认的配置列表
getList();

/** 查询配置列表 */
async function getList() {
  query.value.category = activeName.value;
  const res = await listConfig(query.value); // 假设listConfig返回的是一个Promise
  console.log(res )
  configList.value = res.data || [];
  console.log(configList.value)
}
const saveConfig = async () => {
  // 遍历configList并保存每个配置项
  for (const item of configList.value) {
    try {
      console.log(item)
      const response = await updateConfig(item);
      console.log(response); // 打印成功保存的响应

      // 可以在这里添加一些成功保存后的逻辑，比如提示信息
    } catch (error) {
      console.error("保存配置失败", error);
      // 处理错误情况，例如显示错误提示
    }
  }
  proxy?.$modal.msgSuccess("操作成功");
  // 可以在所有配置项保存完成后执行一些操作，比如刷新列表
  await getList(); // 重新获取最新的配置列表
};
const handleClick = (tab: TabsPaneContext, event: Event) => {
  if (tab.props.name) {
    activeName.value = tab.props.name as string;
    getList();
  }
}

// 添加密码显示状态控制
const isPasswordVisible = ref<Record<string | number, boolean>>({})

// 切换密码显示状态的方法
const togglePasswordVisibility = (id: string | number) => {
  isPasswordVisible.value[id] = !isPasswordVisible.value[id]
}
</script>

<style>
.demo-tabs > .el-tabs__content {
  padding: 32px;
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
}
.demo-tabs{
  padding: 2px 0px 0px 42px;
}
</style>
