<template>
  <MainLayout
    :show-search="true"
    :show-notification="true"
    :show-sidebar-toggle="true"
    @sidebar-toggle="sidebarOpen = !sidebarOpen"
  >
    <div class="home-page bg-gray-50">
      <div class="flex">
      <!-- 左侧边栏 -->
      <aside :class="['fixed left-0 top-16 bottom-0 w-64 bg-white border-r border-gray-200 transform transition-transform duration-300 z-40 overflow-y-auto', sidebarOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0']">
        <div class="p-4">


           <!-- 新建按钮 -->
          <div
            v-if="isAuthenticated"
            class="relative mb-2"
          >
            <button
              @mouseenter="handleMenuEnter"
              class="w-full flex items-center justify-center space-x-2 px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 shadow-md hover:shadow-lg transition-all"
            >
              <i class="fa fa-plus text-base"></i>
              <span class="font-medium text-sm">新建</span>
              <i class="fa fa-angle-down text-sm ml-auto"></i>
            </button>

            <!-- 下拉菜单容器(包含透明桥接区域) -->
            <Transition name="dropdown">
              <div
                v-show="showCreateMenu"
                @mouseenter="handleMenuEnter"
                @mouseleave="handleMenuLeave"
                class="absolute left-0 right-0 top-0 pt-14 z-50"
              >
                <!-- 实际菜单内容 -->
                <div class="bg-white rounded-lg shadow-xl border border-gray-200 py-2">
                  <button
                    @click="handleCreateFile('DOCUMENT')"
                    class="w-full flex items-center px-4 py-3 text-sm text-gray-700 hover:bg-blue-50 transition-colors"
                  >
                    <div class="w-10 h-10 rounded-lg bg-blue-50 flex items-center justify-center mr-3">
                      <i class="fa fa-file-text-o text-blue-600 text-lg"></i>
                    </div>
                    <div class="text-left">
                      <div class="font-medium text-gray-900">文档</div>
                      <div class="text-xs text-gray-500">创建新文档</div>
                    </div>
                  </button>

                  <button
                    @click="handleCreateFile('SPREADSHEET')"
                    class="w-full flex items-center px-4 py-3 text-sm text-gray-700 hover:bg-green-50 transition-colors"
                  >
                    <div class="w-10 h-10 rounded-lg bg-green-50 flex items-center justify-center mr-3">
                      <i class="fa fa-table text-green-600 text-lg"></i>
                    </div>
                    <div class="text-left">
                      <div class="font-medium text-gray-900">表格</div>
                      <div class="text-xs text-gray-500">创建新表格</div>
                    </div>
                  </button>

                  <button
                    @click="handleCreateFile('PRESENTATION')"
                    class="w-full flex items-center px-4 py-3 text-sm text-gray-700 hover:bg-orange-50 transition-colors"
                  >
                    <div class="w-10 h-10 rounded-lg bg-orange-50 flex items-center justify-center mr-3">
                      <i class="fa fa-file-powerpoint-o text-orange-600 text-lg"></i>
                    </div>
                    <div class="text-left">
                      <div class="font-medium text-gray-900">演示文稿</div>
                      <div class="text-xs text-gray-500">创建新演示文稿</div>
                    </div>
                  </button>

                  <div class="border-t border-gray-200 my-2"></div>

                  <button
                    @click="handleCreateFile('FOLDER')"
                    class="w-full flex items-center px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                  >
                    <div class="w-10 h-10 rounded-lg bg-gray-50 flex items-center justify-center mr-3">
                      <i class="fa fa-folder-o text-gray-600 text-lg"></i>
                    </div>
                    <div class="text-left">
                      <div class="font-medium text-gray-900">文件夹</div>
                      <div class="text-xs text-gray-500">创建新文件夹</div>
                    </div>
                  </button>
                </div>
              </div>
            </Transition>
          </div>

          <!-- 导入按钮 -->
          <div v-if="isAuthenticated" class="mb-6">
            <input
              ref="fileInputRef"
              type="file"
              class="hidden"
              @change="handleFileSelect"
              multiple
            />
            <button
              @click="handleImportClick"
              class="w-full flex items-center justify-center space-x-2 px-5 py-2 bg-gray-200 text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-300 hover:border-gray-400 shadow-sm hover:shadow transition-all"
            >
              <i class="fa fa-upload text-base"></i>
              <span class="font-medium text-sm">导入文件</span>
            </button>
          </div>

          <!-- 文件分类导航 -->
          <div class="space-y-1 mb-6">
            <a
              v-for="item in navItems"
              :key="item.key"
              @click="handleNavClick(item)"
              :class="[
                'flex items-center justify-between px-3 py-2.5 rounded-lg cursor-pointer transition-all',
                activeNav === item.key
                  ? 'bg-blue-50 text-blue-600 font-medium'
                  : 'text-gray-700 hover:bg-gray-50'
              ]"
            >
              <div class="flex items-center space-x-3">
                <i :class="['fa', item.icon, activeNav === item.key ? 'text-blue-600' : 'text-gray-400']"></i>
                <span class="text-sm">{{ item.label }}</span>
              </div>
              <span v-if="item.count && isAuthenticated" class="text-xs text-gray-400">{{ item.count }}</span>
            </a>
          </div>

          <!-- 工作空间分类 -->
          <div class="border-t border-gray-200 pt-4 mb-4">
            <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wider px-3 mb-3">工作空间类型</h3>
            <div class="space-y-1">
              <a
                v-for="category in categories"
                :key="category.key"
                @click="handleCategoryClick(category.key)"
                :class="[
                  'flex items-center justify-between px-3 py-2.5 rounded-lg cursor-pointer transition-all',
                  activeCategory === category.key
                    ? 'bg-blue-50 text-blue-600 font-medium'
                    : 'text-gray-700 hover:bg-gray-50'
                ]"
              >
                <div class="flex items-center space-x-3">
                  <i :class="['fa', category.icon, activeCategory === category.key ? 'text-blue-600' : 'text-gray-400']"></i>
                  <span class="text-sm">{{ category.label }}</span>
                </div>
              </a>
            </div>
          </div>

          <!-- 工作空间导航 -->
          <!-- <div v-if="isAuthenticated" class="border-t border-gray-200 pt-4 mb-4">
            <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wider px-3 mb-3">工作空间</h3>
            <a
              @click="handleWorkspaceClick"
              class="flex items-center justify-between px-3 py-2.5 rounded-lg cursor-pointer text-gray-700 hover:bg-blue-50 transition-all group"
            >
              <div class="flex items-center space-x-3">
                <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white">
                  <i class="fa fa-briefcase text-sm"></i>
                </div>
                <div>
                  <div class="text-sm font-medium text-gray-900 group-hover:text-blue-600">我的工作空间</div>
                  <div class="text-xs text-gray-500">管理所有工作空间</div>
                </div>
              </div>
              <i class="fa fa-angle-right text-gray-400 group-hover:text-blue-600"></i>
            </a>
          </div> -->

          <!-- 回收站 -->
          <div class="border-t border-gray-200 pt-4 mb-4">
            <a
              @click="handleTrashClick"
              class="flex items-center justify-between px-3 py-2.5 rounded-lg cursor-pointer text-gray-700 hover:bg-gray-50 transition-all"
            >
              <div class="flex items-center space-x-3">
                <i class="fa fa-trash-o text-gray-400"></i>
                <span class="text-sm">回收站</span>
              </div>
              <span v-if="trashCount > 0" class="text-xs bg-red-100 text-red-600 px-2 py-0.5 rounded-full">{{ trashCount }}</span>
            </a>
          </div>

          <!-- 存储空间 -->
          <!-- <div v-if="isAuthenticated" class="border-t border-gray-200 mt-6 pt-4 px-3">
            <div class="text-xs text-gray-500 mb-2">存储空间</div>
            <div class="w-full bg-gray-200 rounded-full h-2 mb-2">
              <div class="bg-blue-600 h-2 rounded-full" :style="{ width: storagePercent + '%' }"></div>
            </div>
            <div class="text-xs text-gray-600">{{ usedStorage }} / {{ totalStorage }}</div>
          </div> -->
        </div>
      </aside>


      <!-- 主内容区 -->
      <main class="flex-1 md:ml-64 p-6">
        <!-- 面包屑导航 -->
        <div class="flex items-center space-x-2 mb-6 text-sm">
          <a
            v-for="(crumb, index) in breadcrumbs"
            :key="index"
            @click="handleBreadcrumbClick(crumb, index)"
            :class="[
              'flex items-center space-x-2 transition-colors',
              index === breadcrumbs.length - 1
                ? 'text-gray-900 font-medium cursor-default'
                : 'text-gray-500 hover:text-blue-600 cursor-pointer'
            ]"
          >
            <i v-if="index === 0" class="fa fa-home"></i>
            <span>{{ crumb.name }}</span>
            <i v-if="index < breadcrumbs.length - 1" class="fa fa-angle-right text-gray-400"></i>
          </a>
        </div>

        <!-- 未登录提示 -->
        <div v-if="!isAuthenticated" class="bg-blue-50 border border-blue-200 rounded-lg p-6 mb-6">
          <div class="flex items-center space-x-3">
            <i class="fa fa-info-circle text-blue-600 text-2xl"></i>
            <div>
              <h3 class="text-lg font-semibold text-blue-900 mb-1">欢迎使用 DocFlow</h3>
              <p class="text-sm text-blue-700">请先登录以查看和管理您的文件</p>
            </div>
          </div>
        </div>

        <!-- 最近访问页面 -->
        <div v-if="activeNav === 'recent' && isAuthenticated">
          <div class="flex items-center justify-between mb-6">
            <h2 class="text-2xl font-bold text-gray-900">最近访问</h2>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="flex justify-center items-center py-20">
            <LoadingOutlined class="text-4xl text-blue-600" />
          </div>

          <!-- 空状态 -->
          <div v-else-if="recentAccessList.length === 0" class="bg-white rounded-lg border border-gray-200 p-12">
            <a-empty description="暂无访问记录">
              <template #image>
                <i class="fa fa-clock-o text-6xl text-gray-300"></i>
              </template>
            </a-empty>
          </div>

          <!-- 最近访问表格 -->
          <div v-else class="bg-white border border-gray-200 rounded-lg overflow-x-auto">
            <table class="w-full">
              <thead class="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">名称</th>
                  <th class="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">所有者</th>
                  <th class="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">位置</th>
                  <th class="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">访问时间</th>
                  <th class="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">大小</th>
                  <th class="px-6 py-3 text-right text-xs font-semibold text-gray-600 uppercase tracking-wider">操作</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-200">
                <tr
                  v-for="record in recentAccessList"
                  :key="record.id"
                  class="hover:bg-gray-50 transition-colors"
                >
                  <!-- 名称列 -->
                  <td class="px-6 py-4">
                    <div
                      class="flex items-center space-x-3 cursor-pointer"
                      @click="handleAccessRecordClick(record)"
                    >
                      <!-- 文件图标 -->
                      <img
                        v-if="record.targetType === TargetType.FILE"
                        :src="getFileIconByExtension(getFileExtension(record))"
                        alt="文件图标"
                        class="w-10 h-10 object-contain"
                      />
                      <!-- 文件夹/工作空间图标 -->
                      <div
                        v-else
                        :class="['w-10 h-10 rounded-lg flex items-center justify-center', getTargetTypeIconBg(record.targetType)]"
                      >
                        <i :class="['fa text-lg', getTargetTypeIcon(record.targetType)]"></i>
                      </div>

                      <div class="min-w-0 flex-1">
                        <div class="font-medium text-gray-900 truncate">{{ getTargetName(record) }}</div>
                        <div v-if="getFileExtension(record)" class="text-xs text-gray-500">{{ getFileExtension(record) }}</div>
                      </div>
                    </div>
                  </td>

                  <!-- 所有者列 -->
                  <td class="px-6 py-4">
                    <div class="flex items-center space-x-2">
                      <LocalAvatar
                        :name="getOwnerNickName(record)"
                        :size="24"
                      />
                      <span class="text-sm text-gray-700">{{ getOwnerNickName(record) }}</span>
                    </div>
                  </td>

                  <!-- 位置列 -->
                  <td class="px-6 py-4">
                    <span class="text-sm text-gray-600 truncate block max-w-xs" :title="getLocationPath(record)">
                      {{ getLocationPath(record) }}
                    </span>
                  </td>

                  <!-- 访问时间列 -->
                  <td class="px-6 py-4 text-sm text-gray-600">
                    {{ formatAccessTime(record.accessTime) }}
                  </td>

                  <!-- 文件大小列 -->
                  <td class="px-6 py-4 text-sm text-gray-600">
                    {{ record.targetType === TargetType.FILE ? formatFileSize(getFileSize(record)) : '-' }}
                  </td>

                  <!-- 操作列 -->
                  <td class="px-6 py-4 text-right">
                    <div class="flex items-center justify-end space-x-2">
                      <!-- 分享按钮 -->
                      <button
                        @click.stop="handleShareFile(record)"
                        class="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                        title="分享"
                      >
                        <i class="fa fa-share-alt"></i>
                      </button>

                      <!-- 收藏按钮 -->
                      <button
                        @click.stop="handleToggleFavorite(record)"
                        :class="[
                          'p-2 rounded-lg transition-colors',
                          isFavoritedRecord(record, favoriteVersion)
                            ? 'text-yellow-600 bg-yellow-50 hover:bg-yellow-100'
                            : 'text-gray-400 hover:text-yellow-600 hover:bg-yellow-50'
                        ]"
                        :title="isFavoritedRecord(record, favoriteVersion) ? '取消收藏' : '收藏'"
                      >
                        <i :class="isFavoritedRecord(record, favoriteVersion) ? 'fa fa-star' : 'fa fa-star-o'"></i>
                      </button>

                      <!-- 三点菜单 (仅文件) -->
                      <FileActionMenu
                        v-if="record.targetType === TargetType.FILE"
                        :file="convertAccessRecordToFileItem(record)"
                        @download="handleFileDownloadWithConfirm"
                        @delete="handleFileDeleteWithConfirm"
                      />
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 全部文件页面 -->
        <div v-if="activeNav === 'all' && isAuthenticated">
          <!-- 工作空间标题、Tab和创建按钮(始终显示) -->
          <div class="mb-8">
            <div class="flex items-center justify-between mb-4">
              <!-- 左侧:标题和Tab -->
              <div class="flex items-center space-x-6">
                <h2 class="text-lg font-semibold text-gray-900">工作空间</h2>

                <!-- Tab标签页 -->
                <div class="flex items-center space-x-1 border-b border-gray-200">
                  <button
                    @click="handleWorkspaceTabClick('all')"
                    :class="[
                      'px-4 py-2 text-sm font-medium transition-colors relative',
                      activeWorkspaceTab === 'all'
                        ? 'text-blue-600'
                        : 'text-gray-600 hover:text-gray-900'
                    ]"
                  >
                    全部
                    <div
                      v-if="activeWorkspaceTab === 'all'"
                      class="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-600"
                    ></div>
                  </button>

                  <button
                    @click="handleWorkspaceTabClick('created')"
                    :class="[
                      'px-4 py-2 text-sm font-medium transition-colors relative',
                      activeWorkspaceTab === 'created'
                        ? 'text-blue-600'
                        : 'text-gray-600 hover:text-gray-900'
                    ]"
                  >
                    我创建的
                    <div
                      v-if="activeWorkspaceTab === 'created'"
                      class="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-600"
                    ></div>
                  </button>

                  <button
                    @click="handleWorkspaceTabClick('joined')"
                    :class="[
                      'px-4 py-2 text-sm font-medium transition-colors relative',
                      activeWorkspaceTab === 'joined'
                        ? 'text-blue-600'
                        : 'text-gray-600 hover:text-gray-900'
                    ]"
                  >
                    我加入的
                    <div
                      v-if="activeWorkspaceTab === 'joined'"
                      class="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-600"
                    ></div>
                  </button>
                </div>
              </div>

              <!-- 右侧:新建按钮和数量 -->
              <div class="flex items-center space-x-4">
                <span class="text-sm text-gray-500">{{ filteredWorkspaceList.length }} 个工作空间</span>
                <button
                  @click="handleCreateWorkspace"
                  class="flex items-center space-x-2 px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 shadow-sm hover:shadow transition-all"
                >
                  <i class="fa fa-plus"></i>
                  <span>新建工作空间</span>
                </button>
              </div>
            </div>

            <!-- 工作空间卡片列表 -->
            <div v-if="filteredWorkspaceList.length > 0" class="flex flex-wrap gap-4">
              <div
                v-for="workspace in filteredWorkspaceList"
                :key="workspace.id"
                class="workspace-card-wrapper"
              >
                <!-- 封面区域 -->
                <div class="workspace-card-cover" @click="handleWorkspaceCardClick(workspace)">
                  <img
                    v-if="workspace.workspaceIcon"
                    :src="workspace.workspaceIcon"
                    :alt="workspace.workspaceName"
                    class="cover-image"
                  />
                  <div v-else class="cover-placeholder"></div>

                  <!-- 操作按钮 -->
                  <a-dropdown :trigger="['click']" placement="bottomRight" @click.stop>
                    <div class="action-btn">
                      <i class="fa fa-ellipsis-h"></i>
                    </div>
                    <template #overlay>
                      <a-menu>
                        <a-menu-item key="edit" @click="handleEditWorkspace(workspace)">
                          <i class="fa fa-info-circle"></i> 编辑信息
                        </a-menu-item>
                        <a-menu-divider />
                        <a-menu-item key="delete" danger @click="handleDeleteWorkspace(workspace)">
                          <i class="fa fa-trash"></i> 删除
                        </a-menu-item>
                      </a-menu>
                    </template>
                  </a-dropdown>
                </div>

                <!-- 信息区域 -->
                <div class="workspace-card-info" @click="handleWorkspaceCardClick(workspace)">
                  <!-- 工作空间名称 -->
                  <div class="workspace-name">{{ workspace.workspaceName }}</div>

                  <!-- 工作空间描述 -->
                  <div class="workspace-desc">
                    <span v-if="workspace.workspaceDesc" class="desc-text">{{ workspace.workspaceDesc }}</span>
                    <span v-else class="desc-placeholder">还没有添加描述</span>
                  </div>

                  <!-- 默认空间标识 -->
                  <div v-if="workspace.isDefault === 1" class="default-badge">
                    <i class="fa fa-star"></i> 默认空间
                  </div>

                  <!-- 底部信息 -->
                  <div class="card-footer">
                    <div class="footer-item">
                      <i class="fa fa-user"></i>
                      <span>{{ workspace.ownerNickName || workspace.ownerName || '未知' }}</span>
                    </div>
                    <div class="footer-item">
                      <i class="fa fa-users"></i>
                      <span>{{ workspace.memberCount || 0 }} 人</span>
                    </div>
                    <div class="footer-item">
                      <i class="fa fa-file"></i>
                      <span>{{ workspace.contentCount || 0 }} 项</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 空状态提示 -->
            <div v-else class="text-center py-12 bg-gray-50 rounded-lg border-2 border-dashed border-gray-300">
              <i class="fa fa-briefcase text-gray-400 text-4xl mb-4"></i>
              <p class="text-gray-500 mb-4">暂无工作空间</p>
              <!-- <button
                @click="handleCreateWorkspace"
                class="inline-flex items-center space-x-2 px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700"
              >
                <i class="fa fa-plus"></i>
                <span>创建第一个工作空间</span>
              </button> -->
            </div>
          </div>

          <!-- 文件夹区域 -->
          <div v-if="displayFolders.length > 0" class="mb-8">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-lg font-semibold text-gray-900">文件夹</h2>
              <span class="text-sm text-gray-500">{{ displayFolders.length }} 个文件夹</span>
            </div>
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
              <div
                v-for="folder in displayFolders"
                :key="folder.id"
                @click="handleFolderClick(folder)"
                class="bg-white border border-gray-200 rounded-lg p-4 cursor-pointer transition-all hover:shadow-md hover:border-blue-300"
              >
                <div class="flex items-start justify-between mb-3">
                  <div class="w-12 h-12 bg-blue-50 rounded-lg flex items-center justify-center">
                    <i class="fa fa-folder text-blue-500 text-2xl"></i>
                  </div>
                  <div class="flex items-center gap-1">
                    <button @click.stop="toggleFolderExpand(folder)" class="p-1 hover:bg-gray-100 rounded" :title="expandedFolders.has(folder.id) ? '折叠' : '展开'">
                      <i :class="['fa', expandedFolders.has(folder.id) ? 'fa-angle-up' : 'fa-angle-down']"></i>
                    </button>
                    <a-dropdown :trigger="['click']" placement="bottomRight" @click.stop>
                      <button class="p-1 hover:bg-gray-100 rounded">
                        <i class="fa fa-ellipsis-h text-gray-400"></i>
                      </button>
                      <template #overlay>
                        <a-menu>
                          <a-menu-item key="rename" @click="handleRenameFolder(folder)">
                            <i class="fa fa-edit"></i> 重命名
                          </a-menu-item>
                          <a-menu-item key="delete" danger @click="handleDeleteFolderWithConfirm(folder)">
                            <i class="fa fa-trash"></i> 删除
                          </a-menu-item>
                        </a-menu>
                      </template>
                    </a-dropdown>
                  </div>
                </div>
                <h3 class="font-medium text-gray-900 mb-1 truncate">{{ folder.name }}</h3>
                <div class="flex items-center justify-between text-xs text-gray-500">
                  <span>{{ folder.childCount || 0 }} 个项目</span>
                  <span>{{ formatDate(folder.updateTime) }}</span>
                </div>
                <!-- 子项展开区域 -->
                <div v-if="expandedFolders.has(folder.id)" class="mt-3 pt-3 border-t border-gray-100">
                  <div v-if="folderExpandLoadingMap[String(folder.id)]" class="flex justify-center items-center py-3 text-gray-400">
                    <LoadingOutlined />
                    <span class="ml-2 text-xs">载入中...</span>
                  </div>
                  <div v-else>
                    <div v-if="!(folderChildrenMap[String(folder.id)] && folderChildrenMap[String(folder.id)].length)" class="text-xs text-gray-400">
                      空文件夹
                    </div>
                    <div
                      v-for="child in (folderChildrenMap[String(folder.id)] || [])"
                      :key="child.nodeType + '-' + child.id"
                      class="flex items-center justify-between py-1.5 group"
                    >
                      <div class="flex items-center gap-2 min-w-0">
                        <i :class="child.nodeType === 'folder' ? 'fa fa-folder text-blue-500' : 'fa fa-file-o text-gray-500'"></i>
                        <span
                          v-if="child.nodeType === 'folder'"
                          class="text-sm text-gray-700 truncate cursor-pointer group-hover:text-blue-600"
                          @click.stop="handleEnterFolderNode(child)"
                          :title="child.name"
                        >{{ child.name }}</span>
                        <span
                          v-else
                          class="text-sm text-gray-700 truncate cursor-pointer group-hover:text-blue-600"
                          @click.stop="handleOpenFileNode(child)"
                          :title="child.name"
                        >{{ child.name }}</span>
                      </div>
                      <div v-if="child.nodeType === 'folder'" class="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                        <button class="p-1 rounded hover:bg-gray-100" @click.stop="handleRenameFolder({ id: child.id, name: child.name, workspaceId: child.workspaceId })" title="重命名">
                          <i class="fa fa-edit text-gray-400"></i>
                        </button>
                        <button class="p-1 rounded hover:bg-gray-100" @click.stop="handleDeleteFolderWithConfirm({ id: child.id, name: child.name, workspaceId: child.workspaceId })" title="删除">
                          <i class="fa fa-trash text-red-500"></i>
                        </button>
                      </div>
                    </div>
                  </div>
                </div>

              </div>
            </div>
          </div>

        <!-- 文件列表 -->
        <div>
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold text-gray-900">文件</h2>
            <div class="flex items-center space-x-2">
              <button class="p-2 text-gray-500 hover:bg-gray-100 rounded-lg">
                <i class="fa fa-th-large"></i>
              </button>
              <button class="p-2 text-blue-600 bg-blue-50 rounded-lg">
                <i class="fa fa-list"></i>
              </button>
            </div>
          </div>

          <div class="bg-white border border-gray-200 rounded-lg overflow-x-auto">
            <table class="w-full">
              <thead class="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">名称</th>
                  <th class="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">所有者</th>
                  <th class="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">修改日期</th>
                  <th class="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">大小</th>
                  <th class="px-6 py-3 text-right text-xs font-semibold text-gray-600 uppercase tracking-wider">操作</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-200">
                <tr
                  v-for="file in displayFiles"
                  :key="file.id"
                  :class="[
                    'hover:bg-gray-50 cursor-pointer transition-colors',
                    !isAuthenticated && 'opacity-60'
                  ]"
                  @click="handleFileClick(file)"
                >
                  <td class="px-6 py-4">
                    <div class="flex items-center space-x-3">
                      <div :class="['w-10 h-10 rounded-lg flex items-center justify-center', getFileIconBg(file.type)]">
                        <i :class="['fa text-lg', getFileIcon(file.type)]"></i>
                      </div>
                      <div>
                        <div class="font-medium text-gray-900">{{ file.name }}</div>
                        <div class="text-xs text-gray-500">{{ file.extension || '' }}</div>
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4">
                    <div class="flex items-center space-x-2">
                      <LocalAvatar
                        :name="file.creatorName"
                        :size="24"
                      />
                      <span class="text-sm text-gray-700">{{ file.creatorName }}</span>
                    </div>
                  </td>
                  <td class="px-6 py-4 text-sm text-gray-600">{{ formatDate(file.updateTime) }}</td>
                  <td class="px-6 py-4 text-sm text-gray-600">{{ formatFileSize(file.size) }}</td>
                  <td class="px-6 py-4 text-right">
                    <div class="flex items-center justify-end space-x-2">
                      <!-- 收藏按钮 -->
                      <button
                        @click.stop="handleToggleFavoriteForFile(file)"
                        :class="[
                          'p-2 rounded-lg transition-colors',
                          isFavoritedFile(file, favoriteVersion)
                            ? 'text-yellow-600 bg-yellow-50 hover:bg-yellow-100'
                            : 'text-gray-400 hover:text-yellow-600 hover:bg-yellow-50'
                        ]"
                        :title="isFavoritedFile(file, favoriteVersion) ? '取消收藏' : '收藏'"
                      >
                        <i :class="isFavoritedFile(file, favoriteVersion) ? 'fa fa-star' : 'fa fa-star-o'"></i>
                      </button>

                      <!-- 三点菜单 -->
                      <FileActionMenu
                        :file="file"
                        @download="handleFileDownloadWithConfirm"
                        @delete="handleFileDeleteWithConfirm"
                      />
                    </div>
                  </td>
                </tr>

                <!-- 空状态 -->
                <tr v-if="displayFiles.length === 0">
                  <td colspan="5" class="px-6 py-12 text-center">
                    <div class="flex flex-col items-center justify-center">
                      <i class="fa fa-folder-open-o text-gray-300 text-5xl mb-4"></i>
                      <p class="text-gray-500 text-sm">暂无文件</p>
                      <p v-if="!isAuthenticated" class="text-gray-400 text-xs mt-1">登录后查看您的文件</p>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- 分页器 -->
          <div v-if="totalPages > 1" class="flex items-center justify-between mt-6">
            <div class="text-sm text-gray-600">
              共 {{ totalFiles }} 个文件,共 {{ totalPages }} 页
            </div>
            <div class="flex items-center space-x-2">
              <button
                @click="currentPage > 1 && (currentPage--)"
                :disabled="currentPage === 1"
                class="px-3 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                上一页
              </button>
              <button
                v-for="page in visiblePages"
                :key="page"
                @click="currentPage = page"
                :class="[
                  'px-3 py-2 text-sm border rounded-lg',
                  currentPage === page
                    ? 'bg-blue-600 text-white border-blue-600'
                    : 'border-gray-300 hover:bg-gray-50'
                ]"
              >
                {{ page }}
              </button>
              <button
                @click="currentPage < totalPages && (currentPage++)"
                :disabled="currentPage === totalPages"
                class="px-3 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                下一页
              </button>
            </div>
          </div>
        </div>
        </div>
        <!-- 全部文件页面结束 -->

        <!-- 收藏页面 -->
        <div v-if="activeNav === 'favorite' && isAuthenticated">
          <div class="flex items-center justify-between mb-6">
            <h2 class="text-2xl font-bold text-gray-900">我的收藏</h2>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="flex justify-center items-center py-20">
            <LoadingOutlined class="text-4xl text-blue-600" />
          </div>

          <!-- 空状态 -->
          <div v-else-if="favoriteList.length === 0" class="bg-white rounded-lg border border-gray-200 p-12">
            <a-empty description="暂无收藏">
              <template #image>
                <i class="fa fa-star-o text-6xl text-gray-300"></i>
              </template>
            </a-empty>
          </div>

          <!-- 收藏列表 -->
          <div v-else class="bg-white rounded-lg border border-gray-200 overflow-hidden">
            <div
              v-for="favorite in favoriteList"
              :key="favorite.id"
              class="flex items-center px-6 py-4 border-b border-gray-100 hover:bg-gray-50 transition-colors"
              :class="{ 'opacity-60': favorite.isValid === 0 }"
            >

              <!-- 图标 -->
              <div :class="['w-12 h-12 rounded-lg flex items-center justify-center mr-4', favorite.isValid === 0 ? 'bg-gray-50 text-gray-400' : 'bg-yellow-50 text-yellow-500']">
                <i class="fa fa-star text-xl"></i>
              </div>

              <!-- 内容 -->
              <div
                class="flex-1 min-w-0 cursor-pointer"
                @click="handleFavoriteClick(favorite)"
              >
                <div class="flex items-center space-x-2">
                  <h3 class="font-medium text-gray-900 truncate">{{ favorite.favoriteName }}</h3>
                  <span v-if="favorite.isValid === 0" class="px-2 py-0.5 text-xs bg-red-100 text-red-600 rounded">已失效</span>
                </div>
                <!-- <p class="text-sm text-gray-500 truncate">{{ favorite.favoritePath }}</p> -->
                <!-- <p v-if="favorite.isValid === 0 && favorite.invalidReason" class="text-xs text-red-500 mt-1">{{ favorite.invalidReason }}</p> -->
              </div>

              <!-- 操作按钮 -->
              <div class="flex items-center space-x-2 ml-4">
                <span class="text-sm text-gray-500">{{ formatDate(favorite.createTime) }}</span>
                <button
                  @click.stop="handleUnfavorite(favorite)"
                  class="px-3 py-1 text-sm text-red-600 hover:bg-red-50 rounded transition-colors"
                >
                  取消收藏
                </button>
              </div>
            </div>
          </div>
        </div>
        <!-- 收藏页面结束 -->

      </main>
      </div>
    </div>

    <!-- 加载弹框 -->
    <a-modal
      v-model:open="loadingModalVisible"
      :closable="false"
      :footer="null"
      :maskClosable="false"
      :keyboard="false"
      centered
      width="400px"
    >
      <div class="flex items-center space-x-4 py-4">
        <!-- 左侧：加载图标 -->
        <div class="flex-shrink-0">
          <LoadingOutlined class="text-4xl text-blue-600" />
        </div>
        <!-- 右侧：提示文字 -->
        <div class="flex-1">
          <p class="text-lg font-medium text-gray-800">{{ loadingModalText }}</p>
        </div>
      </div>
    </a-modal>

    <!-- 创建工作空间弹框 -->
    <!-- 新建文件夹弹框 -->
    <a-modal
      v-model:visible="createFolderModalVisible"
      title="新建文件夹"
      :confirm-loading="createFolderLoading"
      @ok="handleCreateFolderOk"
      @cancel="createFolderModalVisible = false"
      centered
      width="480px"
      okText="确定"
      cancelText="取消"
    >
      <div class="space-y-4">
        <div v-if="!currentWorkspaceId">
          <div class="text-sm text-gray-600 mb-1">选择工作空间（仅显示我创建的）</div>
          <a-select
            v-model:value="selectedWorkspaceForNewFolder"
            :options="userCreatedWorkspaces.map(ws => ({ label: ws.workspaceName, value: ws.id }))"
            placeholder="请选择工作空间"
            style="width: 100%"
            show-search
            :filter-option="(input, option) => (option?.label ?? '').toLowerCase().includes(input.toLowerCase())"
          />
          <div v-if="userCreatedWorkspaces.length === 0" class="text-xs text-gray-400 mt-1">
            暂无个人创建的工作空间
          </div>
        </div>
        <div>
          <div class="text-sm text-gray-600 mb-1">文件夹名称</div>
          <a-input v-model:value="newFolderName" placeholder="请输入文件夹名称" @press-enter="handleCreateFolderOk" />
        </div>
      </div>
    </a-modal>

    <!-- 重命名文件夹弹框 -->
    <a-modal
      v-model:visible="renameFolderModalVisible"
      title="重命名文件夹"
      :confirm-loading="renameFolderLoading"
      @ok="handleRenameFolderOk"
      @cancel="renameFolderModalVisible = false"
      centered
      width="420px"
    >
      <div>
        <div class="text-sm text-gray-600 mb-1">新名称</div>
        <a-input v-model:value="renameNewName" placeholder="请输入新名称" @press-enter="handleRenameFolderOk" />
      </div>
    </a-modal>

    <CreateWorkspaceModal
      v-model:visible="createWorkspaceModalVisible"
      :default-workspace-type="defaultWorkspaceType"
      @success="handleCreateWorkspaceSuccess"
    />

    <!-- 编辑工作空间弹框 -->
    <EditWorkspaceModal
      v-model:visible="editWorkspaceModalVisible"
      :workspace="currentEditWorkspace"
      @success="handleEditWorkspaceSuccess"
    />

    <!-- 确认对话框 -->
    <ConfirmDialog
      :visible="confirmDialogVisible"
      :title="confirmDialogConfig.title"
      :message="confirmDialogConfig.message"
      :confirm-text="confirmDialogConfig.confirmText"
      :type="confirmDialogConfig.type"
      :loading="isDownloading || isDeleting"
      :file-info="confirmDialogConfig.fileInfo"
      @confirm="confirmDialogConfig.onConfirm"
      @cancel="confirmDialogVisible = false"
    />

    <!-- 创建分享弹窗 -->
    <CreateShareModal
      ref="createShareModalRef"
      :file-id="currentShareFileId"
      :file-name="currentShareFileName"
      @success="handleShareSuccess"
    />

    <!-- 分享链接弹窗 -->
    <ShareLinkModal ref="shareLinkModalRef" />
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/modules/auth'
import { useUserStore } from '@/stores/useUserStore'
import { useWorkspaceStore } from '@/stores/workspace'
import { getFileList, createFile, uploadFile } from '@/api/file'
import { FileType, type FileItem } from '@/types/file'
import { message } from 'ant-design-vue'
import { LoadingOutlined } from '@ant-design/icons-vue'
import { routeUtils } from '@/router'
import MainLayout from '@/components/layout/MainLayout.vue'
import CreateWorkspaceModal from '@/pages/workspace/components/CreateWorkspaceModal.vue'
import EditWorkspaceModal from '@/pages/workspace/components/EditWorkspaceModal.vue'
import FileActionMenu from '@/components/FileActionMenu.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import LocalAvatar from '@/components/common/LocalAvatar.vue'
// 导入新的 API 和类型
import { getRecentAccessList, recordFolderAccess, recordFileAccess } from '@/api/access'
import { getFavoriteList, addFavorite, removeFavorite, checkFavorite, getFavoriteByFileId } from '@/api/favorite'
import { getFileTree } from '@/api/fileTree'
import { createFolder as apiCreateFolder, renameFolder as apiRenameFolder, deleteFolders as apiDeleteFolders, checkFolderExists as apiCheckFolderExists, hasChildren as apiHasChildren, hasFiles as apiHasFiles } from '@/api/folder'
import { TargetType, AccessType } from '@/types/access'
import type { AccessRecord } from '@/types/access'
import type { Favorite } from '@/types/favorite'
import { FavoriteTargetType } from '@/types/favorite'
import type { FileTreeNode, Workspace } from '@/types/fileTree'
// 导入导航状态管理 composable
import { useNavigationState } from '@/composables/useNavigationState'
// 导入工作空间类型枚举
import { WorkspaceType } from '@/types/workspace'
// 导入文件操作相关
import { useFileActions } from '@/composables/useFileActions'
// 导入分享组件
import CreateShareModal from '@/components/share/CreateShareModal.vue'
import ShareLinkModal from '@/components/share/ShareLinkModal.vue'
import type { ShareInfo } from '@/types/share'

