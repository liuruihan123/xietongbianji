<template>
  <MainLayout>
    <div class="body">
      <!-- head -->
      <div class="section section--head">
        <div class="container">
          <div class="row">
            <!-- title -->
            <div class="col-12">
              <div class="section__title">
                <h1>My profile</h1>
              </div>
            </div>
            <!-- end title -->
          </div>
        </div>
      </div>
      <!-- end head -->

    <!-- profile -->
    <div class="section">
      <div class="container">
        <div class="row">
          <!-- tabs nav -->
          <div class="col-12 col-lg-3">
            <div class="section__tabs-profile">
              <ul class="nav nav-tabs section__tabs section__tabs--big section__tabs--profile" id="section__tabs" role="tablist">
                <li class="nav-item" role="presentation">
                  <button
                    :class="{ active: activeTab === 'dashboard' }"
                    @click="setActiveTab('dashboard')"
                    type="button"
                    role="tab"
                  >
                    新币上线监控
                  </button>
                </li>

                <li class="nav-item" role="presentation">
                  <button
                    :class="{ active: activeTab === 'investing' }"
                    @click="setActiveTab('investing')"
                    type="button"
                    role="tab"
                  >
                    即将上线新币
                  </button>
                </li>

                <li class="nav-item" role="presentation">
                  <button
                    :class="{ active: activeTab === 'intelligence' }"
                    @click="setActiveTab('intelligence')"
                    type="button"
                    role="tab"
                  >
                    情报监控
                  </button>
                </li>

                <li class="nav-item" role="presentation">
                  <button
                    :class="{ active: activeTab === 'analysis' }"
                    @click="setActiveTab('analysis')"
                    type="button"
                    role="tab"
                  >
                    情报分析
                  </button>
                </li>

                <li class="nav-item" role="presentation">
                  <button
                    :class="{ active: activeTab === 'settings' }"
                    @click="setActiveTab('settings')"
                    type="button"
                    role="tab"
                  >
                    设置
                  </button>
                </li>
              </ul>

              <!-- design elements -->
              <span class="screw screw--big-br screw--tablet"></span>
              <span class="screw screw--big-bl screw--tablet"></span>
              <span class="screw screw--big-tr screw--tablet"></span>
              <span class="screw screw--big-tl screw--tablet"></span>
            </div>
          </div>
          <!-- end tabs nav -->

          <!-- tabs content -->
          <div class="col-12 col-lg-9">
            <div class="tab-content">
              <!-- dashboard -->
              <div v-show="activeTab === 'dashboard'" class="tab-pane" id="tab-1" role="tabpanel">
                <div class="row">
                  <!-- Subscription Information Section -->
                  <div class="col-12">
                    <h3 class="profile__title">我订阅的内容</h3>
                  </div>

                  <div v-for="subscription in subscriptions" :key="subscription.id" class="col-12 col-md-6 col-lg-4 sub_item_box">
                    <!-- subscription card -->
                    <div class="stats subscription-card">
                      <div class="subscription-header">
                        <div class="exchange-info">
                          <h3 class="exchange-name">{{ subscription.exchangeName }}</h3>
                          <div class="subscription-status">
                            <label class="switch">
                              <input type="checkbox" v-model="subscription.isEnabled" @change="toggleSubscription(subscription.id)">
                              <span class="slider"></span>
                            </label>
                            <span class="status-text" :class="{ 'status-active': subscription.isEnabled }">
                              {{ subscription.isEnabled ? '已开启' : '已关闭' }}
                            </span>
                          </div>
                        </div>
                      </div>

                      <div class="subscription-body">

                        <div class="info-row">

                          <span class="info-value">{{ subscription.contactInfo }}</span>
                        </div>
                        <div class="info-row">
                          <span class="info-label">通知次数</span>
                          <span class="info-value notification-count">{{ subscription.notificationCount }}</span>
                        </div>
                      </div>

                      <!-- action buttons -->
                      <div class="subscription-actions">
                        <button class="action-btn edit-btn" @click="handleEditSubscription(subscription.id)" title="编辑">
                          <img src="@/assets/img/edit.svg" alt="编辑" />
                        </button>
                        <button class="action-btn cancel-btn" @click="handleCancelSubscription(subscription)" title="取消">
                          <img src="@/assets/img/cancel.svg" alt="取消" />
                        </button>
                      </div>

                      <!-- design elements -->
                      <span class="stats__dodger stats__dodger--left stats__dodger--orange"></span>
                      <span class="stats__dodger stats__dodger--right stats__dodger--orange"></span>
                    </div>
                    <!-- end subscription card -->
                  </div>

                  <!-- Notification History Section -->
                  <div class="col-12">
                    <div class="invest invest--big">
                      <div class="notification-header">
                        <h2 class="invest__title">已通知消息列表</h2>
                        <div class="search-container">
                          <div class="form__group search-group">
                            <input
                              type="text"
                              class="form__input search-input"
                              placeholder="搜索交易所、币符号、通知类型..."
                              v-model="searchQuery"
                            >
                            <i class="ti ti-search search-icon"></i>
                          </div>
                        </div>
                      </div>

                      <div class="table-container">
                        <table class="invest__table notification-table">
                          <thead>
                            <tr>
                              <th>交易所</th>
                              <th>币符号</th>
                              <th>交易对</th>
                              <th>通知类型</th>
                              <th>通知状态</th>
                              <th>通知内容</th>
                              <th>通知时间</th>
                            </tr>
                          </thead>
                          <tbody>
                            <tr v-for="notification in filteredNotifications" :key="notification.id">
                              <td class="exchange-cell">{{ notification.exchangeCode }}</td>
                              <td class="symbol-cell">{{ notification.currencySymbol }}</td>
                              <td class="pair-cell">{{ notification.tradingPair }}</td>
                              <td class="type-cell">{{ notification.notificationType }}</td>
                              <td class="status-cell">
                                <span class="status-badge" :class="getStatusClass(notification.notificationStatus)">
                                  {{ notification.notificationStatus }}
                                </span>
                              </td>
                              <td class="content-cell">
                                <span class="content-text" :title="notification.notificationContent">
                                  {{ truncateText(notification.notificationContent, 30) }}
                                </span>
                              </td>
                              <td class="time-cell">{{ formatTime(notification.notificationTime) }}</td>
                            </tr>
                          </tbody>
                        </table>
                      </div>

                      <!-- design elements -->
                      <span class="block-icon block-icon--blue">
                        <i class="ti ti-bell"></i>
                      </span>
                      <span class="screw screw--lines-bl"></span>
                      <span class="screw screw--lines-br"></span>
                      <span class="screw screw--lines-tr"></span>
                    </div>
                  </div>

                  

                  
                </div>
              </div>
              <!-- end dashboard -->

              <!-- investing -->
              <div v-show="activeTab === 'investing'" class="tab-pane" id="tab-2" role="tabpanel">
                <div class="row">
                  <div class="col-12">
                    <!-- profile -->
                    <div class="profile">
                      <!-- tabs nav -->
                      <ul class="nav nav-tabs section__tabs section__tabs--left" id="section__profile-tabs1" role="tablist">
                        <li class="nav-item" role="presentation">
                          <button
                            :class="{ active: activeInvestingTab === 'active' }"
                            @click="setActiveInvestingTab('active')"
                            type="button"
                            role="tab"
                          >
                            即将上线
                          </button>
                        </li>

                        <li class="nav-item" role="presentation">
                          <button
                            :class="{ active: activeInvestingTab === 'closed' }"
                            @click="setActiveInvestingTab('closed')"
                            type="button"
                            role="tab"
                          >
                            已结束
                          </button>
                        </li>

                        <li class="nav-item" role="presentation">
                          <button
                            :class="{ active: activeInvestingTab === 'new' }"
                            @click="setActiveInvestingTab('new')"
                            type="button"
                            role="tab"
                          >
                            新增项目
                          </button>
                        </li>
                      </ul>
                      <!-- end tabs nav -->

                      <!-- tabs content -->
                      <div class="tab-content">
                        <!-- active -->
                        <div v-show="activeInvestingTab === 'active'" class="tab-pane" role="tabpanel">
                          <div class="row">
                            <div class="col-12">
                              <!-- upcoming coins -->
                              <div v-for="coin in upcomingCoins" :key="coin.id" class="deposit">
                                <div class="deposit__name">
                                  <div class="coin-header">
                                    <span class="deposit__icon deposit__icon--orange">
                                      <i class="ti ti-currency-bitcoin"></i>
                                    </span>
                                    <h3 class="deposit__title">{{ coin.currencySymbol }}</h3>
                                  </div>
                                  <span class="listing-time-badge">
                                    上线日期: {{ getListingDate(coin.timeUntilListing) }}
                                  </span>
                                </div>

                                <ul class="deposit__list">
                                  <li>交易所名称: <b>{{ coin.exchangeName }}</b></li>
                                  <li>上线剩余时间: <b>{{ getCountdown(coin.timeUntilListing) }}</b></li>
                                  <li>可信度: <b>{{ coin.credibilityScore }}/10</b></li>
                                  <li>推荐原因: <b>{{ coin.recommendationReason }}</b></li>
                                </ul>
                                
                                <div class="progressbar" :class="getProgressBarClass(coin.listingProgress)">
                                  <h3 class="progressbar__title">从发现到上线进度 (%):</h3>
                                  <div class="progress" role="progressbar" aria-label="Animated striped" :aria-valuenow="coin.listingProgress" aria-valuemin="0" aria-valuemax="100">
                                    <div class="progress-bar progress-bar-striped progress-bar-animated" :style="`width: ${coin.listingProgress}%`">
                                      <span>{{ coin.listingProgress }}%</span>
                                    </div>
                                  </div>
                                  <div class="progressbar__values">
                                    <span class="progressbar__value progressbar__value--left">0%</span>
                                    <span class="progressbar__value progressbar__value--right">100%</span>
                                  </div>
                                </div>

                                <div class="deposit__profit">
                                  <span>相关重要线索</span>
                                  <div class="clues-list">
                                    <div v-for="(clue, index) in coin.relatedClues" :key="index" class="clue-item">
                                      <div class="clue-number">{{ index + 1 }}</div>
                                      <div class="clue-source-box">
                                        <div class="clue-icon">
                                          <img v-if="clue.icon" :src="clue.icon" :alt="clue.source" class="clue-icon-img">
                                          <div v-else class="clue-icon-placeholder">
                                            {{ clue.source.charAt(0).toUpperCase() }}
                                          </div>
                                        </div>
                                        <span class="clue-source-name">{{ clue.source }}</span>
                                      </div>
                                      <a :href="clue.url" target="_blank" class="clue-title" :title="clue.title">
                                        {{ clue.title }}
                                      </a>
                                      <a :href="clue.url" target="_blank" class="clue-detail-icon">
                                        <i class="ti ti-external-link"></i>
                                      </a>
                                    </div>
                                  </div>
                                </div>
                              </div>
                              <!-- end upcoming coins -->
                            </div>
                          </div>
                        </div>
                        <!-- end active -->

                        <!-- closed -->
                        <div v-show="activeInvestingTab === 'closed'" class="tab-pane" role="tabpanel">
                          <div class="row">
                            <div class="col-12">
                              <!-- info -->
                              <div class="profile__info">
                                <p><i class="ti ti-info-circle"></i> 暂无已结束的项目</p>
                              </div>
                              <!-- end info -->
                            </div>
                          </div>
                        </div>
                        <!-- end closed -->

                        <!-- new -->
                        <div v-show="activeInvestingTab === 'new'" class="tab-pane" role="tabpanel">
                          <div class="row">
                            <div class="col-12">
                              <h3 class="profile__title">添加新的监控项目</h3>
                            </div>

                            <div class="col-12 col-xl-6">
                              <div class="form__group">
                                <label for="newCoinSymbol" class="form__label">币种符号</label>
                                <input id="newCoinSymbol" type="text" name="newCoinSymbol" class="form__input" v-model="newCoinForm.symbol">
                              </div>
                            </div>

                            <div class="col-12 col-xl-6">
                              <div class="form__group">
                                <label for="newCoinExchange" class="form__label">交易所</label>
                                <select name="newCoinExchange" id="newCoinExchange" class="form__select" v-model="newCoinForm.exchange">
                                  <option value="Binance">Binance</option>
                                  <option value="OKX">OKX</option>
                                  <option value="Coinbase">Coinbase</option>
                                </select>
                              </div>
                            </div>

                            <div class="col-12">
                              <button class="form__btn form__btn--small" type="button" @click="addNewCoin">添加监控</button>
                            </div>
                          </div>
                        </div>
                        <!-- end new -->
                      </div>
                      <!-- end tabs content -->

                      <!-- design elements -->
                      <span class="screw screw--lines-bl"></span>
                      <span class="screw screw--lines-br"></span>
                      <span class="screw screw--lines-tr"></span>
                      <span class="screw screw--lines-tl"></span>
                    </div>
                    <!-- end profile -->
                  </div>
                </div>
              </div>
              <!-- end investing -->

              <!-- intelligence monitoring -->
              <div v-show="activeTab === 'intelligence'" class="tab-pane" id="tab-3" role="tabpanel">
                <div class="row">
                  <div class="col-12">
                    <!-- profile -->
                    <div class="profile">
                      <!-- tabs nav -->
                      <ul class="nav nav-tabs section__tabs section__tabs--left" id="section__profile-tabs2" role="tablist">
                        <li class="nav-item" role="presentation">
                          <button
                            :class="{ active: activeIntelligenceTab === 'public' }"
                            @click="setActiveIntelligenceTab('public')"
                            type="button"
                            role="tab"
                          >
                            公共情报
                          </button>
                        </li>

                        <li class="nav-item" role="presentation">
                          <button
                            :class="{ active: activeIntelligenceTab === 'subscribed' }"
                            @click="setActiveIntelligenceTab('subscribed')"
                            type="button"
                            role="tab"
                          >
                            我的订阅
                          </button>
                        </li>

                        <li class="nav-item" role="presentation">
                          <button
                            :class="{ active: activeIntelligenceTab === 'config' }"
                            @click="setActiveIntelligenceTab('config')"
                            type="button"
                            role="tab"
                          >
                            配置
                          </button>
                        </li>
                      </ul>
                      <!-- end tabs nav -->

                      <!-- tabs content -->
                      <div class="tab-content">
                        <!-- public intelligence -->
                        <div v-show="activeIntelligenceTab === 'public'" class="tab-pane" role="tabpanel">
                          <div class="row">
                            <div class="col-12">
                              <h3 class="profile__title">公共情报</h3>
                            </div>

                            <!-- intelligence list -->
                            <div v-for="intel in publicIntelligence" :key="intel.id" class="col-12 col-md-6 col-xl-4">
                              <div class="stats intelligence-card">
                                <div class="intelligence-header">
                                  <img :src="intel.avatar" :alt="intel.nickname" class="intelligence-avatar">
                                  <div class="intelligence-info">
                                    <h4 class="intelligence-nickname">{{ intel.nickname }}</h4>
                                    <span class="intelligence-source">{{ intel.source }}</span>
                                  </div>
                                </div>

                                <div class="intelligence-image" v-if="intel.image">
                                  <img :src="intel.image" :alt="intel.nickname">
                                </div>

                                <div class="intelligence-content">
                                  <p class="intelligence-summary">{{ truncateText(intel.summary, 150) }}</p>
                                </div>

                                <div class="intelligence-footer">
                                  <span class="intelligence-time">{{ formatTime(intel.publishTime) }}</span>
                                </div>

                                <!-- design elements -->
                                <span class="stats__dodger stats__dodger--left stats__dodger--blue"></span>
                                <span class="stats__dodger stats__dodger--right stats__dodger--blue"></span>
                              </div>
                            </div>
                            <!-- end intelligence list -->
                          </div>
                        </div>
                        <!-- end public intelligence -->

                        <!-- my subscriptions -->
                        <div v-show="activeIntelligenceTab === 'subscribed'" class="tab-pane" role="tabpanel">
                          <div class="row">
                            <div class="col-12">
                              <h3 class="profile__title">我的订阅</h3>
                            </div>

                            <!-- intelligence list -->
                            <div v-for="intel in subscribedIntelligence" :key="intel.id" class="col-12 col-md-6 col-xl-4">
                              <div class="stats intelligence-card">
                                <div class="intelligence-header">
                                  <img :src="intel.avatar" :alt="intel.nickname" class="intelligence-avatar">
                                  <div class="intelligence-info">
                                    <h4 class="intelligence-nickname">{{ intel.nickname }}</h4>
                                    <span class="intelligence-source">{{ intel.source }}</span>
                                  </div>
                                </div>

                                <div class="intelligence-image" v-if="intel.image">
                                  <img :src="intel.image" :alt="intel.nickname">
                                </div>

                                <div class="intelligence-content">
                                  <p class="intelligence-summary">{{ truncateText(intel.summary, 150) }}</p>
                                </div>

                                <div class="intelligence-footer">
                                  <span class="intelligence-time">{{ formatTime(intel.publishTime) }}</span>
                                </div>

                                <!-- design elements -->
                                <span class="stats__dodger stats__dodger--left stats__dodger--green"></span>
                                <span class="stats__dodger stats__dodger--right stats__dodger--green"></span>
                              </div>
                            </div>
                            <!-- end intelligence list -->
                          </div>
                        </div>
                        <!-- end my subscriptions -->

                        <!-- configuration -->
                        <div v-show="activeIntelligenceTab === 'config'" class="tab-pane" role="tabpanel">
                          <div class="row">
                            <div class="col-12">
                              <h3 class="profile__title">订阅配置</h3>
                            </div>

                            <div class="col-12">
                              <div class="form__group">
                                <label class="form__label">第一步：选择信息渠道</label>
                                <div class="channel-selection">
                                  <label v-for="channel in channels" :key="channel.id" class="channel-option">
                                    <input
                                      type="radio"
                                      name="channel"
                                      :value="channel.id"
                                      v-model="intelligenceConfig.selectedChannel"
                                      @change="loadAccounts(channel.id)"
                                    >
                                    <span class="channel-name">{{ channel.name }}</span>
                                  </label>
                                </div>
                              </div>
                            </div>

                            <div class="col-12" v-if="intelligenceConfig.selectedChannel">
                              <div class="form__group">
                                <label class="form__label">第二步：选择具体账号</label>
                                <select class="form__select" v-model="intelligenceConfig.selectedAccount">
                                  <option value="">请选择账号</option>
                                  <option v-for="account in availableAccounts" :key="account.id" :value="account.id">
                                    {{ account.name }}
                                  </option>
                                </select>
                              </div>
                            </div>

                            <div class="col-12" v-if="intelligenceConfig.selectedAccount">
                              <div class="form__group">
                                <label class="form__label">第三步：选择通知方式（可多选）</label>
                                <div class="notification-methods">
                                  <label class="method-option">
                                    <input type="checkbox" v-model="intelligenceConfig.notifyByPhone">
                                    <span>电话通知</span>
                                  </label>
                                  <label class="method-option">
                                    <input type="checkbox" v-model="intelligenceConfig.notifyBySMS">
                                    <span>短信通知</span>
                                  </label>
                                  <label class="method-option">
                                    <input type="checkbox" v-model="intelligenceConfig.notifyByEmail">
                                    <span>邮件通知</span>
                                  </label>
                                </div>
                              </div>
                            </div>

                            <div class="col-12" v-if="intelligenceConfig.selectedAccount">
                              <button class="form__btn form__btn--small" type="button" @click="subscribeIntelligence">订阅</button>
                            </div>
                          </div>
                        </div>
                        <!-- end configuration -->
                      </div>
                      <!-- end tabs content -->

                      <!-- design elements -->
                      <span class="screw screw--lines-bl"></span>
                      <span class="screw screw--lines-br"></span>
                      <span class="screw screw--lines-tr"></span>
                      <span class="screw screw--lines-tl"></span>
                    </div>
                    <!-- end profile -->
                  </div>
                </div>
              </div>
              <!-- end intelligence monitoring -->

              <!-- intelligence analysis -->
              <div v-show="activeTab === 'analysis'" class="tab-pane" id="tab-4" role="tabpanel">
                <div class="row">
                  <div class="col-12">
                    <!-- analysis panel -->
                    <div class="invest invest--big">
                      <h2 class="invest__title">币圈情报智能分析</h2>

                      <!-- analysis cards -->
                      <div class="row">
                        <div v-for="analysis in intelligenceAnalysis" :key="analysis.id" class="col-12 col-xl-6">
                          <div class="analysis-card">
                            <div class="analysis-header">
                              <h3 class="analysis-title">{{ analysis.title }}</h3>
                              <span class="analysis-signal" :class="getSignalClass(analysis.signal)">
                                {{ analysis.signal }}
                              </span>
                            </div>

                            <div class="analysis-content">
                              <div class="analysis-section">
                                <h4 class="analysis-subtitle">分析摘要</h4>
                                <p>{{ analysis.summary }}</p>
                              </div>

                              <div class="analysis-section">
                                <h4 class="analysis-subtitle">关键指标</h4>
                                <ul class="analysis-metrics">
                                  <li v-for="(metric, index) in analysis.metrics" :key="index">
                                    <span class="metric-label">{{ metric.label }}:</span>
                                    <span class="metric-value">{{ metric.value }}</span>
                                  </li>
                                </ul>
                              </div>

                              <div class="analysis-section">
                                <h4 class="analysis-subtitle">数据来源</h4>
                                <div class="analysis-sources">
                                  <span v-for="(source, index) in analysis.sources" :key="index" class="source-tag">
                                    {{ source }}
                                  </span>
                                </div>
                              </div>

                              <div class="analysis-section">
                                <h4 class="analysis-subtitle">建议操作</h4>
                                <p class="analysis-recommendation">{{ analysis.recommendation }}</p>
                              </div>
                            </div>

                            <div class="analysis-footer">
                              <span class="analysis-time">更新时间: {{ formatTime(analysis.updateTime) }}</span>
                            </div>
                          </div>
                        </div>
                      </div>
                      <!-- end analysis cards -->

                      <!-- design elements -->
                      <span class="block-icon block-icon--purple">
                        <i class="ti ti-chart-line"></i>
                      </span>
                      <span class="screw screw--lines-bl"></span>
                      <span class="screw screw--lines-br"></span>
                      <span class="screw screw--lines-tr"></span>
                    </div>
                    <!-- end analysis panel -->
                  </div>
                </div>
              </div>
              <!-- end intelligence analysis -->

              <!-- settings -->
              <div v-show="activeTab === 'settings'" class="tab-pane" id="tab-5" role="tabpanel">
                <div class="row">
                  <div class="col-12">
                    <!-- profile -->
                    <div class="profile">
                      <!-- tabs nav -->
                      <ul class="nav nav-tabs section__tabs section__tabs--left" id="section__profile-tabs3" role="tablist">
                        <li class="nav-item" role="presentation">
                          <button
                            :class="{ active: activeSettingsTab === 'profile' }"
                            @click="setActiveSettingsTab('profile')"
                            type="button"
                            role="tab"
                          >
                            Profile
                          </button>
                        </li>

                        <li class="nav-item" role="presentation">
                          <button
                            :class="{ active: activeSettingsTab === 'wallets' }"
                            @click="setActiveSettingsTab('wallets')"
                            type="button"
                            role="tab"
                          >
                            Wallets
                          </button>
                        </li>

                        <li class="nav-item" role="presentation">
                          <button
                            :class="{ active: activeSettingsTab === 'password' }"
                            @click="setActiveSettingsTab('password')"
                            type="button"
                            role="tab"
                          >
                            Password
                          </button>
                        </li>
                      </ul>
                      <!-- end tabs nav -->

                      <!-- tabs content -->
                      <div class="tab-content">
                        <!-- profile -->
                        <div v-show="activeSettingsTab === 'profile'" class="tab-pane" role="tabpanel">
                          <div class="row">
                            <div class="col-12">
                              <h3 class="profile__title">Personal information</h3>
                            </div>

                            <div class="col-12 col-xl-6">
                              <div class="form__group">
                                <label for="name1" class="form__label">Name</label>
                                <input id="name1" type="text" name="name1" class="form__input" v-model="profileForm.name">
                              </div>
                            </div>

                            <div class="col-12 col-xl-6">
                              <div class="form__group">
                                <label for="email1" class="form__label">Email</label>
                                <input id="email1" type="text" name="email1" class="form__input" v-model="profileForm.email">
                              </div>
                            </div>

                            <div class="col-12 col-xl-6">
                              <div class="form__group">
                                <label for="phone1" class="form__label">Phone</label>
                                <input id="phone1" type="text" name="phone1" class="form__input" v-model="profileForm.phone">
                              </div>
                            </div>

                            <div class="col-12 col-xl-6">
                              <div class="form__group">
                                <label for="telegram1" class="form__label">Telegram</label>
                                <input id="telegram1" type="text" name="telegram1" class="form__input" v-model="profileForm.telegram">
                              </div>
                            </div>

                            <div class="col-12">
                              <button class="form__btn form__btn--small" type="button" @click="saveProfile">Save</button>
                            </div>
                          </div>
                        </div>
                        <!-- end profile -->

                        <!-- wallets -->
                        <div v-show="activeSettingsTab === 'wallets'" class="tab-pane" role="tabpanel">
                          <div class="row">
                            <div class="col-12">
                              <h3 class="profile__title">Your wallets</h3>
                            </div>

                            <div class="col-12 col-xl-6">
                              <div class="form__group">
                                <label for="bitcoin1" class="form__label">Bitcoin</label>
                                <input id="bitcoin1" type="text" name="bitcoin1" class="form__input" v-model="walletsForm.bitcoin">
                              </div>
                            </div>

                            <div class="col-12 col-xl-6">
                              <div class="form__group">
                                <label for="ethereum1" class="form__label">Ethereum</label>
                                <input id="ethereum1" type="text" name="ethereum1" class="form__input" v-model="walletsForm.ethereum">
                              </div>
                            </div>

                            <div class="col-12 col-xl-6">
                              <div class="form__group">
                                <label for="vm1" class="form__label">Visa/Mastercard</label>
                                <input id="vm1" type="text" name="vm1" class="form__input" v-model="walletsForm.visaMastercard">
                              </div>
                            </div>

                            <div class="col-12 col-xl-6">
                              <div class="form__group">
                                <label for="paypal1" class="form__label">Paypal</label>
                                <input id="paypal1" type="text" name="paypal1" class="form__input" v-model="walletsForm.paypal">
                              </div>
                            </div>

                            <div class="col-12">
                              <button class="form__btn form__btn--small" type="button" @click="saveWallets">Save</button>
                            </div>
                          </div>
                        </div>
                        <!-- end wallets -->

                        <!-- password -->
                        <div v-show="activeSettingsTab === 'password'" class="tab-pane" role="tabpanel">
                          <div class="row">
                            <div class="col-12">
                              <h3 class="profile__title">Change password</h3>
                            </div>

                            <div class="col-12 col-xl-6">
                              <div class="form__group">
                                <label for="op1" class="form__label">Old password</label>
                                <input id="op1" type="password" name="op1" class="form__input" v-model="passwordForm.oldPassword">
                              </div>
                            </div>

                            <div class="col-12 col-xl-6">
                              <div class="form__group">
                                <label for="np1" class="form__label">New password</label>
                                <input id="np1" type="password" name="np1" class="form__input" v-model="passwordForm.newPassword">
                              </div>
                            </div>

                            <div class="col-12 col-xl-6">
                              <div class="form__group">
                                <label for="cnp1" class="form__label">Confirm new password</label>
                                <input id="cnp1" type="password" name="cnp1" class="form__input" v-model="passwordForm.confirmPassword">
                              </div>
                            </div>

                            <div class="col-12">
                              <button class="form__btn form__btn--small" type="button" @click="changePassword">Save</button>
                            </div>
                          </div>
                        </div>
                        <!-- end password -->
                      </div>
                      <!-- end tabs content -->

                      <!-- design elements -->
                      <span class="screw screw--lines-bl"></span>
                      <span class="screw screw--lines-br"></span>
                      <span class="screw screw--lines-tr"></span>
                      <span class="screw screw--lines-tl"></span>
                    </div>
                    <!-- end profile -->
                  </div>
                </div>
              </div>
              <!-- end settings -->
            </div>
          </div>
          <!-- end tabs content -->
        </div>
      </div>
    </div>
    <!-- end profile -->
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@/hooks/useAuth'
import MainLayout from '@/components/layout/MainLayout.vue'

