# 工作空间类型定义使用说明

## 概述

本文档说明如何使用更新后的工作空间类型定义，这些定义已完全匹配后端 `CoWorkspaceBo` 的要求。

## 主要变更

### 1. 字段名称变更

| 旧字段名 | 新字段名 | 说明 |
|---------|---------|------|
| `name` | `workspaceName` | 工作空间名称 |
| `description` | `workspaceDesc` | 工作空间描述 |
| `icon` | `workspaceIcon` | 工作空间图标 |
| `type` | `workspaceType` | 工作空间类型 |

### 2. 工作空间类型

使用枚举 `WorkspaceType` 代替字符串：

```typescript
export enum WorkspaceType {
  PERSONAL = 1,    // 个人空间
  DEPARTMENT = 2,  // 部门空间
  TEAM = 3,        // 团队空间
}
```

### 3. 必填字段

创建工作空间时的必填字段：
- `workspaceName`: string - 工作空间名称
- `workspaceType`: WorkspaceType - 工作空间类型（1-3）

## 使用示例

### 创建个人工作空间

```typescript
import { WorkspaceType } from '@/types/workspace'
import { useWorkspaceStore } from '@/stores/workspace'

const workspaceStore = useWorkspaceStore()

// 最简单的创建方式（只包含必填字段）
const workspace = await workspaceStore.createWorkspace({
  workspaceName: '我的个人空间',
  workspaceType: WorkspaceType.PERSONAL,
})

// 完整的创建方式（包含可选字段）
const workspace = await workspaceStore.createWorkspace({
  workspaceName: '我的个人空间',
  workspaceType: WorkspaceType.PERSONAL,
  workspaceDesc: '这是我的个人工作空间',
  workspaceIcon: 'https://example.com/icon.png',
  workspaceColor: '#1890ff',
  maxStorage: 10737418240, // 10GB
  maxMembers: 10,
  allowInvite: true,
  allowShare: true,
  allowDownload: true,
  sortOrder: 1,
})
```

### 创建部门工作空间

```typescript
const workspace = await workspaceStore.createWorkspace({
  workspaceName: '技术部',
  workspaceType: WorkspaceType.DEPARTMENT,
  workspaceDesc: '技术部门工作空间',
  deptId: 123, // 部门ID（部门空间时必须提供）
  maxMembers: 50,
  allowInvite: true,
})
```

### 创建团队工作空间

```typescript
const workspace = await workspaceStore.createWorkspace({
  workspaceName: '项目A团队',
  workspaceType: WorkspaceType.TEAM,
  workspaceDesc: '项目A的团队协作空间',
  maxStorage: 53687091200, // 50GB
  maxMembers: 20,
  defaultPermission: 'edit',
  allowInvite: true,
  allowShare: false,
  allowDownload: true,
})
```

### 更新工作空间

```typescript
await workspaceStore.updateWorkspace({
  id: 1,
  workspaceName: '新的工作空间名称',
  workspaceDesc: '更新后的描述',
  maxStorage: 107374182400, // 100GB
})
```

### 使用工作空间类型选项

```typescript
import { WORKSPACE_TYPE_OPTIONS } from '@/types/workspace'

// 在表单中使用
<select v-model="formData.workspaceType">
  <option 
    v-for="option in WORKSPACE_TYPE_OPTIONS" 
    :key="option.value" 
    :value="option.value"
  >
    {{ option.label }}
  </option>
</select>
```

### 使用存储配额选项

```typescript
import { STORAGE_QUOTA_OPTIONS } from '@/types/workspace'

// 在表单中使用
<select v-model="formData.maxStorage">
  <option 
    v-for="option in STORAGE_QUOTA_OPTIONS" 
    :key="option.value" 
    :value="option.value"
  >
    {{ option.label }}
  </option>
</select>
```

## 注意事项

1. **工作空间类型是必填的**：创建工作空间时必须指定 `workspaceType`，值必须是 1、2 或 3。

2. **部门空间需要部门ID**：如果创建部门空间（`workspaceType = 2`），建议提供 `deptId`。

3. **字段名称已更改**：所有与工作空间相关的字段名都已更新为与后端一致，请确保在使用时使用新的字段名。

4. **类型安全**：使用 TypeScript 枚举 `WorkspaceType` 可以获得更好的类型检查和代码提示。

5. **可选字段**：除了 `workspaceName` 和 `workspaceType` 外，其他字段都是可选的，可以根据实际需求选择性提供。

## 后端验证规则

根据后端 `CoWorkspaceBo` 的验证规则：

- `workspaceName`: 不能为空，最大长度 100 个字符
- `workspaceType`: 不能为空，值必须在 1-3 之间
- `workspaceDesc`: 最大长度 500 个字符
- `workspaceIcon`: 最大长度 255 个字符
- `workspaceColor`: 最大长度 20 个字符
- `maxStorage`: 不能为负数
- `maxMembers`: 不能小于 1