const router = useRouter()
const authStore = useAuthStore()
const userStore = useUserStore()
const workspaceStore = useWorkspaceStore()

// 使用导航状态管理 composable (替换原有的 activeNav, activeCategory, activeWorkspaceTab)
const { activeNav, activeCategory, activeWorkspaceTab } = useNavigationState()

// 响应式数据
const sidebarOpen = ref(false)
const showCreateMenu = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalFiles = ref(0)
const loading = ref(false)
const fileInputRef = ref<HTMLInputElement | null>(null)

// 文件操作相关
const { handleFileDownload, handleFileDelete, isDownloading, isDeleting } = useFileActions()

// 确认对话框状态
const confirmDialogVisible = ref(false)
const confirmDialogConfig = ref({
  title: '',
  message: '',
  confirmText: '确认',
  type: 'default' as 'default' | 'danger',
  fileInfo: null as any,
  onConfirm: () => {}
})

// 加载弹框状态
const loadingModalVisible = ref(false)
const loadingModalText = ref('')
// 文件夹相关状态
const createFolderModalVisible = ref(false)
const createFolderLoading = ref(false)
const newFolderName = ref('')
const selectedWorkspaceForNewFolder = ref<number | null>(null)

const renameFolderModalVisible = ref(false)
const renameFolderLoading = ref(false)
const renameTargetFolder = ref<any>(null)
const renameNewName = ref('')