const router = useRouter()
const { userInfo, isAuthenticated } = useAuth()

// 当前活跃的标签页
const activeTab = ref('dashboard')
const activeInvestingTab = ref('active')
const activeIntelligenceTab = ref('public')
const activeSettingsTab = ref('profile')

// 搜索查询
const searchQuery = ref('')

// 设置活跃标签页
const setActiveTab = (tab: string) => {
  activeTab.value = tab
}

// 设置投资子标签页
const setActiveInvestingTab = (tab: string) => {
  activeInvestingTab.value = tab
}

// 设置情报监控子标签页
const setActiveIntelligenceTab = (tab: string) => {
  activeIntelligenceTab.value = tab
}

// 设置设置子标签页
const setActiveSettingsTab = (tab: string) => {
  activeSettingsTab.value = tab
}

// 用户统计数据
const userStats = reactive({
  balance: 12031,
  totalDeposit: 114103,
  totalPaid: 89812
})

// 订阅信息数据
const subscriptions = reactive([
  {
    id: 1,
    exchangeName: 'Binance',
    notificationMethod: 'Phone',
    contactInfo: '+86 138****8888',
    notificationCount: 15,
    isEnabled: true
  },
  {
    id: 2,
    exchangeName: 'OKX',
    notificationMethod: 'Email',
    contactInfo: 'user@example.com',
    notificationCount: 8,
    isEnabled: true
  },
  {
    id: 3,
    exchangeName: 'Coinbase',
    notificationMethod: 'Phone',
    contactInfo: '+86 139****9999',
    notificationCount: 3,
    isEnabled: false
  }
])

