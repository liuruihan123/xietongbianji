# RuoYi-coEdit 前端开发提示词文档

**版本**: v1.0.0
**日期**: 2025-11-05
**前端技术栈**: Vue 3 + Ant Design Vue + TypeScript
**后端版本**: v1.23.0

---

## 📚 文档说明

本文档集基于RuoYi-coEdit后端系统(v1.23.0)的完整功能,为前端团队提供详细的Vue3 + Ant Design Vue开发指导。

### 后端技术栈

- **框架**: Spring Boot 3.0.6
- **文档服务**: OnlyOffice Document Server 9.1.0 EE
- **WebSocket**: Spring WebSocket + STOMP
- **数据库**: MySQL 8.0+
- **认证**: Sa-Token 1.34.0 + JWT

### 前端技术栈

- **框架**: Vue 3.3+ (Composition API)
- **UI库**: Ant Design Vue 4.x
- **状态管理**: Pinia 2.1+
- **HTTP客户端**: Axios 1.5+
- **WebSocket**: SockJS + STOMP.js
- **构建工具**: Vite 4.5+

---

## 📖 模块文档索引

### [模块1: 工作空间管理](./前端开发提示词-模块1-工作空间管理.md)

**对应后端功能**:
- 工作空间CRUD (co_workspace表)
- 成员管理 (co_workspace_member表)
- 权限控制 (Owner/Admin/Member/Guest)
- 存储配额管理

**核心API**:
- `GET /coedit/workspace/user/{userId}` - 获取用户工作空间列表
- `POST /coedit/workspace` - 创建工作空间
- `PUT /coedit/workspace` - 更新工作空间
- `DELETE /coedit/workspace/{id}` - 删除工作空间
- `GET /coedit/member/list` - 获取成员列表
- `POST /coedit/member/invite` - 邀请成员

**前端组件**:
- WorkspaceList (工作空间列表)
- WorkspaceCard (工作空间卡片)
- CreateWorkspaceModal (创建对话框)
- MemberManagement (成员管理)

---

### [模块2: 文件管理](./前端开发提示词-模块2-文件管理.md)

**对应后端功能**:
- 文件上传/下载 (co_file表)
- 文件夹管理 (co_folder表)
- 回收站 (co_recycle_bin表)
- 文件搜索 (co_search_history表)

**核心API**:
- `POST /coedit/file/upload` - 上传文件
- `GET /coedit/file/download/{id}` - 下载文件
- `POST /coedit/file/move` - 移动文件
- `DELETE /coedit/file/{id}` - 删除文件(移至回收站)
- `POST /coedit/folder` - 创建文件夹
- `GET /coedit/recycle/list` - 获取回收站列表
- `POST /coedit/recycle/restore/{id}` - 恢复文件

**前端组件**:
- FileList (文件列表)
- FileUpload (文件上传)
- FolderTree (文件夹树)
- RecycleBin (回收站)

---

### 模块3: 文档编辑器

**对应后端功能**:
- OnlyOffice编辑器集成
- 编辑器配置生成 (JWT签名)
- 文档回调处理
- 版本管理 (co_file_version表)

**核心API**:
- `GET /coedit/editor/config/{fileId}` - 获取编辑器配置
- `POST /coedit/editor/callback` - 编辑器回调
- `GET /coedit/version/list` - 获取版本列表
- `POST /coedit/version/restore/{versionId}` - 恢复版本

**前端实现要点**:

```vue
<template>
  <div id="onlyoffice-editor" style="height: 100vh;"></div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRoute } from 'vue-router';
import * as editorApi from '@/api/editor';

const route = useRoute();
const fileId = route.params.fileId;

onMounted(async () => {
  // 获取编辑器配置
  const config = await editorApi.getEditorConfig(fileId);

  // 初始化OnlyOffice编辑器
  new DocsAPI.DocEditor('onlyoffice-editor', {
    documentType: config.documentType, // word/cell/slide
    document: {
      fileType: config.document.fileType,
      key: config.document.key,
      title: config.document.title,
      url: config.document.url,
      permissions: {
        edit: config.document.permissions.edit,
        download: config.document.permissions.download,
        print: config.document.permissions.print,
      },
    },
    editorConfig: {
      mode: config.editorConfig.mode, // edit/view
      lang: 'zh-CN',
      user: {
        id: config.editorConfig.user.id,
        name: config.editorConfig.user.name,
      },
      callbackUrl: config.editorConfig.callbackUrl,
    },
    width: '100%',
    height: '100%',
  });
});
</script>
```