// 展开/折叠相关
const expandedFolders = ref<Set<number | string>>(new Set())
const folderChildrenMap = ref<Record<string, any[]>>({})
const folderExpandLoadingMap = ref<Record<string, boolean>>({})


// 创建工作空间弹框状态
const createWorkspaceModalVisible = ref(false)

// 编辑工作空间弹框状态
const editWorkspaceModalVisible = ref(false)
const currentEditWorkspace = ref<Workspace | null>(null)

// 文件和文件夹数据
const files = ref<FileItem[]>([])
const folders = ref<FileItem[]>([])

// 新增：最近访问数据
const recentAccessList = ref<AccessRecord[]>([])

// 新增：收藏数据
const favoriteList = ref<Favorite[]>([])
const favoriteTotalCount = ref(0)
// 收藏状态Map: key为"targetType-targetId", value为favoriteId
const favoriteStatusMap = ref<Map<string, number | string>>(new Map())
// 正在添加收藏的项目（用于立即显示已收藏状态）
const pendingFavorites = ref<Set<string>>(new Set())
// 收藏状态版本号，用于强制触发响应式更新
const favoriteVersion = ref(0)

// 分享相关
const createShareModalRef = ref<InstanceType<typeof CreateShareModal>>()
const shareLinkModalRef = ref<InstanceType<typeof ShareLinkModal>>()
const currentShareFileId = ref<number | string>(0)
const currentShareFileName = ref('')