// 通知历史数据
const notificationHistory = reactive([
  {
    id: 1,
    exchangeCode: 'BN',
    currencySymbol: 'BTC',
    tradingPair: 'BTC/USDT',
    notificationType: '价格预警',
    notificationStatus: '已发送',
    notificationContent: 'BTC价格突破$50,000',
    notificationTime: '2024-01-15 14:30:25'
  },
  {
    id: 2,
    exchangeCode: 'OKX',
    currencySymbol: 'ETH',
    tradingPair: 'ETH/USDT',
    notificationType: '新币上线',
    notificationStatus: '已发送',
    notificationContent: 'ETH即将在OKX上线',
    notificationTime: '2024-01-14 09:15:10'
  },
  {
    id: 3,
    exchangeCode: 'CB',
    currencySymbol: 'SOL',
    tradingPair: 'SOL/USD',
    notificationType: '交易量异常',
    notificationStatus: '发送失败',
    notificationContent: 'SOL交易量异常增长',
    notificationTime: '2024-01-13 16:45:33'
  }
])

// 即将上线新币数据
const upcomingCoins = reactive([
  {
    id: 1,
    currencySymbol: 'NEWCOIN',
    exchangeName: 'Binance',
    timeUntilListing: '2024-02-01 10:00:00',
    credibilityScore: 8.5,
    recommendationReason: '团队背景强大，技术创新度高',
    relatedClues: [
      {
        source: 'Binance',
        title: 'Binance官方宣布即将上线NEWCOIN，预计将带来重大市场影响',
        url: 'https://www.binance.com/announcement',
        icon: '/src/assets/img/exchanges/binance.png'
      },
      {
        source: 'CoinDesk',
        title: '多位知名KOL推荐NEWCOIN，认为其技术创新度极高',
        url: 'https://www.coindesk.com/news',
        icon: ''
      },
      {
        source: 'Twitter',
        title: 'NEWCOIN团队背景强大，获得顶级投资机构支持',
        url: 'https://twitter.com/newcoin',
        icon: ''
      }
    ],
    listingProgress: 75
  },
  {
    id: 2,
    currencySymbol: 'GOLDDOG',
    exchangeName: 'OKX',
    timeUntilListing: '2024-01-25 15:30:00',
    credibilityScore: 9.2,
    recommendationReason: '社区热度极高，项目方透明度好',
    relatedClues: [
      {
        source: 'OKX',
        title: 'OKX官方确认GOLDDOG上线时间，社区反响热烈',
        url: 'https://www.okx.com/announcement',
        icon: '/src/assets/img/exchanges/okx.png'
      },
      {
        source: 'CoinTelegraph',
        title: '顶级投资机构参与GOLDDOG项目，项目方透明度获得高度评价',
        url: 'https://cointelegraph.com/news',
        icon: ''
      }
    ],
    listingProgress: 85
  },
  {
    id: 3,
    currencySymbol: 'MOONSHOT',
    exchangeName: 'Coinbase',
    timeUntilListing: '2024-01-30 12:00:00',
    credibilityScore: 7.8,
    recommendationReason: '技术创新突破，市场需求大',
    relatedClues: [
      {
        source: 'Coinbase',
        title: 'Coinbase内部消息确认MOONSHOT即将上线',
        url: 'https://www.coinbase.com/blog',
        icon: '/src/assets/img/exchanges/coinbase.png'
      },
      {
        source: 'Reddit',
        title: '多个KOL在社交媒体提及MOONSHOT，市场关注度持续上升',
        url: 'https://www.reddit.com/r/cryptocurrency',
        icon: ''
      }
    ],
    listingProgress: 60
  }
])