**Pinia Store**:

```typescript
// stores/editor.ts
export const useEditorStore = defineStore('editor', () => {
  const currentFile = ref(null);
  const editorConfig = ref(null);
  const versions = ref([]);

  async function loadEditorConfig(fileId: number) {
    editorConfig.value = await editorApi.getEditorConfig(fileId);
    return editorConfig.value;
  }

  async function fetchVersions(fileId: number) {
    const response = await editorApi.getVersionList(fileId);
    versions.value = response.rows;
  }

  async function restoreVersion(versionId: number) {
    await editorApi.restoreVersion(versionId);
    // 刷新编辑器
    window.location.reload();
  }

  return {
    currentFile,
    editorConfig,
    versions,
    loadEditorConfig,
    fetchVersions,
    restoreVersion,
  };
});
```

---

### 模块4: 协同编辑

**对应后端功能**:
- WebSocket实时通信
- 编辑会话管理 (co_edit_session表)
- 编辑锁 (co_edit_lock表)
- 编辑历史 (co_edit_history表)
- 编辑日志 (co_edit_log表)

**核心WebSocket消息类型**:
- `USER_JOIN` - 用户加入
- `USER_LEAVE` - 用户离开
- `EDIT_STATUS` - 编辑状态
- `CURSOR_MOVE` - 光标移动
- `CONTENT_CHANGE` - 内容变更
- `HEARTBEAT` - 心跳
- `SYSTEM_NOTIFY` - 系统通知

**前端实现要点**:

```typescript
// composables/useWebSocket.ts
import { ref, onMounted, onUnmounted } from 'vue';
import SockJS from 'sockjs-client';
import { Client, IMessage } from '@stomp/stompjs';

export function useWebSocket(fileId: string) {
  const client = ref<Client | null>(null);
  const connected = ref(false);
  const onlineUsers = ref<any[]>([]);

  function connect() {
    const socket = new SockJS(`${import.meta.env.VITE_API_BASE_URL}/ws/coedit`);

    client.value = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,

      onConnect: () => {
        console.log('WebSocket连接成功');
        connected.value = true;
        subscribe();
        sendJoinMessage();
      },

      onDisconnect: () => {
        console.log('WebSocket断开连接');
        connected.value = false;
      },

      onStompError: (frame) => {
        console.error('STOMP错误:', frame);
      },
    });

    client.value.activate();
  }

  function subscribe() {
    // 订阅文件主题
    client.value?.subscribe(`/topic/file/${fileId}`, (message: IMessage) => {
      const data = JSON.parse(message.body);
      handleMessage(data);
    });

    // 订阅用户私有队列
    client.value?.subscribe('/user/queue/message', (message: IMessage) => {
      const data = JSON.parse(message.body);
      handlePrivateMessage(data);
    });
  }

  function handleMessage(data: any) {
    switch (data.type) {
      case 'USER_JOIN':
        onlineUsers.value.push(data.user);
        message.info(`${data.user.name} 加入了编辑`);
        break;

      case 'USER_LEAVE':
        onlineUsers.value = onlineUsers.value.filter(u => u.id !== data.user.id);
        message.info(`${data.user.name} 离开了编辑`);
        break;

      case 'EDIT_STATUS':
        updateUserStatus(data.userId, data.status);
        break;

      case 'CURSOR_MOVE':
        updateUserCursor(data.userId, data.position);
        break;

      case 'CONTENT_CHANGE':
        // OnlyOffice会自动处理内容同步
        break;

      case 'SYSTEM_NOTIFY':
        message.warning(data.message);
        break;
    }
  }

  function sendJoinMessage() {
    const userId = localStorage.getItem('userId');
    const userName = localStorage.getItem('userName');

    client.value?.publish({
      destination: `/app/file/${fileId}/join`,
      body: JSON.stringify({
        userId,
        userName,
        timestamp: Date.now(),
      }),
    });

### 模块6: 文件搜索

**对应后端功能**:
- 基础搜索
- 高级搜索(多条件)
- 搜索历史 (co_search_history表)
- 热门搜索

**核心API**:
- `GET /coedit/file/search` - 搜索文件
- `POST /coedit/search/advanced` - 高级搜索
- `GET /coedit/search/history` - 获取搜索历史
- `GET /coedit/search/hot` - 获取热门搜索

**前端实现要点**:

```vue
<!-- components/FileSearch.vue -->
<template>
  <div class="file-search">
    <a-auto-complete
      v-model:value="keyword"
      :options="suggestions"
      placeholder="搜索文件名、内容、标签..."
      style="width: 400px"
      @search="handleSearch"
      @select="handleSelect"
    >
      <template #option="{ value, label, type }">
        <div class="search-option">
          <FileOutlined v-if="type === 'file'" />
          <HistoryOutlined v-else-if="type === 'history'" />
          <FireOutlined v-else />
          <span>{{ label }}</span>
        </div>
      </template>
    </a-auto-complete>

    <a-button @click="showAdvancedSearch">高级搜索</a-button>

    <!-- 高级搜索对话框 -->
    <a-modal
      v-model:visible="advancedVisible"
      title="高级搜索"
      width="600px"
      @ok="handleAdvancedSearch"
    >
      <a-form :model="advancedForm" layout="vertical">
        <a-form-item label="文件名">
          <a-input v-model:value="advancedForm.fileName" />
        </a-form-item>

        <a-form-item label="文件类型">
          <a-select v-model:value="advancedForm.fileType" mode="multiple">
            <a-select-option value="doc">Word文档</a-select-option>
            <a-select-option value="xls">Excel表格</a-select-option>
            <a-select-option value="ppt">PPT演示</a-select-option>
            <a-select-option value="pdf">PDF文档</a-select-option>
            <a-select-option value="txt">文本文件</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="创建时间">
          <a-range-picker
            v-model:value="advancedForm.createTime"
            show-time
            style="width: 100%"
          />
        </a-form-item>

        <a-form-item label="文件大小">
          <a-row :gutter="8">
            <a-col :span="11">
              <a-input-number
                v-model:value="advancedForm.minSize"
                placeholder="最小(MB)"
                style="width: 100%"
              />
            </a-col>
            <a-col :span="2" style="text-align: center">-</a-col>
            <a-col :span="11">
              <a-input-number
                v-model:value="advancedForm.maxSize"
                placeholder="最大(MB)"
                style="width: 100%"
              />
            </a-col>
          </a-row>
        </a-form-item>

        <a-form-item label="创建人">
          <a-select v-model:value="advancedForm.createBy" show-search>
            <a-select-option
              v-for="user in users"
              :key="user.id"
              :value="user.id"
            >
              {{ user.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue';
import { debounce } from 'lodash-es';
import * as searchApi from '@/api/search';
import { FileOutlined, HistoryOutlined, FireOutlined } from '@ant-design/icons-vue';

const keyword = ref('');
const suggestions = ref([]);
const advancedVisible = ref(false);
const advancedForm = reactive({
  fileName: '',
  fileType: [],
  createTime: null,
  minSize: null,
  maxSize: null,
  createBy: null,
});

// 防抖搜索
const handleSearch = debounce(async (value: string) => {
  if (!value) {
    // 显示搜索历史和热门搜索
    const [history, hot] = await Promise.all([
      searchApi.getSearchHistory(),
      searchApi.getHotSearch(),
    ]);

    suggestions.value = [
      ...history.map(h => ({ value: h.keyword, label: h.keyword, type: 'history' })),
      ...hot.map(h => ({ value: h.keyword, label: h.keyword, type: 'hot' })),
    ];
  } else {
    // 搜索建议
    const results = await searchApi.searchFiles({ keyword: value });
    suggestions.value = results.map(r => ({
      value: r.id,
      label: r.name,
      type: 'file',
    }));
  }
}, 300);

function handleSelect(value: any) {
  // 跳转到文件详情或打开文件
  console.log('选中文件:', value);
}

function showAdvancedSearch() {
  advancedVisible.value = true;
}

async function handleAdvancedSearch() {
  const results = await searchApi.advancedSearch(advancedForm);
  emit('search', results);
  advancedVisible.value = false;
}

const emit = defineEmits(['search']);
</script>
```

---

### 模块7: 文件预览

**对应后端功能**:
- Office文件预览
- PDF预览
- 图片预览
- 缩略图生成 (co_file_thumbnail表)
- 水印功能

**核心API**:
- `GET /coedit/preview/{fileId}` - 获取预览配置
- `GET /coedit/thumbnail/{fileId}` - 获取缩略图

**前端实现要点**:

```vue
<!-- views/preview/index.vue -->
<template>
  <div class="file-preview">
    <!-- Office文件预览 -->
    <div v-if="isOfficeFile" id="onlyoffice-preview" style="height: 100vh;"></div>

    <!-- PDF预览 -->
    <div v-else-if="isPdfFile" class="pdf-preview">
      <iframe :src="pdfUrl" style="width: 100%; height: 100vh; border: none;"></iframe>
    </div>

    <!-- 图片预览 -->
    <div v-else-if="isImageFile" class="image-preview">
      <a-image :src="imageUrl" :preview="{ visible: true }" />
    </div>

    <!-- 文本预览 -->
    <div v-else-if="isTextFile" class="text-preview">
      <pre>{{ textContent }}</pre>
    </div>

    <!-- 不支持预览 -->
    <div v-else class="unsupported-preview">
      <a-result
        status="warning"
        title="该文件类型不支持在线预览"
        sub-title="请下载后查看"
      >
        <template #extra>
          <a-button type="primary" @click="downloadFile">
            <DownloadOutlined /> 下载文件
          </a-button>
        </template>
      </a-result>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import * as previewApi from '@/api/preview';
import { DownloadOutlined } from '@ant-design/icons-vue';

const route = useRoute();
const fileId = route.params.fileId;
const fileInfo = ref(null);
const textContent = ref('');

const isOfficeFile = computed(() => {
  const ext = fileInfo.value?.extension?.toLowerCase();
  return ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx'].includes(ext);
});

const isPdfFile = computed(() => {
  return fileInfo.value?.extension?.toLowerCase() === 'pdf';
});

const isImageFile = computed(() => {
  const ext = fileInfo.value?.extension?.toLowerCase();
  return ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'svg'].includes(ext);
});

const isTextFile = computed(() => {
  const ext = fileInfo.value?.extension?.toLowerCase();
  return ['txt', 'md', 'json', 'xml', 'csv'].includes(ext);
});

const pdfUrl = computed(() => {
  return `${import.meta.env.VITE_API_BASE_URL}/coedit/preview/${fileId}`;
});

const imageUrl = computed(() => {
  return `${import.meta.env.VITE_API_BASE_URL}/coedit/preview/${fileId}`;
});

onMounted(async () => {
  // 获取文件信息
  fileInfo.value = await previewApi.getFileInfo(fileId);

  if (isOfficeFile.value) {
    initOnlyOfficePreview();
  } else if (isTextFile.value) {
    textContent.value = await previewApi.getTextContent(fileId);
  }
});

async function initOnlyOfficePreview() {
  const config = await previewApi.getPreviewConfig(fileId);

  new DocsAPI.DocEditor('onlyoffice-preview', {
    documentType: config.documentType,
    document: {
      fileType: config.document.fileType,
      key: config.document.key,
      title: config.document.title,
      url: config.document.url,
      permissions: {
        edit: false, // 预览模式不允许编辑
        download: config.document.permissions.download,
        print: config.document.permissions.print,
      },
    },
    editorConfig: {
      mode: 'view', // 预览模式
      lang: 'zh-CN',
    },
    width: '100%',
    height: '100%',
  });
}

function downloadFile() {
  window.location.href = `${import.meta.env.VITE_API_BASE_URL}/coedit/file/download/${fileId}`;
}
</script>
```

---

### 模块8: 文档转换

**对应后端功能**:
- 文档格式转换 (co_file_conversion表)
- 支持50+种格式互转
- 转换进度跟踪
- 转换历史记录

**核心API**:
- `POST /coedit/convert/start` - 开始转换
- `GET /coedit/convert/progress/{taskId}` - 查询转换进度
- `GET /coedit/convert/history` - 获取转换历史
- `GET /coedit/convert/formats` - 获取支持的格式

**前端实现要点**:

```vue
<!-- components/FileConversion.vue -->
<template>
  <a-modal
    v-model:visible="visible"
    title="文档转换"
    @ok="handleConvert"
  >
    <a-form :model="form" layout="vertical">
      <a-form-item label="源文件">
        <a-input :value="fileName" disabled />
      </a-form-item>

      <a-form-item label="当前格式">
        <a-tag color="blue">{{ currentFormat }}</a-tag>
      </a-form-item>

      <a-form-item label="目标格式">
        <a-select v-model:value="form.targetFormat" placeholder="请选择目标格式">
          <a-select-option
            v-for="format in supportedFormats"
            :key="format"
            :value="format"
          >
            {{ format.toUpperCase() }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item v-if="converting" label="转换进度">
        <a-progress :percent="progress" :status="progressStatus" />
        <div style="margin-top: 8px; color: #666;">
          {{ progressText }}
        </div>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { message } from 'ant-design-vue';
import * as conversionApi from '@/api/conversion';

const props = defineProps<{
  fileId: number;
  fileName: string;
  currentFormat: string;
}>();

const emit = defineEmits(['success']);

const visible = ref(false);
const converting = ref(false);
const progress = ref(0);
const progressText = ref('');
const taskId = ref('');
const supportedFormats = ref([]);

const form = reactive({
  targetFormat: '',
});

const progressStatus = computed(() => {
  if (progress.value === 100) return 'success';
  if (progress.value > 0) return 'active';
  return 'normal';
});

async function show() {
  visible.value = true;
  // 获取支持的转换格式
  const formats = await conversionApi.getSupportedFormats(props.currentFormat);
  supportedFormats.value = formats;
}

async function handleConvert() {
  if (!form.targetFormat) {
    message.warning('请选择目标格式');
    return;
  }

  try {
    converting.value = true;
    progress.value = 0;
    progressText.value = '正在提交转换任务...';

    // 开始转换
    const task = await conversionApi.startConversion({
      fileId: props.fileId,
      targetFormat: form.targetFormat,
    });
    taskId.value = task.taskId;

    // 轮询转换进度
    pollProgress();
  } catch (error) {
    message.error('转换失败');
    converting.value = false;
  }
}

async function pollProgress() {
  const timer = setInterval(async () => {
    try {
      const result = await conversionApi.getProgress(taskId.value);
      progress.value = result.progress;
      progressText.value = result.message;

      if (result.status === 'COMPLETED') {
        clearInterval(timer);
        message.success('转换完成');
        emit('success', result.fileId);
        visible.value = false;
        converting.value = false;
      } else if (result.status === 'FAILED') {
        clearInterval(timer);
        message.error(`转换失败: ${result.error}`);
        converting.value = false;
      }
    } catch (error) {
      clearInterval(timer);
      message.error('查询转换进度失败');
      converting.value = false;
    }
  }, 1000);
}

defineExpose({ show });
</script>
```

---

### 模块9: 性能监控

**对应后端功能**:
- 编辑器性能监控 (co_editor_performance表)
- 实时连接统计
- 设备类型统计
- 网络类型统计
- 延迟分布

**核心API**:
- `GET /coedit/performance/stats` - 获取性能统计
- `GET /coedit/performance/realtime` - 获取实时数据
- `POST /coedit/performance/report` - 上报性能数据

**前端实现要点**:

```vue
<!-- views/performance/index.vue -->
<template>
  <div class="performance-monitor">
    <a-row :gutter="16">
      <!-- 实时连接数 -->
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="实时连接数"
            :value="stats.activeConnections"
            :prefix="<TeamOutlined />"
          />
        </a-card>
      </a-col>

      <!-- 平均延迟 -->
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="平均延迟"
            :value="stats.avgLatency"
            suffix="ms"
            :value-style="{ color: stats.avgLatency > 100 ? '#cf1322' : '#3f8600' }"
          />
        </a-card>
      </a-col>

      <!-- 今日编辑次数 -->
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="今日编辑次数"
            :value="stats.todayEdits"
            :prefix="<EditOutlined />"
          />
        </a-card>
      </a-col>

      <!-- 在线用户数 -->
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="在线用户数"
            :value="stats.onlineUsers"
            :prefix="<UserOutlined />"
          />
        </a-card>
      </a-col>
    </a-row>

    <!-- 图表 -->
    <a-row :gutter="16" style="margin-top: 16px;">
      <a-col :span="12">
        <a-card title="延迟分布">
          <div ref="latencyChartRef" style="height: 300px;"></div>
        </a-card>
      </a-col>

      <a-col :span="12">
        <a-card title="设备类型分布">
          <div ref="deviceChartRef" style="height: 300px;"></div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" style="margin-top: 16px;">
      <a-col :span="12">
        <a-card title="网络类型分布">
          <div ref="networkChartRef" style="height: 300px;"></div>
        </a-card>
      </a-col>

      <a-col :span="12">
        <a-card title="实时连接趋势">
          <div ref="connectionChartRef" style="height: 300px;"></div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import * as echarts from 'echarts';
import * as performanceApi from '@/api/performance';
import { TeamOutlined, EditOutlined, UserOutlined } from '@ant-design/icons-vue';

const stats = ref({
  activeConnections: 0,
  avgLatency: 0,
  todayEdits: 0,
  onlineUsers: 0,
});

const latencyChartRef = ref();
const deviceChartRef = ref();
const networkChartRef = ref();
const connectionChartRef = ref();

let latencyChart: echarts.ECharts;
let deviceChart: echarts.ECharts;
let networkChart: echarts.ECharts;
let connectionChart: echarts.ECharts;
let timer: any;

onMounted(async () => {
  initCharts();
  await loadData();
  startRealTimeUpdate();
});

onUnmounted(() => {
  if (timer) clearInterval(timer);
  latencyChart?.dispose();
  deviceChart?.dispose();
  networkChart?.dispose();
  connectionChart?.dispose();
});

function initCharts() {
  latencyChart = echarts.init(latencyChartRef.value);
  deviceChart = echarts.init(deviceChartRef.value);
  networkChart = echarts.init(networkChartRef.value);
  connectionChart = echarts.init(connectionChartRef.value);
}

async function loadData() {
  const data = await performanceApi.getStats();
  stats.value = data.stats;

  // 延迟分布图
  latencyChart.setOption({
    tooltip: {},
    xAxis: {
      type: 'category',
      data: ['0-50ms', '50-100ms', '100-200ms', '200-500ms', '>500ms'],
    },
    yAxis: { type: 'value' },
    series: [{
      data: data.latencyDistribution,
      type: 'bar',
      itemStyle: { color: '#1890ff' },
    }],
  });

  // 设备类型分布图
  deviceChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '50%',
      data: data.deviceDistribution,
    }],
  });

  // 网络类型分布图
  networkChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '50%',
      data: data.networkDistribution,
    }],
  });
}