// 请求防抖标记，避免重复请求导致接口调用两次
const loadingRecent = ref(false)
const loadingFavStatus = ref(false)
const loadingFileTreeFlag = ref(false)
const loadingFavoritesFlag = ref(false)

function getStackSample() {
  return (new Error().stack || '').split('\n').slice(2, 8)
}


// 新增：文件树数据
const fileTreeNodes = ref<FileTreeNode[]>([])
const workspaceList = ref<Workspace[]>([]) // 工作空间列表
const currentWorkspaceId = ref<number | null>(null) // 当前选中的工作空间ID,null表示查看所有工作空间
const currentParentId = ref<number | null>(null) // 当前文件夹ID,null表示根目录

// 文件树分页数据
const fileTreeTotal = ref(0) // 文件树总记录数
const fileTreePages = ref(0) // 文件树总页数

// 计算属性
const isAuthenticated = computed(() => authStore.isAuthenticated)

// 导航项
const navItems = [
  { key: 'recent', label: '最近访问', icon: 'fa-clock-o', count: 0 },
  { key: 'all', label: '全部文件', icon: 'fa-home', count: 0 },
  { key: 'favorite', label: '已收藏', icon: 'fa-star-o', count: 0 },
  { key: 'shared', label: '分享文件', icon: 'fa-share-alt', count: 0 },
]

// 分类项
const categories = [
  { key: 'personal', label: '个人文档', icon: 'fa-user' },
  { key: 'team', label: '团队文档', icon: 'fa-users' },
  { key: 'department', label: '部门文档', icon: 'fa-building-o' },
]

// 团队列表 (已废弃,改为工作空间导航)
// const teams = ref([])

// 回收站
const trashCount = ref(0)

// 面包屑导航
type Crumb = { name: string; path?: string; workspaceId?: number | null; folderId?: number | null }
const breadcrumbs = ref<Crumb[]>([
  { name: '全部文件', path: '/' }
])

// 存储空间
const usedStorage = ref('1.2 GB')
const totalStorage = ref('10 GB')
const storagePercent = computed(() => 12)

// 占位数据(未登录时显示)
const placeholderFolders = [
  { id: 1, name: '我的文档', childCount: 12, updateTime: '2024-01-15', type: FileType.FOLDER, isFolder: true },
  { id: 2, name: '项目资料', childCount: 8, updateTime: '2024-01-14', type: FileType.FOLDER, isFolder: true },
  { id: 3, name: '团队协作', childCount: 5, updateTime: '2024-01-13', type: FileType.FOLDER, isFolder: true },
]

const placeholderFiles = [
  { id: 1, name: '产品需求文档.docx', type: FileType.DOCUMENT, extension: 'docx', creatorName: '张三', updateTime: '2024-01-15', size: 1024000 },
  { id: 2, name: '数据分析报告.xlsx', type: FileType.SPREADSHEET, extension: 'xlsx', creatorName: '李四', updateTime: '2024-01-14', size: 2048000 },
  { id: 3, name: '项目演示.pptx', type: FileType.PRESENTATION, extension: 'pptx', creatorName: '王五', updateTime: '2024-01-13', size: 5120000 },
  { id: 4, name: '技术文档.pdf', type: FileType.PDF, extension: 'pdf', creatorName: '赵六', updateTime: '2024-01-12', size: 3072000 },
]

// 显示的文件夹和文件
const displayFolders = computed(() => {
  return isAuthenticated.value ? folders.value : placeholderFolders
})

const displayFiles = computed(() => {
  return isAuthenticated.value ? files.value : placeholderFiles
})

// 过滤后的工作空间列表(后端已处理筛选,前端直接使用)
const filteredWorkspaceList = computed(() => {
  return isAuthenticated.value ? workspaceList.value : []
})

// 当前用户创建的工作空间列表(用于新建文件夹时选择)
const userCreatedWorkspaces = computed(() => {
  const currentUserId = authStore.userInfo?.id || authStore.userInfo?.userId
  if (!currentUserId || !workspaceList.value.length) {
    return workspaceList.value
  }
  return workspaceList.value.filter(ws => ws.ownerId === currentUserId)
})

// 根据当前选中的工作空间分类,计算默认的工作空间类型
const defaultWorkspaceType = computed(() => {
  switch (activeCategory.value) {
    case 'personal':
      return WorkspaceType.PERSONAL
    case 'team':
      return WorkspaceType.TEAM
    case 'department':
      return WorkspaceType.DEPARTMENT
    default:
      return WorkspaceType.PERSONAL
  }
})