// 新币表单
const newCoinForm = reactive({
  symbol: '',
  exchange: 'Binance'
})

// 投资表单
const investForm = reactive({
  tariff: '0',
  period: '0',
  amount: ''
})

// 情报监控配置
const intelligenceConfig = reactive({
  selectedChannel: '',
  selectedAccount: '',
  notifyByPhone: false,
  notifyBySMS: false,
  notifyByEmail: false
})

// 可用账号列表
const availableAccounts = ref<Array<{id: string, name: string}>>([])

// 信息渠道列表
const channels = reactive([
  { id: 'twitter', name: 'X/Twitter' },
  { id: 'facebook', name: 'Facebook' },
  { id: 'pnews', name: 'Pnews' },
  { id: 'telegram', name: 'Telegram' },
  { id: 'reddit', name: 'Reddit' }
])

// 公共情报数据
const publicIntelligence = reactive([
  {
    id: 1,
    avatar: 'https://coin-images.coingecko.com/coins/images/55684/small/coingeckoupdate.png',
    nickname: 'CryptoWhale',
    source: 'X/Twitter',
    image: 'https://coin-images.coingecko.com/coins/images/55684/small/coingeckoupdate.png',
    summary: 'Bitcoin突破新高！市场情绪高涨，多个技术指标显示强势上涨趋势。建议关注回调买入机会。',
    publishTime: '2024-01-15 14:30:00'
  },
  {
    id: 2,
    avatar: 'https://coin-images.coingecko.com/coins/images/55684/small/coingeckoupdate.png',
    nickname: 'BlockchainNews',
    source: 'Facebook',
    image: '',
    summary: '以太坊2.0升级进展顺利，预计将在下个月完成最后阶段。这将大幅提升网络性能和降低gas费用。',
    publishTime: '2024-01-15 13:15:00'
  },
  {
    id: 3,
    avatar: 'https://coin-images.coingecko.com/coins/images/55684/small/coingeckoupdate.png',
    nickname: 'DeFiAnalyst',
    source: 'Pnews',
    image: 'https://coin-images.coingecko.com/coins/images/55684/small/coingeckoupdate.png',
    summary: 'DeFi协议TVL创历史新高，达到1000亿美元。主要增长来自于新兴的Layer2解决方案。',
    publishTime: '2024-01-15 12:00:00'
  }
])