function startRealTimeUpdate() {
  timer = setInterval(async () => {
    const realtime = await performanceApi.getRealtime();
    stats.value = realtime.stats;

    // 更新连接趋势图
    connectionChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: realtime.timeline,
      },
      yAxis: { type: 'value' },
      series: [{
        data: realtime.connections,
        type: 'line',
        smooth: true,
        itemStyle: { color: '#52c41a' },
      }],
    });
  }, 5000); // 每5秒更新一次
}
</script>
```

---

## 🔧 通用技术方案

### 1. 项目初始化

```bash
# 创建Vue3项目
npm create vite@latest ruoyi-coedit-frontend -- --template vue-ts

# 安装依赖
cd ruoyi-coedit-frontend
npm install

# 安装Ant Design Vue
npm install ant-design-vue @ant-design/icons-vue

# 安装Pinia
npm install pinia pinia-plugin-persistedstate

# 安装其他依赖
npm install axios sockjs-client @stomp/stompjs dayjs lodash-es spark-md5 file-saver jszip

# 安装开发依赖
npm install -D @types/sockjs-client @types/lodash-es @types/spark-md5 @types/file-saver
```

### 2. 环境配置

```env
# .env.development
VITE_API_BASE_URL=http://localhost:8080
VITE_ONLYOFFICE_URL=http://192.168.245.129:9000
VITE_WS_URL=ws://localhost:8080/ws/coedit