// 分页
const totalPages = computed(() => Math.ceil(totalFiles.value / pageSize.value))
const visiblePages = computed(() => {
  const pages = []
  const maxVisible = 5
  let start = Math.max(1, currentPage.value - Math.floor(maxVisible / 2))
  let end = Math.min(totalPages.value, start + maxVisible - 1)

  if (end - start < maxVisible - 1) {
    start = Math.max(1, end - maxVisible + 1)
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

// 获取文件图标
function getFileIcon(type: FileType): string {
  const iconMap = {
    [FileType.FOLDER]: 'fa-folder',
    [FileType.DOCUMENT]: 'fa-file-text-o',
    [FileType.SPREADSHEET]: 'fa-file-excel-o',
    [FileType.PRESENTATION]: 'fa-file-powerpoint-o',
    [FileType.PDF]: 'fa-file-pdf-o',
    [FileType.IMAGE]: 'fa-file-image-o',
    [FileType.ARCHIVE]: 'fa-file-archive-o',
    [FileType.OTHER]: 'fa-file-o',
  }
  return iconMap[type] || 'fa-file-o'
}

// 获取文件图标背景色
function getFileIconBg(type: FileType): string {
  const bgMap = {
    [FileType.FOLDER]: 'bg-blue-50 text-blue-500',
    [FileType.DOCUMENT]: 'bg-blue-50 text-blue-500',
    [FileType.SPREADSHEET]: 'bg-green-50 text-green-500',
    [FileType.PRESENTATION]: 'bg-orange-50 text-orange-500',
    [FileType.PDF]: 'bg-red-50 text-red-500',
    [FileType.IMAGE]: 'bg-purple-50 text-purple-500',
    [FileType.ARCHIVE]: 'bg-yellow-50 text-yellow-500',
    [FileType.OTHER]: 'bg-gray-50 text-gray-500',
  }
  return bgMap[type] || 'bg-gray-50 text-gray-500'
}

// 格式化日期
function formatDate(dateStr: string): string {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  // 处理时区差异导致的负数天数
  if (days < 0) return '刚刚'
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days} 天前`

  return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' })
}

// 格式化文件大小
function formatFileSize(bytes: number): string {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
  return (bytes / (1024 * 1024 * 1024)).toFixed(1) + ' GB'
}

// 格式化存储空间大小(用于工作空间)
function formatStorageSize(bytes: number): string {
  if (!bytes) return '0 B'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
  return (bytes / (1024 * 1024 * 1024)).toFixed(2) + ' GB'
}

// 格式化访问时间(友好格式)
function formatAccessTime(timeStr: string): string {
  if (!timeStr) return '-'
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  // 小于1分钟
  if (diff < 60000) return '刚刚'
  // 小于1小时
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  // 小于1天
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  // 显示完整时间
  return timeStr.substring(0, 16).replace('T', ' ')
}

// 根据文件扩展名获取图标路径
function getFileIconByExtension(extension: string): string {
  if (!extension) {
    return new URL('@/assets/office_icons/icons/file_img.png', import.meta.url).href
  }

  // 移除开头的点号(如果有)
  const ext = extension.toLowerCase().replace(/^\./, '')

  // 图标映射
  const iconMap: Record<string, string> = {
    // Excel 文件
    'xlsx': 'excel.png',
    'xls': 'excel.png',
    'xlsm': 'excel.png',
    'xlsb': 'excel.png',
    'csv': 'excel.png',
    'et': 'excel.png',
    'ett': 'excel.png',

    // Word 文件
    'docx': 'word.png',
    'doc': 'word.png',
    'docm': 'word.png',
    'dot': 'word.png',
    'dotx': 'word.png',
    'wps': 'word.png',

    // PowerPoint 文件
    'pptx': 'ppt.png',
    'ppt': 'ppt.png',
    'pptm': 'ppt.png',
    'ppsx': 'ppt.png',
    'pps': 'ppt.png',
    'dps': 'ppt.png',
    'dpt': 'ppt.png',

    

    // PDF 文件
    'pdf': 'pdf.png',

    // 文本文件
    'txt': 'txt.png',
    'md': 'txt.png',
    'log': 'txt.png',
  }

  const iconFile = iconMap[ext] || 'file_img.png'

  try {
    return new URL(`../assets/office_icons/icons/${iconFile}`, import.meta.url).href
  } catch (error) {
    console.error('加载图标失败:', error)
    return new URL('@/assets/office_icons/icons/file_img.png', import.meta.url).href
  }
}

// 获取文件所有者名称
function getOwnerNickName(record: AccessRecord): string {
  if (!record.targetInfo) return '-'
  return (record.targetInfo as any).ownerNickName || '未知'
}

// 获取文件位置路径
function getLocationPath(record: AccessRecord): string {
  if (!record.targetInfo) return '未知位置'
  return (record.targetInfo as any).locationPath || '根目录'
}

// 获取文件大小
function getFileSize(record: AccessRecord): number {
  if (!record.targetInfo) return 0
  return (record.targetInfo as any).fileSize || 0
}

// 获取文件扩展名
function getFileExtension(record: AccessRecord): string {
  if (!record.targetInfo) return ''
  const info = record.targetInfo as any
  return info.fileExtension || info.fileType || ''
}

// 根据目标类型获取图标
function getTargetTypeIcon(targetType: number): string {
  const iconMap: Record<number, string> = {
    1: 'fa-briefcase', // 工作空间
    2: 'fa-folder',    // 文件夹
    3: 'fa-file-o',    // 文件
  }
  return iconMap[targetType] || 'fa-file-o'
}

// 根据目标类型获取图标背景色
function getTargetTypeIconBg(targetType: number): string {
  const bgMap: Record<number, string> = {
    1: 'bg-purple-50 text-purple-500', // 工作空间
    2: 'bg-blue-50 text-blue-500',     // 文件夹
    3: 'bg-gray-50 text-gray-500',     // 文件
  }
  return bgMap[targetType] || 'bg-gray-50 text-gray-500'
}

// 获取目标名称
function getTargetName(record: AccessRecord): string {
  if (!record.targetInfo) return '(已删除)'

  const nameFields: Record<number, string> = {
    1: 'workspaceName',
    2: 'folderName',
    3: 'fileName',
  }

  const field = nameFields[record.targetType]
  return field ? (record.targetInfo as any)[field] : '未知'
}

// 获取目标路径
function getTargetPath(record: AccessRecord): string {
  if (!record.targetInfo) return ''

  const pathFields: Record<number, string> = {
    2: 'folderPath',
    3: 'filePath',
  }

  const field = pathFields[record.targetType]
  return field ? (record.targetInfo as any)[field] : ''
}

// 菜单显示控制
let menuLeaveTimer: ReturnType<typeof setTimeout> | null = null

// 鼠标进入按钮或菜单区域
function handleMenuEnter() {
  // 清除之前的关闭定时器
  if (menuLeaveTimer) {
    clearTimeout(menuLeaveTimer)
    menuLeaveTimer = null
  }
  // 显示菜单
  showCreateMenu.value = true
}

// 鼠标离开菜单区域
function handleMenuLeave() {
  // 延迟关闭,避免误触
  menuLeaveTimer = setTimeout(() => {
    showCreateMenu.value = false
  }, 150) // 150ms延迟
}

// 创建文件/文件夹
async function handleCreateFile(type: string) {
  showCreateMenu.value = false
  if (menuLeaveTimer) {
    clearTimeout(menuLeaveTimer)
    menuLeaveTimer = null
  }

  if (!isAuthenticated.value) {
    userStore.openLoginModal()
    return
  }

  // 新建文件夹
  if (type === 'FOLDER') {
    if (!isAuthenticated.value) {
      userStore.openLoginModal()
      return
    }
    // 预选工作空间: 当前上下文的 workspace 或第一个可用工作空间
    selectedWorkspaceForNewFolder.value = currentWorkspaceId.value || (workspaceList.value[0]?.id ?? null)
    newFolderName.value = ''
    createFolderModalVisible.value = true
    return
  }

  // 根据类型映射文件扩展名和默认文件名
  const fileTypeMap: Record<string, { extension: string; name: string }> = {
    DOCUMENT: { extension: 'docx', name: '新建文档' },
    SPREADSHEET: { extension: 'xlsx', name: '新建表格' },
    PRESENTATION: { extension: 'pptx', name: '新建演示文稿' },
  }

  const fileInfo = fileTypeMap[type]
  if (!fileInfo) {
    message.error('不支持的文件类型')
    return
  }

  // 立即显示加载弹框
  loadingModalText.value = '文档创建中...'
  loadingModalVisible.value = true

  try {
    loading.value = true
    const fileName = `${fileInfo.name}.${fileInfo.extension}`

    // 调用创建文件 API,不传 workspaceId,后端会自动创建默认工作空间
    const response = await createFile({
      fileName,
      fileType: fileInfo.extension,
    })

    console.log('创建文件API响应:', response)
    console.log('文件ID:', response.data?.id)
    console.log('文件ID类型:', typeof response.data?.id)

    if (response.data) {
      message.success('创建成功')

      // 跳转到编辑页面
      console.log('准备跳转到编辑器,文件ID:', response.data.id)
      await router.push(routeUtils.getLocalizedPath(`/editor/${response.data.id}`))

      // 跳转后关闭弹框
      loadingModalVisible.value = false
    } else {
      console.error('创建文件响应中没有data字段')
      message.error('创建文件失败:响应数据异常')
      loadingModalVisible.value = false
    }
  } catch (error: any) {
    console.error('创建文件失败:', error)
    message.error(error.message || '创建文件失败')
    // 发生错误时也要关闭弹框
    loadingModalVisible.value = false
  } finally {
    loading.value = false
  }
}

// 文件夹点击 -> 进入该文件夹
async function handleFolderClick(folder: any) {
  if (!isAuthenticated.value) {
    userStore.openLoginModal()
    return
  }
  try {
    currentWorkspaceId.value = Number(folder.workspaceId)
    currentParentId.value = Number(folder.id)

    // 更新面包屑
    const baseCrumb = { name: '全部文件', workspaceId: null as any, folderId: null as any }
    if (!breadcrumbs.value.length || !('folderId' in breadcrumbs.value[0])) {
      breadcrumbs.value = [baseCrumb]
    }
    breadcrumbs.value.push({ name: folder.name, workspaceId: currentWorkspaceId.value, folderId: currentParentId.value })

    await loadFileTree(currentWorkspaceId.value, currentParentId.value)
    // 记录访问
    try { await recordFolderAccess({ folderId: Number(folder.id), workspaceId: Number(folder.workspaceId) }) } catch {}
  } catch (e) {
    console.error('进入文件夹失败:', e)
    message.error('进入文件夹失败，请稍后重试')
  }
}

// 文件点击
function handleFileClick(file: any) {
  if (!isAuthenticated.value) {
    userStore.openLoginModal()
    return
  }

  // TODO: 打开文件编辑器
  console.log('打开文件:', file)
  router.push(routeUtils.getLocalizedPath(`/editor/${file.id}`))
}

// 将访问记录转换为文件项
function convertAccessRecordToFileItem(record: AccessRecord): any {
  const targetInfo = record.targetInfo as any
  return {
    id: targetInfo?.id || record.targetId,
    name: getTargetName(record),
    type: targetInfo?.fileType || targetInfo?.fileExtension || '',
    extension: getFileExtension(record),
    size: getFileSize(record),
    path: getLocationPath(record),
    workspaceId: targetInfo?.workspaceId,
    creatorName: getOwnerNickName(record),
    updateTime: targetInfo?.updateTime || record.accessTime
  }
}

// 文件下载确认处理
async function handleFileDownloadWithConfirm(file: any) {
  if (!isAuthenticated.value) {
    userStore.openLoginModal()
    return
  }

  confirmDialogConfig.value = {
    title: '确认下载',
    message: '确定要下载这个文件吗？',
    confirmText: '下载',
    type: 'default',
    fileInfo: file,
    onConfirm: async () => {
      const success = await handleFileDownload(file, false)
      confirmDialogVisible.value = false
      if (success) {
        // 下载成功，不需要更新UI
      }
    }
  }
  confirmDialogVisible.value = true
}

// 文件删除确认处理
async function handleFileDeleteWithConfirm(file: any) {
  if (!isAuthenticated.value) {
    userStore.openLoginModal()
    return
  }

  confirmDialogConfig.value = {
    title: '确认删除',
    message: '确定要删除这个文件吗？此操作会将文件移至回收站，可以在回收站中恢复。',
    confirmText: '删除',
    type: 'danger',
    fileInfo: file,
    onConfirm: async () => {
      const success = await handleFileDelete(file)
      confirmDialogVisible.value = false
      if (success) {
        // 删除成功，刷新文件树
        await loadFileTree(currentWorkspaceId.value, currentParentId.value)
      }
    }
  }
  confirmDialogVisible.value = true
}

// 创建文件夹确认
async function handleCreateFolderOk() {
  if (!isAuthenticated.value) {
    userStore.openLoginModal();
    return;
  }
  const name = newFolderName.value.trim();
  if (!name) {
    message.warning('请输入文件夹名称');
    return;
  }
  const wsId = selectedWorkspaceForNewFolder.value ?? currentWorkspaceId.value;
  if (!wsId) {
    message.warning('请选择工作空间');
    return;
  }
  try {
    createFolderLoading.value = true;
    // 先检查是否存在
    try {
      const existsResp = await apiCheckFolderExists({
        workspaceId: Number(wsId),
        parentId: currentParentId.value ?? null,
        folderName: name,
      });
      if (existsResp?.data === true) {
        message.error('同级目录下已存在同名文件夹');
        return;
      }
    } catch (e) {
      // 检查接口失败不阻断创建，但打印日志
      console.warn('检查文件夹是否存在失败，继续尝试创建', e);
    }

    const resp = await apiCreateFolder({
      workspaceId: Number(wsId),
      parentId: currentParentId.value ?? null,
      folderName: name,
    });
    if (resp?.code === 200) {
      message.success('创建成功');
      createFolderModalVisible.value = false;
      newFolderName.value = '';
      // 创建成功后刷新文件列表
      await loadFileTree(currentWorkspaceId.value, currentParentId.value);
    }
  } catch (error: any) {
    console.error('创建文件夹失败:', error);
    message.error(error?.message || '创建失败');
  } finally {
    createFolderLoading.value = false;
  }
}

// 切换展开/折叠
async function toggleFolderExpand(folder: any) {
  const key = String(folder.id);
  if (expandedFolders.value.has(folder.id)) {
    expandedFolders.value.delete(folder.id);
    // 仅折叠，不清空缓存
    expandedFolders.value = new Set(expandedFolders.value);
  } else {
    expandedFolders.value.add(folder.id);
    expandedFolders.value = new Set(expandedFolders.value);
    if (!folderChildrenMap.value[key]) {
      await loadFolderChildren(Number(folder.workspaceId), Number(folder.id));
    }
  }
}

// 加载子节点
async function loadFolderChildren(workspaceId: number, parentId: number) {
  const key = String(parentId);
  folderExpandLoadingMap.value[key] = true;
  try {
    const resp = await getFileTree({ workspaceId, parentId });
    const nodes = resp?.data?.fileTree || [];
    folderChildrenMap.value[key] = nodes;
  } catch (e) {
    console.error('加载子节点失败:', e);
    message.error('加载子项失败');
  } finally {
    folderExpandLoadingMap.value[key] = false;
  }
}

// 从子节点进入文件夹
async function handleEnterFolderNode(node: any) {
  try {
    currentWorkspaceId.value = Number(node.workspaceId);
    currentParentId.value = Number(node.id);
    breadcrumbs.value.push({ name: node.name, workspaceId: currentWorkspaceId.value, folderId: currentParentId.value });
    await loadFileTree(currentWorkspaceId.value, currentParentId.value);
    try { await recordFolderAccess({ folderId: Number(node.id), workspaceId: Number(node.workspaceId) }); } catch {}
  } catch (e) {
    console.error('进入子文件夹失败:', e);
    message.error('进入文件夹失败');
  }
}

// 打开子节点文件
function handleOpenFileNode(node: any) {
  const fileId = Number(node.id);
  router.push(routeUtils.getLocalizedPath(`/editor/${fileId}`));
}

// 打开重命名对话框
function handleRenameFolder(folder: any) {
  renameTargetFolder.value = folder;
  renameNewName.value = folder.name || folder.folderName || '';
  renameFolderModalVisible.value = true;
}

// 确认重命名
async function handleRenameFolderOk() {
  const name = renameNewName.value.trim();
  if (!name) {
    message.warning('请输入新名称');
    return;
  }
  try {
    renameFolderLoading.value = true;
    await apiRenameFolder(renameTargetFolder.value.id, name);
    message.success('重命名成功');
    renameFolderModalVisible.value = false;
    await loadFileTree(currentWorkspaceId.value, currentParentId.value);
  } catch (e: any) {
    console.error('重命名失败:', e);
    message.error(e?.message || '重命名失败');
  } finally {
    renameFolderLoading.value = false;
  }
}

// 删除文件夹（确认）
function handleDeleteFolderWithConfirm(folder: any) {
  confirmDialogConfig.value = {
    title: '删除文件夹',
    message: `确定要删除“${folder.name || folder.folderName}”吗？删除前将检查是否包含子项。`,
    confirmText: '删除',
    type: 'danger',
    fileInfo: folder,
    onConfirm: async () => {
      try {
        // 删除前检查
        const [childrenResp, filesResp] = await Promise.all([
          apiHasChildren(folder.id),
          apiHasFiles(folder.id),
        ]);
        if (childrenResp?.data === true) {
          message.error('该文件夹下有子文件夹，不能删除');
          return;
        }
        if (filesResp?.data === true) {
          message.error('该文件夹下有文件，不能删除');
          return;
        }
        await apiDeleteFolders(folder.id);
        message.success('删除成功');
        confirmDialogVisible.value = false;
        await loadFileTree(currentWorkspaceId.value, currentParentId.value);
      } catch (e: any) {
        console.error('删除失败:', e);
        message.error(e?.message || '删除失败');
      }
    },
  } as any;
  confirmDialogVisible.value = true;
}

// 面包屑点击跳转
async function handleBreadcrumbClick(crumb: any, index: number) {
  if (index === breadcrumbs.value.length - 1) return;
  breadcrumbs.value = breadcrumbs.value.slice(0, index + 1);
  // 根
  if (index === 0) {
    currentWorkspaceId.value = null;
    currentParentId.value = null;
    await loadFileTree(null, null);
    return;
  }
  // 进入对应层
  const target = breadcrumbs.value[index];
  currentWorkspaceId.value = target.workspaceId ?? null;
  currentParentId.value = target.folderId ?? null;
  await loadFileTree(currentWorkspaceId.value, currentParentId.value);
}

// 刷新当前列表
async function refreshCurrentList() {
  switch (activeNav.value) {
    case 'recent':
      await loadRecentAccess()
      break
    case 'all':
      await loadFileTree(null, null)
      break
    case 'favorite':
      await loadFavorites()
      break
  }
}

// 处理访问记录点击
function handleAccessRecordClick(record: AccessRecord) {
  if (!record.targetInfo) {
    message.warning('该项目已被删除')
    return
  }

  // 根据目标类型跳转到不同页面
  switch (record.targetType) {
    case TargetType.WORKSPACE: // 工作空间
      router.push(routeUtils.getLocalizedPath('/workspace'))
      break
    case TargetType.FOLDER: // 文件夹
      // TODO: 跳转到文件夹详情页
      console.log('打开文件夹:', record.targetInfo)
      break
    case TargetType.FILE: // 文件
      const fileInfo = record.targetInfo as any
      router.push(routeUtils.getLocalizedPath(`/editor/${fileInfo.id}`))
      break
  }
}

// 处理收藏项点击
function handleFavoriteClick(favorite: Favorite) {
  if (favorite.isValid === 0) {
    message.warning('该项目已失效,无法打开')
    return
  }

  // 根据目标类型跳转到不同页面
  switch (favorite.targetType) {
    case FavoriteTargetType.WORKSPACE: // 工作空间
      router.push(routeUtils.getLocalizedPath('/workspace'))
      break
    case FavoriteTargetType.FOLDER: // 文件夹
      // TODO: 跳转到文件夹详情页
      console.log('打开文件夹:', favorite.targetInfo)
      break
    case FavoriteTargetType.FILE: // 文件
      if (favorite.targetInfo) {
        router.push(routeUtils.getLocalizedPath(`/editor/${favorite.targetId}`))
      }
      break
  }
}

// 取消收藏
async function handleUnfavorite(favorite: Favorite) {
  try {
    await removeFavorite(favorite.id)
    message.success('已取消收藏')
    // 重新加载收藏列表
    loadFavorites()
  } catch (error) {
    console.error('取消收藏失败:', error)
    message.error('取消收藏失败')
  }
}

// 分享文件
function handleShareFile(record: AccessRecord) {
  if (!record.targetInfo) {
    message.warning('该文件已被删除')
    return
  }

  // 只有文件类型可以分享
  if (record.targetType !== TargetType.FILE) {
    message.warning('只能分享文件')
    return
  }

  const targetInfo = record.targetInfo as any
  currentShareFileId.value = record.targetId
  currentShareFileName.value = targetInfo?.fileName || targetInfo?.name || '未知文件'

  // 打开创建分享弹窗
  createShareModalRef.value?.open()
}

// 分享成功回调
function handleShareSuccess(shareInfo: ShareInfo) {
  // 检查 shareUrl 是否存在，如果不存在则在前端构造
  if (!shareInfo.shareUrl && shareInfo.shareCode) {
    const origin = window.location.origin
    shareInfo.shareUrl = `${origin}/s/${shareInfo.shareCode}`
  }

  // 打开分享链接弹窗
  shareLinkModalRef.value?.open(shareInfo)
}

// 下载文件
async function handleDownloadFile(record: AccessRecord) {
  if (!record.targetInfo) {
    message.warning('该文件已被删除')
    return
  }

  if (record.targetType !== TargetType.FILE) {
    message.warning('只能下载文件')
    return
  }

  // TODO: 实现下载功能
  console.log('下载文件:', record)
  message.info('下载功能开发中...')
}

// 检查是否已收藏（AccessRecord）
// 第二个参数 _version 用于在模板中建立响应式依赖
function isFavoritedRecord(record: AccessRecord, _version?: number): boolean {
  const key = `${record.targetType}-${String(record.targetId)}`
  // 检查已确认的收藏 或 正在添加中的收藏
  return favoriteStatusMap.value.has(key) || pendingFavorites.value.has(key)
}

// 检查文件是否已收藏
// 第二个参数 _version 用于在模板中建立响应式依赖
function isFavoritedFile(file: FileItem, _version?: number): boolean {
  const key = `${FavoriteTargetType.FILE}-${String(file.id)}`
  // 检查已确认的收藏 或 正在添加中的收藏
  return favoriteStatusMap.value.has(key) || pendingFavorites.value.has(key)
}

// 收藏/取消收藏（AccessRecord）
async function handleToggleFavorite(record: AccessRecord) {
  if (!record.targetInfo) {
    message.warning('该项目已被删除')
    return
  }

  const favoriteKey = `${record.targetType}-${String(record.targetId)}`
  const existingFavoriteId = favoriteStatusMap.value.get(favoriteKey)
  const isPending = pendingFavorites.value.has(favoriteKey)

  try {
    if (existingFavoriteId || isPending) {
      // 如果正在添加中，提示用户稍等
      if (isPending) {
        message.info('正在添加收藏，请稍后再试')
        return
      }
      // 已收藏，弹出确认提示
      const confirmed = await new Promise<boolean>((resolve) => {
        confirmDialogConfig.value = {
          title: '取消收藏',
          message: '是否取消收藏该文件？',
          confirmText: '确认',
          type: 'default',
          fileInfo: null,
          onConfirm: () => {
            resolve(true)
            confirmDialogVisible.value = false
          }
        }
        confirmDialogVisible.value = true

        // 监听对话框关闭事件
        const timer = setTimeout(() => {
          if (!confirmDialogVisible.value) {
            resolve(false)
          }
        }, 100)
      })

      if (!confirmed) {
        console.log('[handleToggleFavorite] 用户取消了取消收藏确认')
        return
      }

      // 执行取消收藏
      await removeFavorite(existingFavoriteId)
      favoriteStatusMap.value.delete(favoriteKey)
      // 递增版本号强制触发响应式更新
      favoriteVersion.value++
      message.success('已取消收藏')
    } else {
      // 未收藏，先检查是否已收藏（防止重复）
      const checkResult = await checkFavorite({
        targetType: record.targetType as any,
        targetId: record.targetId,
      })

      if (checkResult.data?.isFavorited && checkResult.data?.favoriteId) {
        // 已经收藏过了，更新本地状态
        favoriteStatusMap.value.set(favoriteKey, checkResult.data.favoriteId)
        favoriteVersion.value++
        message.info('该项目已在收藏列表中')
      } else {
        // 添加收藏
        const targetInfo = record.targetInfo as any

        // 立即标记为"正在添加"，UI 先显示已收藏状态
        pendingFavorites.value.add(favoriteKey)
        favoriteVersion.value++

        await addFavorite({
          targetType: record.targetType as any,
          targetId: record.targetId,
          workspaceId: currentWorkspaceId.value || targetInfo?.workspaceId || targetInfo?.id,
        })

        // 添加成功后，获取真实的 favoriteId
        const favoriteResult = await getFavoriteByFileId(record.targetId)
        if (favoriteResult.data?.id) {
          favoriteStatusMap.value.set(favoriteKey, favoriteResult.data.id)
        }
        // 从 pending 中移除
        pendingFavorites.value.delete(favoriteKey)
        favoriteVersion.value++

        message.success('已添加到收藏')
      }
    }

    // 如果当前在收藏页面，重新加载收藏列表
    if (activeNav.value === 'favorite') {
      loadFavorites()
    }
  } catch (error) {
    // 出错时从 pending 中移除
    pendingFavorites.value.delete(favoriteKey)
    favoriteVersion.value++
    console.error('收藏操作失败:', error)
    message.error('收藏操作失败')
  }
}

// 收藏/取消收藏（文件）
async function handleToggleFavoriteForFile(file: any) {
  const favoriteKey = `${FavoriteTargetType.FILE}-${String(file.id)}`
  const existingFavoriteId = favoriteStatusMap.value.get(favoriteKey)
  const isPending = pendingFavorites.value.has(favoriteKey)

  try {
    if (existingFavoriteId || isPending) {
      // 如果正在添加中，提示用户稍等
      if (isPending) {
        message.info('正在添加收藏，请稍后再试')
        return
      }
      // 已收藏，弹出确认提示
      const confirmed = await new Promise<boolean>((resolve) => {
        confirmDialogConfig.value = {
          title: '取消收藏',
          message: '是否取消收藏该文件？',
          confirmText: '确认',
          type: 'default',
          fileInfo: null,
          onConfirm: () => {
            resolve(true)
            confirmDialogVisible.value = false
          }
        }
        confirmDialogVisible.value = true

        // 监听对话框关闭事件
        const timer = setTimeout(() => {
          if (!confirmDialogVisible.value) {
            resolve(false)
          }
        }, 100)
      })

      if (!confirmed) {
        return
      }

      // 执行取消收藏
      await removeFavorite(existingFavoriteId)
      favoriteStatusMap.value.delete(favoriteKey)
      // 递增版本号强制触发响应式更新
      favoriteVersion.value++
      message.success('已取消收藏')
    } else {
      // 未收藏，先检查是否已收藏（防止重复）
      const checkResult = await checkFavorite({
        targetType: FavoriteTargetType.FILE,
        targetId: file.id,
      })

      if (checkResult.data?.isFavorited && checkResult.data?.favoriteId) {
        // 已经收藏过了，更新本地状态
        favoriteStatusMap.value.set(favoriteKey, checkResult.data.favoriteId)
        favoriteVersion.value++
        message.info('该文件已在收藏列表中')
      } else {
        // 立即标记为"正在添加"，UI 先显示已收藏状态
        pendingFavorites.value.add(favoriteKey)
        favoriteVersion.value++

        // 添加收藏
        await addFavorite({
          targetType: FavoriteTargetType.FILE,
          targetId: file.id,
          workspaceId: file.workspaceId || currentWorkspaceId.value || 0,
        })

        // 添加成功后，获取真实的 favoriteId
        const favoriteResult = await getFavoriteByFileId(file.id)
        if (favoriteResult.data?.id) {
          favoriteStatusMap.value.set(favoriteKey, favoriteResult.data.id)
        }
        // 从 pending 中移除
        pendingFavorites.value.delete(favoriteKey)
        favoriteVersion.value++

        message.success('已添加到收藏')
      }
    }

    // 如果当前在收藏页面，重新加载收藏列表
    if (activeNav.value === 'favorite') {
      loadFavorites()
    }
  } catch (error) {
    // 出错时从 pending 中移除
    pendingFavorites.value.delete(favoriteKey)
    favoriteVersion.value++
    console.error('收藏操作失败:', error)
    message.error('收藏操作失败')
  }
}

// 工作空间卡片点击
async function handleWorkspaceCardClick(workspace: Workspace) {
  if (!isAuthenticated.value) {
    userStore.openLoginModal()
    return
  }

  try {
    // 显示加载提示
    const loadingMessage = message.loading(`正在打开工作空间: ${workspace.workspaceName}...`, 0)

    // 使用 routeUtils 获取带语言前缀的路径
    const localizedPath = routeUtils.getLocalizedPath(`/workspace/${workspace.id}`)

    // 跳转到工作空间详情页
    await router.push(localizedPath)

    // 关闭加载提示
    loadingMessage()
  } catch (error) {
    console.error('打开工作空间失败:', error)
    message.error('打开工作空间失败,请稍后重试')
  }
  // loadFileTree(workspace.id, null)
}

// 工作空间操作菜单
function handleWorkspaceAction(workspace: Workspace) {
  console.log('工作空间操作:', workspace)
  // TODO: 显示操作菜单(重命名、删除、设置等)
  message.info('工作空间操作菜单开发中...')
}


// 编辑工作空间信息
function handleEditWorkspace(workspace: Workspace) {
  currentEditWorkspace.value = workspace
  editWorkspaceModalVisible.value = true
}

// 编辑工作空间成功回调
function handleEditWorkspaceSuccess() {
  editWorkspaceModalVisible.value = false
  currentEditWorkspace.value = null
  message.success('工作空间更新成功')
  // 重新加载工作空间列表
  loadFileTree()
}

// 删除工作空间
async function handleDeleteWorkspace(workspace: Workspace) {
  console.log('删除工作空间:', workspace)
  message.info('删除功能开发中...')
}

// 文件夹操作菜单
function handleFolderAction(folder: any) {
  console.log('文件夹操作:', folder)
  // TODO: 显示操作菜单(重命名、删除、移动等)
  message.info('文件夹操作菜单开发中...')
}

// 工作空间Tab点击
function handleWorkspaceTabClick(tabKey: 'all' | 'created' | 'joined') {
  activeWorkspaceTab.value = tabKey
  // 重置到第一页并重新加载数据
  currentPage.value = 1
  if (activeNav.value === 'all') {
    loadFileTree(null, null)
  }
}

// 工作空间类型点击
function handleCategoryClick(categoryKey: string) {
  if (!isAuthenticated.value) {
    userStore.openLoginModal()
    return
  }

  // 如果当前不是"全部文件"导航，自动切换到"全部文件"
  // 这样可以让用户更方便地查看所选工作空间类型下的所有文件
  if (activeNav.value !== 'all') {
    activeNav.value = 'all'
  }

  activeCategory.value = categoryKey as any
  // 重置到第一页并重新加载数据
  currentPage.value = 1
  if (activeNav.value === 'all') {
    loadFileTree(null, null)
  }
}

// 新建工作空间
function handleCreateWorkspace() {
  if (!isAuthenticated.value) {
    userStore.openLoginModal()
    return
  }
  createWorkspaceModalVisible.value = true
}

// 创建工作空间成功回调
async function handleCreateWorkspaceSuccess() {
  message.success('工作空间创建成功')
  // 重新加载工作空间列表和文件树
  if (activeNav.value === 'all') {
    await loadFileTree(null, null)
  }
}

// 工作空间点击(侧边栏)
function handleWorkspaceClick() {
  if (!isAuthenticated.value) {
    userStore.openLoginModal()
    return
  }
  // 工作空间列表已移除,点击后滚动到工作空间卡片区域
  const workspaceSection = document.querySelector('.workspace-section')
  if (workspaceSection) {
    workspaceSection.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

// 回收站点击
function handleTrashClick() {
  if (!isAuthenticated.value) {
    userStore.openLoginModal()
    return
  }
  console.log('打开回收站')
  router.push(routeUtils.getLocalizedPath('/trash'))
}

// 导航项点击
function handleNavClick(item: any) {
  if (!isAuthenticated.value) {
    userStore.openLoginModal()
    return
  }

  // 如果是"分享文件",跳转到分享页面
  if (item.key === 'shared') {
    router.push(routeUtils.getLocalizedPath('/share'))
    return
  }

  console.log('[Home] handleNavClick -> 切换 activeNav', { to: item.key })
  // 仅切换导航状态，实际加载交给 watch(activeNav) 统一处理，避免重复请求
  activeNav.value = item.key
}


// 导入按钮点击
function handleImportClick() {
  if (!isAuthenticated.value) {
    userStore.openLoginModal()
    return
  }
  fileInputRef.value?.click()
}

// 文件选择处理
async function handleFileSelect(event: Event) {
  const target = event.target as HTMLInputElement
  const files = target.files

  if (!files || files.length === 0) {
    return
  }

  // 保存文件数量,因为后面会清空target.value
  const fileCount = files.length

  // 支持在线编辑的文件扩展名（包含 WPS Office 格式）
  const editableExtensions = ['docx', 'xlsx', 'pptx', 'doc', 'xls', 'ppt', 'et', 'ett', 'wps', 'dps', 'dpt']

  // 如果是单个文件且支持在线编辑,立即显示加载弹框
  if (fileCount === 1) {
    const firstFile = files[0]
    const extension = firstFile.name.split('.').pop()?.toLowerCase()
    if (extension && editableExtensions.includes(extension)) {
      loadingModalText.value = `${firstFile.name} 加载中...`
      loadingModalVisible.value = true
    }
  }

  // 上传所有选中的文件
  let lastUploadedFileId: number | string | null = null
  let canOpenEditor = false

  for (let i = 0; i < files.length; i++) {
    const file = files[i]
    try {
      loading.value = true
      // 不传 workspaceId,后端会自动创建默认工作空间
      const response = await uploadFile(file)
      message.success(`${file.name} 上传成功`)

      // 记录最后一个上传的文件信息
      if (response.data) {
        lastUploadedFileId = response.data.id

        // 检查文件扩展名是否支持在线编辑
        const extension = file.name.split('.').pop()?.toLowerCase()
        if (extension && editableExtensions.includes(extension)) {
          canOpenEditor = true
        }
      }
    } catch (error: any) {
      console.error('上传文件失败:', error)
      message.error(`${file.name} 上传失败: ${error.message || '未知错误'}`)
      // 上传失败时关闭弹框
      loadingModalVisible.value = false
    }
  }

  // 清空文件选择,允许重复选择同一文件
  target.value = ''

  // 刷新文件列表（使用 loadFileTree 以保持与当前视图一致）
  await loadFileTree(null, null)
  loading.value = false

  // 如果只上传了一个文件且支持在线编辑,直接跳转到编辑器
  if (fileCount === 1 && lastUploadedFileId && canOpenEditor) {
    // 跳转到编辑器
    await router.push(routeUtils.getLocalizedPath(`/editor/${lastUploadedFileId}`))

    // 跳转后关闭弹框
    loadingModalVisible.value = false
  } else {
    // 如果不跳转编辑器,也要关闭弹框
    loadingModalVisible.value = false
  }
}

// 加载文件列表
async function loadFiles() {
  if (!isAuthenticated.value) {
    return
  }

  loading.value = true
  try {
    const response = await getFileList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      parentId: null, // 根目录
    })

    if (response.data) {
      const allFiles = response.data.rows || []

      // 分离文件夹和文件
      folders.value = allFiles.filter(f => f.isFolder)
      files.value = allFiles.filter(f => !f.isFolder)
      totalFiles.value = response.data.total || 0
    }
  } catch (error) {
    console.error('加载文件列表失败:', error)
    message.error('加载文件列表失败')
  } finally {
    loading.value = false
  }
}

// 加载最近访问列表
async function loadRecentAccess() {
  if (!isAuthenticated.value) {
    return
  }

  loading.value = true
  try {
    const response = await getRecentAccessList({ limit: 50 })
    if (response.data) {
      recentAccessList.value = response.data
      console.log('========== 最近访问数据 ==========', {
        count: recentAccessList.value.length,
        sampleKeys: recentAccessList.value.slice(0, 20).map(r => `${r.targetType}-${r.targetId}`),
        sampleTypes: recentAccessList.value.slice(0, 5).map(r => ({ targetType: r.targetType, targetId: r.targetId, targetIdType: typeof r.targetId }))
      })

      // 同时加载收藏状态
      await loadFavoriteStatus()
    }
  } catch (error) {
    console.error('加载最近访问列表失败:', error)
    message.error('加载最近访问列表失败')
  } finally {
    loading.value = false
  }
}

// 加载收藏状态（用于更新favoriteStatusMap）
async function loadFavoriteStatus() {
  try {
    console.log('========== 开始加载收藏状态 ==========')
    const response = await getFavoriteList({ pageNum: 1, pageSize: 200 })
    console.log('[loadFavoriteStatus] 完整响应:', response)
    console.log('[loadFavoriteStatus] response.data:', response?.data)

    const rows = response?.rows || []
    console.log('[loadFavoriteStatus] rows 数量:', rows.length)
    console.log('[loadFavoriteStatus] rows 内容:', rows)

    if (rows.length > 0) {
      // 更新收藏状态Map
      favoriteStatusMap.value.clear()
      rows.forEach((favorite: any) => {
        const key = `${favorite.targetType}-${String(favorite.targetId)}`
        favoriteStatusMap.value.set(key, favorite.id)
      })

      // 递增版本号强制触发响应式更新
      favoriteVersion.value++
    }
  } catch (error) {
    console.error('加载收藏状态失败:', error)
  }
}

// 加载收藏列表
async function loadFavorites() {
  if (!isAuthenticated.value) {
    return
  }

  loading.value = true
  try {
    const response = await getFavoriteList({ pageNum: 1, pageSize: 50 })
    if (response) {
      favoriteList.value = response.rows || []
      favoriteTotalCount.value = response.total || 0

      // 更新收藏状态Map
      favoriteStatusMap.value.clear()
      favoriteList.value.forEach(favorite => {
        const key = `${favorite.targetType}-${favorite.targetId}`
        favoriteStatusMap.value.set(key, favorite.id)
      })
      // 递增版本号强制触发响应式更新
      favoriteVersion.value++
    }
  } catch (error) {
    console.error('加载收藏列表失败:', error)
    message.error('加载收藏列表失败')
  } finally {
    loading.value = false
  }
}

// 加载文件树
async function loadFileTree(workspaceId?: number | null, parentId?: number | null) {
  if (!isAuthenticated.value) {
    return
  }

  loading.value = true
  try {
    // 构建查询参数
    const params: any = {
      workspaceId: workspaceId,
      parentId: parentId,
    }

    // 如果是全局模式(workspaceId为null),添加筛选和分页参数
    if (workspaceId === null || workspaceId === undefined) {
      // 工作空间类型筛选
      const workspaceTypeMap: Record<string, number> = {
        'personal': 1,   // 个人文档
        'department': 2, // 部门文档
        'team': 3,       // 团队文档
      }
      if (activeCategory.value && activeCategory.value !== 'all') {
        params.workspaceType = workspaceTypeMap[activeCategory.value]
      }

      // 所有权类型筛选
      const ownershipTypeMap: Record<string, number> = {
        'all': 0,      // 全部
        'created': 1,  // 我创建的
        'joined': 2,   // 我加入的
      }
      if (activeWorkspaceTab.value) {
        params.ownershipType = ownershipTypeMap[activeWorkspaceTab.value]
      }

      // 分页参数
      params.pageNum = currentPage.value
      params.pageSize = pageSize.value
    }

    // 调用新的 API
    const response = await getFileTree(params)

    if (response.data) {
      // 保存工作空间列表
      workspaceList.value = response.data.workspaces || []

      // 保存文件树节点
      fileTreeNodes.value = response.data.fileTree || []

      // 保存分页信息
      if (response.data.total !== undefined) {
        fileTreeTotal.value = response.data.total
        totalFiles.value = response.data.total
      }
      if (response.data.pages !== undefined) {
        fileTreePages.value = response.data.pages
      }

      // 分离文件夹和文件用于显示
      folders.value = response.data.fileTree
        .filter(node => node.nodeType === 'folder')
        .map(node => ({
          id: node.id,
          name: node.name,
          type: FileType.FOLDER,
          isFolder: true,
          childCount: (node.fileCount || 0) + (node.folderCount || 0),
          updateTime: node.updateTime || node.createTime,
          size: 0,
          path: node.folderInfo?.folderPath || '',
          parentId: node.parentId,
          workspaceId: node.workspaceId,
          creatorId: node.folderInfo?.ownerId || 0,
          creatorName: node.folderInfo?.ownerNickName || '未知',
          status: 0,
          createTime: node.createTime,
          isFavorite: node.isFavorited,
        }))

      files.value = response.data.fileTree
        .filter(node => node.nodeType === 'file')
        .map(node => ({
          id: node.id,
          name: node.fileInfo?.fileName || node.name,
          type: getFileTypeByExtension(node.fileInfo?.fileExtension || ''),
          isFolder: false,
          size: node.fileInfo?.fileSize || 0,
          path: node.fileInfo?.filePath || '',
          parentId: node.parentId,
          workspaceId: node.workspaceId,
          creatorId: node.fileInfo?.ownerId || 0,
          creatorName: node.fileInfo?.ownerNickName || '未知',
          status: 0,
          createTime: node.createTime,
          updateTime: node.updateTime || node.createTime,
          extension: node.fileInfo?.fileExtension || node.fileInfo?.fileType || '',
          isFavorite: node.isFavorited,
        }))

      console.log('========== 全部文件: 文件列表 ==========', {
        count: files.value.length,
        firstItems: files.value.slice(0, 10).map(f => ({ id: f.id, idType: typeof f.id, name: f.name })),
      })

      // 加载收藏状态
      await loadFavoriteStatus()

      // 对比 keys
      const fileKeys = files.value.map(f => `${FavoriteTargetType.FILE}-${String(f.id)}`)
      const matches = fileKeys.filter(k => favoriteStatusMap.value.has(k))
      console.log('========== 收藏对比(全部文件) ==========', {
        fileCount: files.value.length,
        favoriteMapSize: favoriteStatusMap.value.size,
        matchCount: matches.length,
        sampleMatches: matches.slice(0, 20),
        sampleMissKeys: fileKeys.filter(k => !favoriteStatusMap.value.has(k)).slice(0, 20)
      })
    }
  } catch (error) {
    console.error('加载文件树失败:', error)
    message.error('加载文件树失败')
  } finally {
    loading.value = false
  }
}

// 根据文件扩展名获取文件类型
function getFileTypeByExtension(extension: string): FileType {
  if (!extension) return FileType.OTHER

  const ext = extension.toLowerCase().replace(/^\./, '')

  // 文档类型（包含 WPS 文字）
  if (['doc', 'docx', 'docm', 'dot', 'dotx', 'txt', 'md', 'wps'].includes(ext)) {
    return FileType.DOCUMENT
  }
  // 表格类型（包含 WPS 表格）
  if (['xls', 'xlsx', 'xlsm', 'xlsb', 'csv', 'et', 'ett'].includes(ext)) {
    return FileType.SPREADSHEET
  }
  // 演示文稿类型（包含 WPS 演示）
  if (['ppt', 'pptx', 'pptm', 'ppsx', 'pps', 'dps', 'dpt'].includes(ext)) {
    return FileType.PRESENTATION
  }
  // PDF类型
  if (ext === 'pdf') {
    return FileType.PDF
  }
  // 图片类型
  if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'svg', 'webp'].includes(ext)) {
    return FileType.IMAGE
  }
  // 压缩包类型
  if (['zip', 'rar', '7z', 'tar', 'gz'].includes(ext)) {
    return FileType.ARCHIVE
  }

  return FileType.OTHER
}

// 监听登录状态变化
watch(isAuthenticated, (newVal) => {
  if (newVal) {
    // 根据当前导航加载对应数据
    switch (activeNav.value) {
      case 'recent':
        loadRecentAccess()
        break
      case 'all':
        // 全部文件:不传 workspaceId,获取所有工作空间及其一级文件树
        loadFileTree(null, null)
        break
      case 'favorite':
        loadFavorites()
        break
      default:
        loadFiles()
    }
  } else {
    files.value = []
    folders.value = []
    recentAccessList.value = []
    favoriteList.value = []
    workspaceList.value = []
  }
})

// 监听分页变化
watch(currentPage, () => {
  if (activeNav.value === 'all') {
    // 全部文件:不传 workspaceId,获取所有工作空间及其一级文件树
    loadFileTree(null, null)
  } else {
    loadFiles()
  }
})

// 点击外部关闭下拉菜单
function handleClickOutside(event: MouseEvent) {
  const target = event.target as HTMLElement
  if (!target.closest('.relative')) {
    showCreateMenu.value = false
  }
}

// 监听导航状态变化,自动加载对应数据
watch(activeNav, (newNav, oldNav) => {
  if (!isAuthenticated.value) return
  console.log('[Home] watch(activeNav) 触发:', { oldNav, newNav })

  switch (newNav) {
    case 'recent':
      console.log('[Home] watch(activeNav) -> 调用 loadRecentAccess')
      loadRecentAccess()
      break
    case 'all':
      console.log('[Home] watch(activeNav) -> 调用 loadFileTree(null, null)')
      loadFileTree(null, null)
      break
    case 'favorite':
      console.log('[Home] watch(activeNav) -> 调用 loadFavorites')
      loadFavorites()
      break
    // 'shared' 会跳转到其他页面,不需要处理
  }
}, { immediate: false })

// 监听工作空间分类变化,重新加载数据
watch(activeCategory, () => {
  if (!isAuthenticated.value) return
  if (activeNav.value === 'all') {
    currentPage.value = 1
    loadFileTree(null, null)
  }
}, { immediate: false })

// 监听工作空间 Tab 变化,重新加载数据
watch(activeWorkspaceTab, () => {
  if (!isAuthenticated.value) return
  if (activeNav.value === 'all') {
    currentPage.value = 1
    loadFileTree(null, null)
  }
}, { immediate: false })

// 组件挂载
onMounted(() => {
  if (isAuthenticated.value) {
    // 根据当前导航状态加载对应数据
    switch (activeNav.value) {
      case 'recent':
        loadRecentAccess()
        break
      case 'all':
        loadFileTree(null, null)
        break
      case 'favorite':
        loadFavorites()
        break
    }
  }

  // 添加全局点击事件监听
  document.addEventListener('click', handleClickOutside)
})

// 组件卸载时移除事件监听
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.home-page {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 下拉菜单过渡动画 */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}

.dropdown-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}

/* 自定义滚动条 */
aside::-webkit-scrollbar {
  width: 6px;
}

aside::-webkit-scrollbar-track {
  background: #f1f1f1;
}

aside::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 3px;
}

aside::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}

/* 表格行悬停效果 */
tbody tr:hover {
  background-color: #f7fafc;
}

/* 工作空间卡片样式 */
.workspace-card-wrapper {
  width: 250px;
  height: 190px;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  border: 1px solid #e8e8e8;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  padding: 12px;
}

.workspace-card-wrapper:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  /* border-color: #1890ff; */
}

.workspace-card-cover {
  position: relative;
  height: 110px;
  flex-shrink: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

      border-radius: 8px;
    height: 64px;
    position: relative;
    width: 100%;
}

.workspace-card-cover .cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.workspace-card-cover .cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.workspace-card-cover .action-btn {
    position: absolute;
    top: 8px;
    right: 8px;
    width: 28px;
    height: 22px;
    border-radius: 4px;
     background: rgb(255 255 255 / 58%);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  color: #666;
}

.workspace-card-cover .action-btn:hover {
  background: white;
  color: #1890ff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.workspace-card-cover .action-btn i {
  font-size: 14px;
}

.workspace-card-info {
  flex: 1;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.workspace-card-info .workspace-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.workspace-card-info .workspace-desc {
  font-size: 12px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-top: -2px;
}

.workspace-card-info .workspace-desc .desc-text {
  color: #666;
}

.workspace-card-info .workspace-desc .desc-placeholder {
  color: #999;
  font-style: italic;
}

.workspace-card-info .default-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 2px 8px;
  background: #e6f7ff;
  color: #1890ff;
  font-size: 12px;
  border-radius: 4px;
  width: fit-content;
}

.workspace-card-info .default-badge i {
  font-size: 10px;
}

.workspace-card-info .card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
  font-size: 12px;
  color: #666;
  gap: 8px;
}

.workspace-card-info .card-footer .footer-item {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  min-width: 0;
}

.workspace-card-info .card-footer .footer-item i {
  flex-shrink: 0;
  color: #999;
  font-size: 12px;
}

.workspace-card-info .card-footer .footer-item span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #666;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .home-page {
    font-size: 14px;
  }
}
</style>