// 订阅情报数据
const subscribedIntelligence = reactive([
  {
    id: 1,
    avatar: 'https://coin-images.coingecko.com/coins/images/55684/small/coingeckoupdate.png',
    nickname: 'MyFavoriteAnalyst',
    source: 'X/Twitter',
    image: 'https://coin-images.coingecko.com/coins/images/55684/small/coingeckoupdate.png',
    summary: '今日市场分析：BTC在50000美元附近形成强支撑，短期内有望突破52000美元阻力位。',
    publishTime: '2024-01-15 15:00:00'
  }
])

// 情报分析数据
const intelligenceAnalysis = reactive([
  {
    id: 1,
    title: 'BTC 短期走势分析',
    signal: '买入',
    summary: '基于多个技术指标和链上数据分析，BTC当前处于上升通道，建议逢低买入。',
    metrics: [
      { label: 'RSI指标', value: '65 (中性偏多)' },
      { label: 'MACD', value: '金叉形成' },
      { label: '链上活跃地址', value: '增长15%' },
      { label: '交易所流出量', value: '增加20%' }
    ],
    sources: ['Glassnode', 'CoinMetrics', 'TradingView'],
    recommendation: '建议在48000-49000美元区间分批建仓，止损设在46000美元。',
    updateTime: '2024-01-15 16:00:00'
  },
  {
    id: 2,
    title: 'ETH 生态发展分析',
    signal: '持有',
    summary: 'ETH生态持续发展，Layer2方案逐渐成熟，建议长期持有优质资产。',
    metrics: [
      { label: 'TVL增长', value: '+25%' },
      { label: 'Gas费用', value: '下降30%' },
      { label: 'DApp活跃度', value: '历史新高' },
      { label: '开发者数量', value: '增长18%' }
    ],
    sources: ['DeFi Llama', 'Dune Analytics', 'Electric Capital'],
    recommendation: '当前价格合理，建议持有并关注Layer2生态项目机会。',
    updateTime: '2024-01-15 15:30:00'
  }
])