# .env.production
VITE_API_BASE_URL=https://api.example.com
VITE_ONLYOFFICE_URL=https://onlyoffice.example.com
VITE_WS_URL=wss://api.example.com/ws/coedit
```

---

由于内容较长,我将继续在下一部分完成文档。

  }

  function sendLeaveMessage() {
    const userId = localStorage.getItem('userId');

    client.value?.publish({
      destination: `/app/file/${fileId}/leave`,
      body: JSON.stringify({
        userId,
        timestamp: Date.now(),
      }),
    });
  }

  function disconnect() {
    sendLeaveMessage();
    client.value?.deactivate();
  }

  onMounted(connect);
  onUnmounted(disconnect);

  return {
    connected,
    onlineUsers,
    client,
    disconnect,
  };
}
```

**在线用户列表组件**:

```vue
<template>
  <div class="online-users">
    <div class="users-header">
      <UserOutlined />
      <span>在线用户 ({{ onlineUsers.length }})</span>
    </div>
    <a-list :data-source="onlineUsers" size="small">
      <template #renderItem="{ item }">
        <a-list-item>
          <a-list-item-meta>
            <template #avatar>
              <a-avatar :src="item.avatar">{{ item.name[0] }}</a-avatar>
            </template>
            <template #title>
              {{ item.name }}
            </template>
            <template #description>
              <a-badge :status="item.status === 'EDITING' ? 'processing' : 'default'" />
              {{ item.status === 'EDITING' ? '正在编辑' : '查看中' }}
            </template>
          </a-list-item-meta>
        </a-list-item>
      </template>
    </a-list>
  </div>
</template>

<script setup lang="ts">
import { UserOutlined } from '@ant-design/icons-vue';

defineProps<{
  onlineUsers: any[];
}>();
</script>
```