// 个人信息表单
const profileForm = reactive({
  name: '',
  email: '',
  phone: '',
  telegram: ''
})

// 钱包表单
const walletsForm = reactive({
  bitcoin: '',
  ethereum: '',
  visaMastercard: '',
  paypal: ''
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 推荐链接
const referralLink = computed(() => {
  if (userInfo.value) {
    return `https://bicanmou.com/${userInfo.value.userId}`
  }
  return 'https://bicanmou.com'
})

// 过滤后的通知历史
const filteredNotifications = computed(() => {
  if (!searchQuery.value) {
    return notificationHistory
  }

  const query = searchQuery.value.toLowerCase()
  return notificationHistory.filter(notification =>
    notification.exchangeCode.toLowerCase().includes(query) ||
    notification.currencySymbol.toLowerCase().includes(query) ||
    notification.tradingPair.toLowerCase().includes(query) ||
    notification.notificationType.toLowerCase().includes(query) ||
    notification.notificationStatus.toLowerCase().includes(query) ||
    notification.notificationContent.toLowerCase().includes(query)
  )
})

// 推荐统计
const referralStats = reactive({
  todayRegistrations: 1,
  todayClicks: 9,
  totalRegistrations: 21,
  totalClicks: 503,
  totalReferrals: 12
})

// 切换订阅状态
const toggleSubscription = (subscriptionId: number) => {
  const subscription = subscriptions.find(s => s.id === subscriptionId)
  if (subscription) {
    console.log(`订阅 ${subscription.exchangeName} 状态已${subscription.isEnabled ? '开启' : '关闭'}`)
  }
}

// 处理编辑订阅
const handleEditSubscription = (subscriptionId: number) => {
  const subscription = subscriptions.find(s => s.id === subscriptionId)
  if (subscription) {
    console.log('编辑订阅:', subscription)
    // 这里可以添加编辑订阅的逻辑，比如打开编辑对话框
  }
}

// 处理取消订阅
const handleCancelSubscription = (subscription: any) => {
  const confirmed = window.confirm(`是否取消该交易所的监控？\n\n交易所: ${subscription.exchangeName}\n联系方式: ${subscription.contactInfo}`)

  if (confirmed) {
    // 从订阅列表中移除该订阅
    const index = subscriptions.findIndex(s => s.id === subscription.id)
    if (index !== -1) {
      subscriptions.splice(index, 1)
      console.log(`已取消 ${subscription.exchangeName} 的监控`)
    }
  }
}

// 获取倒计时
const getCountdown = (targetTime: string) => {
  const now = new Date().getTime()
  const target = new Date(targetTime).getTime()
  const difference = target - now

  if (difference <= 0) {
    return '已上线'
  }

  const days = Math.floor(difference / (1000 * 60 * 60 * 24))
  const hours = Math.floor((difference % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((difference % (1000 * 60 * 60)) / (1000 * 60))

  return `${days}天 ${hours}小时 ${minutes}分钟`
}

// 添加新币监控
const addNewCoin = () => {
  if (newCoinForm.symbol && newCoinForm.exchange) {
    const newCoin = {
      id: upcomingCoins.length + 1,
      currencySymbol: newCoinForm.symbol,
      exchangeName: newCoinForm.exchange,
      timeUntilListing: '2024-02-15 10:00:00', // 默认时间
      credibilityScore: 7.0,
      recommendationReason: '用户自定义监控',
      relatedClues: [
        {
          source: '用户添加',
          title: '用户手动添加的监控项目',
          url: '#',
          icon: ''
        }
      ],
      listingProgress: 50 // 默认进度50%
    }
    upcomingCoins.push(newCoin)
    newCoinForm.symbol = ''
    newCoinForm.exchange = 'Binance'
    console.log('新币监控已添加:', newCoin)
  }
}

// 加载账号列表
const loadAccounts = (channelId: string) => {
  // 模拟根据渠道加载账号
  const accountsMap: Record<string, Array<{id: string, name: string}>> = {
    twitter: [
      { id: 'tw1', name: '@elonmusk' },
      { id: 'tw2', name: '@VitalikButerin' },
      { id: 'tw3', name: '@cz_binance' }
    ],
    facebook: [
      { id: 'fb1', name: 'Crypto News Official' },
      { id: 'fb2', name: 'Blockchain Today' }
    ],
    pnews: [
      { id: 'pn1', name: 'CoinDesk' },
      { id: 'pn2', name: 'CoinTelegraph' }
    ],
    telegram: [
      { id: 'tg1', name: 'Crypto Signals' },
      { id: 'tg2', name: 'Bitcoin News' }
    ],
    reddit: [
      { id: 'rd1', name: 'r/cryptocurrency' },
      { id: 'rd2', name: 'r/bitcoin' }
    ]
  }

  availableAccounts.value = accountsMap[channelId] || []
  intelligenceConfig.selectedAccount = ''
}

// 订阅情报
const subscribeIntelligence = () => {
  if (!intelligenceConfig.selectedChannel || !intelligenceConfig.selectedAccount) {
    alert('请完成所有配置步骤')
    return
  }

  if (!intelligenceConfig.notifyByPhone && !intelligenceConfig.notifyBySMS && !intelligenceConfig.notifyByEmail) {
    alert('请至少选择一种通知方式')
    return
  }

  console.log('订阅配置:', intelligenceConfig)
  alert('订阅成功！')

  // 重置表单
  intelligenceConfig.selectedChannel = ''
  intelligenceConfig.selectedAccount = ''
  intelligenceConfig.notifyByPhone = false
  intelligenceConfig.notifyBySMS = false
  intelligenceConfig.notifyByEmail = false
  availableAccounts.value = []
}

// 获取信号样式类
const getSignalClass = (signal: string) => {
  const signalMap: Record<string, string> = {
    '买入': 'signal-buy',
    '卖出': 'signal-sell',
    '持有': 'signal-hold',
    '观望': 'signal-wait'
  }
  return signalMap[signal] || 'signal-default'
}

// 获取进度条颜色类
const getProgressBarClass = (progress: number) => {
  if (progress >= 100) {
    return 'progressbar--red' // 已结束
  } else if (progress >= 70) {
    return 'progressbar--green' // 即将上线 (70-99%)
  } else if (progress >= 30) {
    return 'progressbar--orange' // 进行中 (30-69%)
  } else {
    return 'progressbar--blue' // 刚开始 (0-29%)
  }
}

// 获取上线日期（只显示日期，不显示时分秒）
const getListingDate = (dateTimeString: string) => {
  const date = new Date(dateTimeString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 保存个人信息
const saveProfile = () => {
  console.log('个人信息表单:', profileForm)
  // 这里可以添加保存个人信息逻辑
}

// 保存钱包信息
const saveWallets = () => {
  console.log('钱包表单:', walletsForm)
  // 这里可以添加保存钱包信息逻辑
}

// 修改密码
const changePassword = () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    console.error('新密码和确认密码不匹配')
    return
  }
  console.log('密码修改表单:', passwordForm)
  // 这里可以添加修改密码逻辑
}

// 处理投资
const handleInvest = () => {
  console.log('投资表单:', investForm)
  // 这里可以添加投资逻辑
}

// 截断文本
const truncateText = (text: string, maxLength: number) => {
  if (text.length <= maxLength) {
    return text
  }
  return text.substring(0, maxLength) + '...'
}

// 获取状态样式类
const getStatusClass = (status: string) => {
  switch (status) {
    case '已发送':
      return 'status-success'
    case '发送失败':
      return 'status-error'
    case '发送中':
      return 'status-pending'
    default:
      return 'status-default'
  }
}

// 格式化时间
const formatTime = (timeString: string) => {
  const date = new Date(timeString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (minutes < 1) {
    return '刚刚'
  } else if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return timeString.split(' ')[0] // 返回日期部分
  }
}

// 组件挂载时检查登录状态
onMounted(() => {
  if (!isAuthenticated.value) {
    router.push('/signin')
  }
})
</script>

<style>

/* 修复 tab-pane 显示问题 - 让 v-show 能正常工作 */
.tab-pane {
  display: block;
}

/* 订阅卡片美化样式 */
.subscription-card {
  padding: 20px !important;
  padding-bottom: 60px !important;
  background: var(--block-bg) !important;
  border-radius: 8px;
  position: relative;
  overflow: hidden;
}

.sub_item_box{
  color: #d0d0d0;
}

.subscription-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.exchange-info {
  flex: 1;
}

.exchange-name {
  font-size: 1.4rem !important;
  font-weight: 600 !important;
  color: #d0d0d0;
  margin: 0 0 10px 0 !important;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.subscription-status {
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-text {
  font-size: 0.9rem;
  font-weight: 500;
  color: #ccc;
  transition: color 0.3s ease;
}

.status-text.status-active {
  color: var(--primary);
}

.subscription-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 0.9rem;
  color: #999;
  font-weight: 400;
}

.info-value {
  font-size: 0.95rem;
  color: var(--white);
  font-weight: 500;
}

.notification-count {
  background: linear-gradient(135deg, #c1c9c1, #4caf50);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

/* 开关样式 */
.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 24px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #eeeeee30;
  transition: .4s;
  border-radius: 24px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .slider {
   background-color: #4CAF50;
}

input:checked + .slider:before {
  transform: translateX(26px);
}

/* 订阅操作按钮样式 */
.subscription-actions {
  position: absolute;
  bottom: 15px;
  right: 15px;
  display: flex;
  gap: 10px;
  z-index: 10;
}

.action-btn {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.08);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  padding: 0;
}

.action-btn img {
  width: 18px;
  height: 18px;
  filter: brightness(0) invert(1);
  opacity: 0.7;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.action-btn:hover img {
  opacity: 1;
}

.edit-btn:hover {
  background: rgba(0, 212, 255, 0.2);
}

.edit-btn:hover img {
  filter: brightness(0) saturate(100%) invert(64%) sepia(98%) saturate(2844%) hue-rotate(163deg) brightness(101%) contrast(101%);
}

.cancel-btn:hover {
  background: rgba(255, 82, 82, 0.2);
}

.cancel-btn:hover img {
  filter: brightness(0) saturate(100%) invert(48%) sepia(79%) saturate(2476%) hue-rotate(346deg) brightness(118%) contrast(119%);
}

.action-btn:active {
  transform: translateY(0);
}

/* 情报卡片样式 */
.intelligence-card {
  padding: 20px !important;
  background: var(--block-bg) !important;
  border-radius: 8px;
  position: relative;
  overflow: hidden;
  margin-bottom: 20px;
}

.intelligence-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 15px;
}

.intelligence-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}

.intelligence-info {
  flex: 1;
}

.intelligence-nickname {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--white);
  margin: 0 0 5px 0;
}

.intelligence-source {
  font-size: 0.85rem;
  color: var(--primary);
  background: rgba(0, 212, 255, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
}

.intelligence-image {
  margin-bottom: 15px;
  border-radius: 6px;
  overflow: hidden;
}

.intelligence-image img {
  width: 100%;
  height: auto;
  display: block;
}

.intelligence-content {
  margin-bottom: 15px;
}

.intelligence-summary {
  color: #d0d0d0;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.intelligence-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.intelligence-time {
  font-size: 0.85rem;
  color: #999;
}

/* 渠道选择样式 */
.channel-selection {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-top: 10px;
}

.channel-option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 15px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.channel-option:hover {
  background: rgba(255, 255, 255, 0.1);
}

.channel-option input[type="radio"] {
  cursor: pointer;
}

.channel-name {
  color: var(--white);
  font-weight: 500;
}

/* 通知方式选择样式 */
.notification-methods {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-top: 10px;
}

.method-option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 15px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.method-option:hover {
  background: rgba(255, 255, 255, 0.1);
}

.method-option input[type="checkbox"] {
  cursor: pointer;
}

.method-option span {
  color: var(--white);
  font-weight: 500;
}

/* 情报分析卡片样式 */
.analysis-card {
  background: var(--block-bg);
  border-radius: 8px;
  padding: 25px;
  margin-bottom: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.analysis-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid rgba(255, 255, 255, 0.1);
}

.analysis-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: var(--white);
  margin: 0;
}

.analysis-signal {
  padding: 6px 16px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 0.9rem;
}

.signal-buy {
  background: linear-gradient(135deg, #4CAF50, #45a049);
  color: white;
}

.signal-sell {
  background: linear-gradient(135deg, #f44336, #da190b);
  color: white;
}

.signal-hold {
  background: linear-gradient(135deg, #2196F3, #0b7dda);
  color: white;
}

.signal-wait {
  background: linear-gradient(135deg, #FF9800, #e68900);
  color: white;
}

.analysis-content {
  color: #d0d0d0;
}

.analysis-section {
  margin-bottom: 20px;
}

.analysis-section:last-child {
  margin-bottom: 0;
}

.analysis-subtitle {
  font-size: 1rem;
  font-weight: 600;
  color: var(--primary);
  margin: 0 0 10px 0;
}

.analysis-metrics {
  list-style: none;
  padding: 0;
  margin: 0;
}

.analysis-metrics li {
  padding: 8px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.analysis-metrics li:last-child {
  border-bottom: none;
}

.metric-label {
  color: #999;
  margin-right: 10px;
}

.metric-value {
  color: var(--white);
  font-weight: 500;
}

.analysis-sources {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.source-tag {
  background: rgba(0, 212, 255, 0.1);
  color: var(--primary);
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 0.85rem;
}

.analysis-recommendation {
  background: rgba(255, 255, 255, 0.05);
  padding: 12px;
  border-radius: 6px;
  border-left: 3px solid var(--primary);
  margin: 0;
  line-height: 1.6;
}

.analysis-footer {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.analysis-time {
  font-size: 0.85rem;
  color: #999;
}

/* 线索列表样式 */
.clues-list {
  margin-top: 10px;
  max-height: 120px; /* 3条记录的高度，缩小 */
  overflow-y: auto;
  padding-right: 8px;
}

/* 自定义滚动条样式 */
.clues-list::-webkit-scrollbar {
  width: 6px;
}

.clues-list::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 3px;
}

.clues-list::-webkit-scrollbar-thumb {
  background: rgba(0, 212, 255, 0.3);
  border-radius: 3px;
}

.clues-list::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 212, 255, 0.5);
}

.clue-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 4px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.05);
  min-height: 36px;
}

.clue-item:last-child {
  margin-bottom: 0;
}

.clue-item:hover {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(0, 212, 255, 0.3);
}

.clue-number {
  width: 20px;
  height: 20px;
  background: linear-gradient(135deg, var(--primary), #0b7dda);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.7rem;
  font-weight: 600;
  flex-shrink: 0;
}

/* 来源盒子样式 - 缩小到极致 */
.clue-source-box {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 3px 8px;
  background: rgba(0, 212, 255, 0.08);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  flex-shrink: 0;
  transition: all 0.3s ease;
  height: 24px;
}

.clue-item:hover .clue-source-box {
  background: rgba(0, 212, 255, 0.12);
  border-color: rgba(0, 212, 255, 0.3);
}

.clue-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.clue-icon-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 2px;
}

.clue-icon-placeholder {
  width: 100%;
  height: 100%;
  background: #666;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 0.65rem;
}

.clue-source-name {
  font-size: 0.7rem;
  color: var(--primary);
  font-weight: 600;
  text-transform: uppercase;
  white-space: nowrap;
  line-height: 1;
}

/* 标题样式 - 可点击 */
.clue-title {
  flex: 1;
  font-size: 0.8rem;
  color: #d0d0d0;
  line-height: 1.2;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
  text-decoration: none;
  cursor: pointer;
  transition: color 0.3s ease;
}

.clue-title:hover {
  color: var(--primary);
  text-decoration: underline;
}

/* 详情图标 */
.clue-detail-icon {
  width: 26px;
  height: 26px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  border-radius: 4px;
  text-decoration: none;
  transition: all 0.3s ease;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.08);
}

.clue-detail-icon:hover {
  background: rgba(0, 212, 255, 0.3);
  color: var(--primary);
  transform: scale(1.1);
}

.clue-detail-icon i {
  font-size: 0.95rem;
}

/* deposit__name 布局调整 - 垂直排列 */
.deposit__name {
  display: flex;
  flex-direction: column;
  align-items: flex-start !important;
  gap: 0 !important;
}

/* 币头部容器 - 图标和名称水平对齐 */
.coin-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.coin-header .deposit__icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.coin-header .deposit__icon i {
  font-size: 1.5rem;
}

.coin-header .deposit__title {
  font-size: 1.1rem;
  margin: 0;
  line-height: 1;
}

/* 上线时间徽章样式 - 显示在币名称下方 */
.listing-time-badge {
  display: inline-block;
  padding: 4px 10px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a6f);
  color: white;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  margin-top: 8px;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
  animation: pulse-badge 2s ease-in-out infinite;
}

/* 徽章脉冲动画 */
@keyframes pulse-badge {
  0%, 100% {
    box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
  }
  50% {
    box-shadow: 0 2px 15px rgba(255, 107, 107, 0.6);
  }
}

/* 通知消息列表美化样式 */
.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.search-container {
  flex: 0 0 auto;
  min-width: 300px;
}

.search-group {
  position: relative;
  margin: 0 !important;
}

.search-input {
  padding-right: 45px !important;
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  color: var(--white) !important;
  font-size: 0.9rem;
}

.search-input::placeholder {
  color: #999 !important;
}

.search-input:focus {
  border-color: var(--primary) !important;
  box-shadow: 0 0 0 2px rgba(0, 212, 255, 0.2) !important;
}

.search-icon {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  font-size: 1.1rem;
  pointer-events: none;
}

.table-container {
  overflow-x: auto;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.02);
  width: 100%;
  color: #999;
}

.notification-table {
  width: 100% !important;
  margin: 0 !important;
  border-collapse: separate;
  border-spacing: 0;
}

.notification-table th {
  background: rgba(0, 212, 255, 0.1) !important;
  color: var(--primary) !important;
  font-weight: 600 !important;
  font-size: 0.9rem !important;
  padding: 15px 12px !important;
  text-align: left !important;
  border-bottom: 0px solid var(--primary) !important;
  white-space: nowrap;
}

.notification-table td {
  padding: 12px !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05) !important;
  color: var(--white) !important;
  font-size: 0.9rem !important;
  vertical-align: middle !important;
  white-space: nowrap;
}

.notification-table tbody tr:hover {
  background: rgba(255, 255, 255, 0.03) !important;
}

.exchange-cell {
  font-weight: 600 !important;
  color: var(--primary) !important;
}

.symbol-cell {
  font-weight: 600 !important;
  color: #ffd700 !important;
}

.pair-cell {
  color: #ccc !important;
}

.type-cell {
  color: #87ceeb !important;
}

.status-cell {
  text-align: center !important;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status-success {
  background: linear-gradient(135deg, #28a745, #20c997);
  color: white;
}

.status-error {
  background: linear-gradient(135deg, #dc3545, #e74c3c);
  color: white;
}

.status-pending {
  background: linear-gradient(135deg, #ffc107, #fd7e14);
  color: #333;
}

.status-default {
  background: rgba(255, 255, 255, 0.1);
  color: #ccc;
}

.content-cell {
  max-width: 200px !important;
}

.content-text {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: help;
}

.time-cell {
  color: #999 !important;
  font-size: 0.85rem !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .notification-header {
    flex-direction: column;
    align-items: stretch;
  }

  .search-container {
    min-width: auto;
  }

  .notification-table th,
  .notification-table td {
    padding: 8px 6px !important;
    font-size: 0.8rem !important;
  }

  .content-cell {
    max-width: 120px !important;
  }
}
</style>