---

### 模块5: 文件分享

**对应后端功能**:
- 分享链接管理 (co_share_link表)
- 访问记录 (co_share_access_record表)
- 安全控制 (co_share_security表)

**核心API**:
- `POST /coedit/share/create` - 创建分享链接
- `GET /coedit/share/list` - 获取分享列表
- `GET /coedit/share/access/{shareCode}` - 访问分享
- `POST /coedit/share/verify` - 验证分享密码
- `DELETE /coedit/share/{id}` - 取消分享

**前端实现要点**:

```vue
<!-- components/CreateShareModal.vue -->
<template>
  <a-modal
    v-model:visible="visible"
    title="创建分享链接"
    @ok="handleCreate"
  >
    <a-form :model="form" layout="vertical">
      <a-form-item label="分享权限">
        <a-radio-group v-model:value="form.permission">
          <a-radio value="VIEW">仅查看</a-radio>
          <a-radio value="EDIT">可编辑</a-radio>
          <a-radio value="DOWNLOAD">可下载</a-radio>
        </a-radio-group>
      </a-form-item>

      <a-form-item label="有效期">
        <a-select v-model:value="form.expireType">
          <a-select-option value="PERMANENT">永久有效</a-select-option>
          <a-select-option value="7_DAYS">7天</a-select-option>
          <a-select-option value="30_DAYS">30天</a-select-option>
          <a-select-option value="CUSTOM">自定义</a-select-option>
        </a-select>
        <a-date-picker
          v-if="form.expireType === 'CUSTOM'"
          v-model:value="form.expireTime"
          show-time
          style="width: 100%; margin-top: 8px;"
        />
      </a-form-item>

      <a-form-item label="访问密码">
        <a-input-password
          v-model:value="form.password"
          placeholder="留空则无需密码"
        />
      </a-form-item>

      <a-form-item label="允许下载">
        <a-switch v-model:checked="form.allowDownload" />
      </a-form-item>

      <a-form-item label="允许打印">
        <a-switch v-model:checked="form.allowPrint" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { message } from 'ant-design-vue';
import * as shareApi from '@/api/share';

const props = defineProps<{
  fileId: number;
}>();

const emit = defineEmits(['success']);

const visible = ref(false);
const form = reactive({
  permission: 'VIEW',
  expireType: 'PERMANENT',
  expireTime: null,
  password: '',
  allowDownload: true,
  allowPrint: true,
});

async function handleCreate() {
  try {
    const shareLink = await shareApi.createShare({
      fileId: props.fileId,
      ...form,
    });

    message.success('分享链接创建成功');
    emit('success', shareLink);
    visible.value = false;
  } catch (error) {
    message.error('创建分享链接失败');
  }
}

function show() {
  visible.value = true;
}

defineExpose({ show });
</script>
```

---

由于内容较多,我将继续在下一部分完成剩余模块的文档。

