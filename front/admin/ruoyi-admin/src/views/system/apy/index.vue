<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="法规目录" prop="library">
            <el-input v-model="queryParams.library" placeholder="请输入法规目录" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="Gid" prop="Gid">
            <el-input v-model="queryParams.Gid" placeholder="请输入Gid" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="标题" prop="Title">
            <el-input v-model="queryParams.Title" placeholder="请输入标题" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="发文案号" prop="DocumentNO">
            <el-input v-model="queryParams.DocumentNO" placeholder="请输入发文案号" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="处罚日期" prop="PunishmentDate">
            <el-date-picker clearable
              v-model="queryParams.PunishmentDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择处罚日期"
            />
          </el-form-item>
          <el-form-item label="处罚对象" prop="PunishmentObject">
            <el-input v-model="queryParams.PunishmentObject" placeholder="请输入处罚对象" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="执法级别" prop="EnforcementLevel">
            <el-input v-model="queryParams.EnforcementLevel" placeholder="请输入执法级别" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="执法地域" prop="LawRegional">
            <el-input v-model="queryParams.LawRegional" placeholder="请输入执法地域" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="执法机关" prop="LawDepartment">
            <el-input v-model="queryParams.LawDepartment" placeholder="请输入执法机关" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="处罚对象分类" prop="PunishmentTarget">
            <el-input v-model="queryParams.PunishmentTarget" placeholder="请输入处罚对象分类" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="处罚依据" prop="AccordingLaw">
            <el-input v-model="queryParams.AccordingLaw" placeholder="请输入处罚依据" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="主题分类" prop="Category">
            <el-input v-model="queryParams.Category" placeholder="请输入主题分类" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="正文" prop="AllText">
            <el-input v-model="queryParams.AllText" placeholder="请输入正文" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="是否含有罚款" prop="hasFk">
            <el-input v-model="queryParams.hasFk" placeholder="请输入是否含有罚款" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="AccordingLaw法条个数" prop="AccordingLawNum">
            <el-input v-model="queryParams.AccordingLawNum" placeholder="请输入AccordingLaw法条个数" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="AllText字符个数" prop="AllTextCharCount">
            <el-input v-model="queryParams.AllTextCharCount" placeholder="请输入AllText字符个数" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="暂扣或者吊销许可证、暂扣或者吊销执照" prop="licenseSuspensionOrRevocation">
            <el-input v-model="queryParams.licenseSuspensionOrRevocation" placeholder="请输入暂扣或者吊销许可证、暂扣或者吊销执照" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="责令停产停业" prop="orderToCeaseOperations">
            <el-input v-model="queryParams.orderToCeaseOperations" placeholder="请输入责令停产停业" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="没收违法所得、没收非法财物" prop="confiscationOfIllegalGains">
            <el-input v-model="queryParams.confiscationOfIllegalGains" placeholder="请输入没收违法所得、没收非法财物" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="罚款" prop="fine">
            <el-input v-model="queryParams.fine" placeholder="请输入罚款" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="警告" prop="warning">
            <el-input v-model="queryParams.warning" placeholder="请输入警告" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="行政拘留" prop="administrativeDetention">
            <el-input v-model="queryParams.administrativeDetention" placeholder="请输入行政拘留" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="其他" prop="otherPunishments">
            <el-input v-model="queryParams.otherPunishments" placeholder="请输入其他" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="公共文化与传媒" prop="publicCultureAndMedia">
            <el-input v-model="queryParams.publicCultureAndMedia" placeholder="请输入公共文化与传媒" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="其他" prop="otherCategories">
            <el-input v-model="queryParams.otherCategories" placeholder="请输入其他" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="外汇" prop="foreignExchange">
            <el-input v-model="queryParams.foreignExchange" placeholder="请输入外汇" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="土地城建" prop="landAndUrbanConstruction">
            <el-input v-model="queryParams.landAndUrbanConstruction" placeholder="请输入土地城建" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="招标采购" prop="biddingAndProcurement">
            <el-input v-model="queryParams.biddingAndProcurement" placeholder="请输入招标采购" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="安全监管" prop="safetyRegulation">
            <el-input v-model="queryParams.safetyRegulation" placeholder="请输入安全监管" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="体育" prop="sports">
            <el-input v-model="queryParams.sports" placeholder="请输入体育" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="税务" prop="taxation">
            <el-input v-model="queryParams.taxation" placeholder="请输入税务" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="银行" prop="banking">
            <el-input v-model="queryParams.banking" placeholder="请输入银行" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="环保" prop="environmentalProtection">
            <el-input v-model="queryParams.environmentalProtection" placeholder="请输入环保" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="水利" prop="waterResources">
            <el-input v-model="queryParams.waterResources" placeholder="请输入水利" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="海关" prop="customs">
            <el-input v-model="queryParams.customs" placeholder="请输入海关" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="商标" prop="trademark">
            <el-input v-model="queryParams.trademark" placeholder="请输入商标" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="保险" prop="insurance">
            <el-input v-model="queryParams.insurance" placeholder="请输入保险" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="审计" prop="audit">
            <el-input v-model="queryParams.audit" placeholder="请输入审计" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="交通运输" prop="transportation">
            <el-input v-model="queryParams.transportation" placeholder="请输入交通运输" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="金融" prop="finance">
            <el-input v-model="queryParams.finance" placeholder="请输入金融" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="教育" prop="education">
            <el-input v-model="queryParams.education" placeholder="请输入教育" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="民政与劳动社会保障" prop="civilAffairsAndSocialSecurity">
            <el-input v-model="queryParams.civilAffairsAndSocialSecurity" placeholder="请输入民政与劳动社会保障" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="能源与电力" prop="energyAndPower">
            <el-input v-model="queryParams.energyAndPower" placeholder="请输入能源与电力" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="专利" prop="patent">
            <el-input v-model="queryParams.patent" placeholder="请输入专利" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="食品 药品 医疗" prop="foodDrugMedical">
            <el-input v-model="queryParams.foodDrugMedical" placeholder="请输入食品 药品 医疗" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="司法" prop="judiciary">
            <el-input v-model="queryParams.judiciary" placeholder="请输入司法" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="著作权" prop="copyright">
            <el-input v-model="queryParams.copyright" placeholder="请输入著作权" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="工信" prop="industryAndInformationTechnology">
            <el-input v-model="queryParams.industryAndInformationTechnology" placeholder="请输入工信" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="证券期货" prop="securitiesAndFutures">
            <el-input v-model="queryParams.securitiesAndFutures" placeholder="请输入证券期货" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="财税" prop="financeAndTaxation">
            <el-input v-model="queryParams.financeAndTaxation" placeholder="请输入财税" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="文化和旅游" prop="cultureAndTourism">
            <el-input v-model="queryParams.cultureAndTourism" placeholder="请输入文化和旅游" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="互联网" prop="internet">
            <el-input v-model="queryParams.internet" placeholder="请输入互联网" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="农林渔牧" prop="agricultureForestryFishing">
            <el-input v-model="queryParams.agricultureForestryFishing" placeholder="请输入农林渔牧" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="知识产权" prop="intellectualProperty">
            <el-input v-model="queryParams.intellectualProperty" placeholder="请输入知识产权" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="市场监管" prop="marketRegulation">
            <el-input v-model="queryParams.marketRegulation" placeholder="请输入市场监管" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="公安" prop="publicSecurity">
            <el-input v-model="queryParams.publicSecurity" placeholder="请输入公安" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="会计" prop="accounting">
            <el-input v-model="queryParams.accounting" placeholder="请输入会计" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="疫情防控" prop="epidemicPreventionAndControl">
            <el-input v-model="queryParams.epidemicPreventionAndControl" placeholder="请输入疫情防控" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="省份" prop="province">
            <el-input v-model="queryParams.province" placeholder="请输入省份" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="城市" prop="city">
            <el-input v-model="queryParams.city" placeholder="请输入城市" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="区县" prop="county">
            <el-input v-model="queryParams.county" placeholder="请输入区县" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </transition>

    <el-card shadow="never">
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:apy:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:apy:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['system:apy:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['system:apy:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="apyList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="" align="center" prop="id" v-if="true" />
        <el-table-column label="法规目录" align="center" prop="library" />
        <el-table-column label="Gid" align="center" prop="Gid" />
        <el-table-column label="标题" align="center" prop="Title" />
        <el-table-column label="发文案号" align="center" prop="DocumentNO" />
        <el-table-column label="处罚日期" align="center" prop="PunishmentDate" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.PunishmentDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="处罚对象" align="center" prop="PunishmentObject" />
        <el-table-column label="执法级别" align="center" prop="EnforcementLevel" />
        <el-table-column label="执法地域" align="center" prop="LawRegional" />
        <el-table-column label="执法机关" align="center" prop="LawDepartment" />
        <el-table-column label="处罚对象分类" align="center" prop="PunishmentTarget" />
        <el-table-column label="处罚种类" align="center" prop="PunishmentType" />
        <el-table-column label="处罚依据" align="center" prop="AccordingLaw" />
        <el-table-column label="主题分类" align="center" prop="Category" />
        <el-table-column label="正文" align="center" prop="AllText" />
        <el-table-column label="是否含有罚款" align="center" prop="hasFk" />
        <el-table-column label="罚款金额(元)" align="center" prop="fkje" />
        <el-table-column label="AccordingLaw字符个数" align="center" prop="AccordingLawCharCount" />
        <el-table-column label="AccordingLaw法条个数" align="center" prop="AccordingLawNum" />
        <el-table-column label="AllText字符个数" align="center" prop="AllTextCharCount" />
        <el-table-column label="暂扣或者吊销许可证、暂扣或者吊销执照" align="center" prop="licenseSuspensionOrRevocation" />
        <el-table-column label="责令停产停业" align="center" prop="orderToCeaseOperations" />
        <el-table-column label="没收违法所得、没收非法财物" align="center" prop="confiscationOfIllegalGains" />
        <el-table-column label="罚款" align="center" prop="fine" />
        <el-table-column label="警告" align="center" prop="warning" />
        <el-table-column label="行政拘留" align="center" prop="administrativeDetention" />
        <el-table-column label="其他" align="center" prop="otherPunishments" />
        <el-table-column label="公共文化与传媒" align="center" prop="publicCultureAndMedia" />
        <el-table-column label="其他" align="center" prop="otherCategories" />
        <el-table-column label="外汇" align="center" prop="foreignExchange" />
        <el-table-column label="土地城建" align="center" prop="landAndUrbanConstruction" />
        <el-table-column label="招标采购" align="center" prop="biddingAndProcurement" />
        <el-table-column label="安全监管" align="center" prop="safetyRegulation" />
        <el-table-column label="体育" align="center" prop="sports" />
        <el-table-column label="税务" align="center" prop="taxation" />
        <el-table-column label="银行" align="center" prop="banking" />
        <el-table-column label="环保" align="center" prop="environmentalProtection" />
        <el-table-column label="水利" align="center" prop="waterResources" />
        <el-table-column label="海关" align="center" prop="customs" />
        <el-table-column label="商标" align="center" prop="trademark" />
        <el-table-column label="保险" align="center" prop="insurance" />
        <el-table-column label="审计" align="center" prop="audit" />
        <el-table-column label="交通运输" align="center" prop="transportation" />
        <el-table-column label="金融" align="center" prop="finance" />
        <el-table-column label="教育" align="center" prop="education" />
        <el-table-column label="民政与劳动社会保障" align="center" prop="civilAffairsAndSocialSecurity" />
        <el-table-column label="能源与电力" align="center" prop="energyAndPower" />
        <el-table-column label="专利" align="center" prop="patent" />
        <el-table-column label="食品 药品 医疗" align="center" prop="foodDrugMedical" />
        <el-table-column label="司法" align="center" prop="judiciary" />
        <el-table-column label="著作权" align="center" prop="copyright" />
        <el-table-column label="工信" align="center" prop="industryAndInformationTechnology" />
        <el-table-column label="证券期货" align="center" prop="securitiesAndFutures" />
        <el-table-column label="财税" align="center" prop="financeAndTaxation" />
        <el-table-column label="文化和旅游" align="center" prop="cultureAndTourism" />
        <el-table-column label="互联网" align="center" prop="internet" />
        <el-table-column label="农林渔牧" align="center" prop="agricultureForestryFishing" />
        <el-table-column label="知识产权" align="center" prop="intellectualProperty" />
        <el-table-column label="市场监管" align="center" prop="marketRegulation" />
        <el-table-column label="公安" align="center" prop="publicSecurity" />
        <el-table-column label="会计" align="center" prop="accounting" />
        <el-table-column label="疫情防控" align="center" prop="epidemicPreventionAndControl" />
        <el-table-column label="省份" align="center" prop="province" />
        <el-table-column label="城市" align="center" prop="city" />
        <el-table-column label="区县" align="center" prop="county" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:apy:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:apy:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination
          v-show="total>0"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
      />
    </el-card>
    <!-- 添加或修改案例库对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="apyFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="法规目录" prop="library">
          <el-input v-model="form.library" placeholder="请输入法规目录" />
        </el-form-item>
        <el-form-item label="Gid" prop="Gid">
          <el-input v-model="form.Gid" placeholder="请输入Gid" />
        </el-form-item>
        <el-form-item label="标题" prop="Title">
            <el-input v-model="form.Title" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="发文案号" prop="DocumentNO">
          <el-input v-model="form.DocumentNO" placeholder="请输入发文案号" />
        </el-form-item>
        <el-form-item label="处罚日期" prop="PunishmentDate">
          <el-date-picker clearable
            v-model="form.PunishmentDate"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择处罚日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="处罚对象" prop="PunishmentObject">
            <el-input v-model="form.PunishmentObject" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="执法级别" prop="EnforcementLevel">
          <el-input v-model="form.EnforcementLevel" placeholder="请输入执法级别" />
        </el-form-item>
        <el-form-item label="执法地域" prop="LawRegional">
          <el-input v-model="form.LawRegional" placeholder="请输入执法地域" />
        </el-form-item>
        <el-form-item label="执法机关" prop="LawDepartment">
          <el-input v-model="form.LawDepartment" placeholder="请输入执法机关" />
        </el-form-item>
        <el-form-item label="处罚对象分类" prop="PunishmentTarget">
          <el-input v-model="form.PunishmentTarget" placeholder="请输入处罚对象分类" />
        </el-form-item>
        <el-form-item label="处罚依据" prop="AccordingLaw">
            <el-input v-model="form.AccordingLaw" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="主题分类" prop="Category">
          <el-input v-model="form.Category" placeholder="请输入主题分类" />
        </el-form-item>
        <el-form-item label="正文" prop="AllText">
            <el-input v-model="form.AllText" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="是否含有罚款" prop="hasFk">
          <el-input v-model="form.hasFk" placeholder="请输入是否含有罚款" />
        </el-form-item>
        <el-form-item label="罚款金额(元)" prop="fkje">
          <el-input v-model="form.fkje" placeholder="请输入罚款金额(元)" />
        </el-form-item>
        <el-form-item label="AccordingLaw字符个数" prop="AccordingLawCharCount">
          <el-input v-model="form.AccordingLawCharCount" placeholder="请输入AccordingLaw字符个数" />
        </el-form-item>
        <el-form-item label="AccordingLaw法条个数" prop="AccordingLawNum">
          <el-input v-model="form.AccordingLawNum" placeholder="请输入AccordingLaw法条个数" />
        </el-form-item>
        <el-form-item label="AllText字符个数" prop="AllTextCharCount">
          <el-input v-model="form.AllTextCharCount" placeholder="请输入AllText字符个数" />
        </el-form-item>
        <el-form-item label="暂扣或者吊销许可证、暂扣或者吊销执照" prop="licenseSuspensionOrRevocation">
          <el-input v-model="form.licenseSuspensionOrRevocation" placeholder="请输入暂扣或者吊销许可证、暂扣或者吊销执照" />
        </el-form-item>
        <el-form-item label="责令停产停业" prop="orderToCeaseOperations">
          <el-input v-model="form.orderToCeaseOperations" placeholder="请输入责令停产停业" />
        </el-form-item>
        <el-form-item label="没收违法所得、没收非法财物" prop="confiscationOfIllegalGains">
          <el-input v-model="form.confiscationOfIllegalGains" placeholder="请输入没收违法所得、没收非法财物" />
        </el-form-item>
        <el-form-item label="罚款" prop="fine">
          <el-input v-model="form.fine" placeholder="请输入罚款" />
        </el-form-item>
        <el-form-item label="警告" prop="warning">
          <el-input v-model="form.warning" placeholder="请输入警告" />
        </el-form-item>
        <el-form-item label="行政拘留" prop="administrativeDetention">
          <el-input v-model="form.administrativeDetention" placeholder="请输入行政拘留" />
        </el-form-item>
        <el-form-item label="其他" prop="otherPunishments">
          <el-input v-model="form.otherPunishments" placeholder="请输入其他" />
        </el-form-item>
        <el-form-item label="公共文化与传媒" prop="publicCultureAndMedia">
          <el-input v-model="form.publicCultureAndMedia" placeholder="请输入公共文化与传媒" />
        </el-form-item>
        <el-form-item label="其他" prop="otherCategories">
          <el-input v-model="form.otherCategories" placeholder="请输入其他" />
        </el-form-item>
        <el-form-item label="外汇" prop="foreignExchange">
          <el-input v-model="form.foreignExchange" placeholder="请输入外汇" />
        </el-form-item>
        <el-form-item label="土地城建" prop="landAndUrbanConstruction">
          <el-input v-model="form.landAndUrbanConstruction" placeholder="请输入土地城建" />
        </el-form-item>
        <el-form-item label="招标采购" prop="biddingAndProcurement">
          <el-input v-model="form.biddingAndProcurement" placeholder="请输入招标采购" />
        </el-form-item>
        <el-form-item label="安全监管" prop="safetyRegulation">
          <el-input v-model="form.safetyRegulation" placeholder="请输入安全监管" />
        </el-form-item>
        <el-form-item label="体育" prop="sports">
          <el-input v-model="form.sports" placeholder="请输入体育" />
        </el-form-item>
        <el-form-item label="税务" prop="taxation">
          <el-input v-model="form.taxation" placeholder="请输入税务" />
        </el-form-item>
        <el-form-item label="银行" prop="banking">
          <el-input v-model="form.banking" placeholder="请输入银行" />
        </el-form-item>
        <el-form-item label="环保" prop="environmentalProtection">
          <el-input v-model="form.environmentalProtection" placeholder="请输入环保" />
        </el-form-item>
        <el-form-item label="水利" prop="waterResources">
          <el-input v-model="form.waterResources" placeholder="请输入水利" />
        </el-form-item>
        <el-form-item label="海关" prop="customs">
          <el-input v-model="form.customs" placeholder="请输入海关" />
        </el-form-item>
        <el-form-item label="商标" prop="trademark">
          <el-input v-model="form.trademark" placeholder="请输入商标" />
        </el-form-item>
        <el-form-item label="保险" prop="insurance">
          <el-input v-model="form.insurance" placeholder="请输入保险" />
        </el-form-item>
        <el-form-item label="审计" prop="audit">
          <el-input v-model="form.audit" placeholder="请输入审计" />
        </el-form-item>
        <el-form-item label="交通运输" prop="transportation">
          <el-input v-model="form.transportation" placeholder="请输入交通运输" />
        </el-form-item>
        <el-form-item label="金融" prop="finance">
          <el-input v-model="form.finance" placeholder="请输入金融" />
        </el-form-item>
        <el-form-item label="教育" prop="education">
          <el-input v-model="form.education" placeholder="请输入教育" />
        </el-form-item>
        <el-form-item label="民政与劳动社会保障" prop="civilAffairsAndSocialSecurity">
          <el-input v-model="form.civilAffairsAndSocialSecurity" placeholder="请输入民政与劳动社会保障" />
        </el-form-item>
        <el-form-item label="能源与电力" prop="energyAndPower">
          <el-input v-model="form.energyAndPower" placeholder="请输入能源与电力" />
        </el-form-item>
        <el-form-item label="专利" prop="patent">
          <el-input v-model="form.patent" placeholder="请输入专利" />
        </el-form-item>
        <el-form-item label="食品 药品 医疗" prop="foodDrugMedical">
          <el-input v-model="form.foodDrugMedical" placeholder="请输入食品 药品 医疗" />
        </el-form-item>
        <el-form-item label="司法" prop="judiciary">
          <el-input v-model="form.judiciary" placeholder="请输入司法" />
        </el-form-item>
        <el-form-item label="著作权" prop="copyright">
          <el-input v-model="form.copyright" placeholder="请输入著作权" />
        </el-form-item>
        <el-form-item label="工信" prop="industryAndInformationTechnology">
          <el-input v-model="form.industryAndInformationTechnology" placeholder="请输入工信" />
        </el-form-item>
        <el-form-item label="证券期货" prop="securitiesAndFutures">
          <el-input v-model="form.securitiesAndFutures" placeholder="请输入证券期货" />
        </el-form-item>
        <el-form-item label="财税" prop="financeAndTaxation">
          <el-input v-model="form.financeAndTaxation" placeholder="请输入财税" />
        </el-form-item>
        <el-form-item label="文化和旅游" prop="cultureAndTourism">
          <el-input v-model="form.cultureAndTourism" placeholder="请输入文化和旅游" />
        </el-form-item>
        <el-form-item label="互联网" prop="internet">
          <el-input v-model="form.internet" placeholder="请输入互联网" />
        </el-form-item>
        <el-form-item label="农林渔牧" prop="agricultureForestryFishing">
          <el-input v-model="form.agricultureForestryFishing" placeholder="请输入农林渔牧" />
        </el-form-item>
        <el-form-item label="知识产权" prop="intellectualProperty">
          <el-input v-model="form.intellectualProperty" placeholder="请输入知识产权" />
        </el-form-item>
        <el-form-item label="市场监管" prop="marketRegulation">
          <el-input v-model="form.marketRegulation" placeholder="请输入市场监管" />
        </el-form-item>
        <el-form-item label="公安" prop="publicSecurity">
          <el-input v-model="form.publicSecurity" placeholder="请输入公安" />
        </el-form-item>
        <el-form-item label="会计" prop="accounting">
          <el-input v-model="form.accounting" placeholder="请输入会计" />
        </el-form-item>
        <el-form-item label="疫情防控" prop="epidemicPreventionAndControl">
          <el-input v-model="form.epidemicPreventionAndControl" placeholder="请输入疫情防控" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="form.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区县" prop="county">
          <el-input v-model="form.county" placeholder="请输入区县" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Apy" lang="ts">
import { listApy, getApy, delApy, addApy, updateApy } from '@/api/system/apy';
import { ApyVO, ApyQuery, ApyForm } from '@/api/system/apy/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const apyList = ref<ApyVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const apyFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: ApyForm = {
  id: undefined,
  library: undefined,
  Gid: undefined,
  Title: undefined,
  DocumentNO: undefined,
  PunishmentDate: undefined,
  PunishmentObject: undefined,
  EnforcementLevel: undefined,
  LawRegional: undefined,
  LawDepartment: undefined,
  PunishmentTarget: undefined,
  PunishmentType: undefined,
  AccordingLaw: undefined,
  Category: undefined,
  AllText: undefined,
  hasFk: undefined,
  fkje: undefined,
  AccordingLawCharCount: undefined,
  AccordingLawNum: undefined,
  AllTextCharCount: undefined,
  licenseSuspensionOrRevocation: undefined,
  orderToCeaseOperations: undefined,
  confiscationOfIllegalGains: undefined,
  fine: undefined,
  warning: undefined,
  administrativeDetention: undefined,
  otherPunishments: undefined,
  publicCultureAndMedia: undefined,
  otherCategories: undefined,
  foreignExchange: undefined,
  landAndUrbanConstruction: undefined,
  biddingAndProcurement: undefined,
  safetyRegulation: undefined,
  sports: undefined,
  taxation: undefined,
  banking: undefined,
  environmentalProtection: undefined,
  waterResources: undefined,
  customs: undefined,
  trademark: undefined,
  insurance: undefined,
  audit: undefined,
  transportation: undefined,
  finance: undefined,
  education: undefined,
  civilAffairsAndSocialSecurity: undefined,
  energyAndPower: undefined,
  patent: undefined,
  foodDrugMedical: undefined,
  judiciary: undefined,
  copyright: undefined,
  industryAndInformationTechnology: undefined,
  securitiesAndFutures: undefined,
  financeAndTaxation: undefined,
  cultureAndTourism: undefined,
  internet: undefined,
  agricultureForestryFishing: undefined,
  intellectualProperty: undefined,
  marketRegulation: undefined,
  publicSecurity: undefined,
  accounting: undefined,
  epidemicPreventionAndControl: undefined,
  province: undefined,
  city: undefined,
  county: undefined
}
const data = reactive<PageData<ApyForm, ApyQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    library: undefined,
    Gid: undefined,
    Title: undefined,
    DocumentNO: undefined,
    PunishmentDate: undefined,
    PunishmentObject: undefined,
    EnforcementLevel: undefined,
    LawRegional: undefined,
    LawDepartment: undefined,
    PunishmentTarget: undefined,
    PunishmentType: undefined,
    AccordingLaw: undefined,
    Category: undefined,
    AllText: undefined,
    hasFk: undefined,
    AccordingLawNum: undefined,
    AllTextCharCount: undefined,
    licenseSuspensionOrRevocation: undefined,
    orderToCeaseOperations: undefined,
    confiscationOfIllegalGains: undefined,
    fine: undefined,
    warning: undefined,
    administrativeDetention: undefined,
    otherPunishments: undefined,
    publicCultureAndMedia: undefined,
    otherCategories: undefined,
    foreignExchange: undefined,
    landAndUrbanConstruction: undefined,
    biddingAndProcurement: undefined,
    safetyRegulation: undefined,
    sports: undefined,
    taxation: undefined,
    banking: undefined,
    environmentalProtection: undefined,
    waterResources: undefined,
    customs: undefined,
    trademark: undefined,
    insurance: undefined,
    audit: undefined,
    transportation: undefined,
    finance: undefined,
    education: undefined,
    civilAffairsAndSocialSecurity: undefined,
    energyAndPower: undefined,
    patent: undefined,
    foodDrugMedical: undefined,
    judiciary: undefined,
    copyright: undefined,
    industryAndInformationTechnology: undefined,
    securitiesAndFutures: undefined,
    financeAndTaxation: undefined,
    cultureAndTourism: undefined,
    internet: undefined,
    agricultureForestryFishing: undefined,
    intellectualProperty: undefined,
    marketRegulation: undefined,
    publicSecurity: undefined,
    accounting: undefined,
    epidemicPreventionAndControl: undefined,
    province: undefined,
    city: undefined,
    county: undefined
  },
  rules: {
    id: [
      { required: true, message: "不能为空", trigger: "blur" }
    ],
    library: [
      { required: true, message: "法规目录不能为空", trigger: "blur" }
    ],
    Gid: [
      { required: true, message: "Gid不能为空", trigger: "blur" }
    ],
    Title: [
      { required: true, message: "标题不能为空", trigger: "blur" }
    ],
    DocumentNO: [
      { required: true, message: "发文案号不能为空", trigger: "blur" }
    ],
    PunishmentObject: [
      { required: true, message: "处罚对象不能为空", trigger: "blur" }
    ],
    EnforcementLevel: [
      { required: true, message: "执法级别不能为空", trigger: "blur" }
    ],
    LawRegional: [
      { required: true, message: "执法地域不能为空", trigger: "blur" }
    ],
    LawDepartment: [
      { required: true, message: "执法机关不能为空", trigger: "blur" }
    ],
    PunishmentTarget: [
      { required: true, message: "处罚对象分类不能为空", trigger: "blur" }
    ],
    PunishmentType: [
      { required: true, message: "处罚种类不能为空", trigger: "change" }
    ],
    AccordingLaw: [
      { required: true, message: "处罚依据不能为空", trigger: "blur" }
    ],
    Category: [
      { required: true, message: "主题分类不能为空", trigger: "blur" }
    ],
    AllText: [
      { required: true, message: "正文不能为空", trigger: "blur" }
    ],
    hasFk: [
      { required: true, message: "是否含有罚款不能为空", trigger: "blur" }
    ],
    fkje: [
      { required: true, message: "罚款金额(元)不能为空", trigger: "blur" }
    ],
    AccordingLawCharCount: [
      { required: true, message: "AccordingLaw字符个数不能为空", trigger: "blur" }
    ],
    AccordingLawNum: [
      { required: true, message: "AccordingLaw法条个数不能为空", trigger: "blur" }
    ],
    AllTextCharCount: [
      { required: true, message: "AllText字符个数不能为空", trigger: "blur" }
    ],
    licenseSuspensionOrRevocation: [
      { required: true, message: "暂扣或者吊销许可证、暂扣或者吊销执照不能为空", trigger: "blur" }
    ],
    orderToCeaseOperations: [
      { required: true, message: "责令停产停业不能为空", trigger: "blur" }
    ],
    confiscationOfIllegalGains: [
      { required: true, message: "没收违法所得、没收非法财物不能为空", trigger: "blur" }
    ],
    fine: [
      { required: true, message: "罚款不能为空", trigger: "blur" }
    ],
    warning: [
      { required: true, message: "警告不能为空", trigger: "blur" }
    ],
    administrativeDetention: [
      { required: true, message: "行政拘留不能为空", trigger: "blur" }
    ],
    otherPunishments: [
      { required: true, message: "其他不能为空", trigger: "blur" }
    ],
    publicCultureAndMedia: [
      { required: true, message: "公共文化与传媒不能为空", trigger: "blur" }
    ],
    otherCategories: [
      { required: true, message: "其他不能为空", trigger: "blur" }
    ],
    foreignExchange: [
      { required: true, message: "外汇不能为空", trigger: "blur" }
    ],
    landAndUrbanConstruction: [
      { required: true, message: "土地城建不能为空", trigger: "blur" }
    ],
    biddingAndProcurement: [
      { required: true, message: "招标采购不能为空", trigger: "blur" }
    ],
    safetyRegulation: [
      { required: true, message: "安全监管不能为空", trigger: "blur" }
    ],
    sports: [
      { required: true, message: "体育不能为空", trigger: "blur" }
    ],
    taxation: [
      { required: true, message: "税务不能为空", trigger: "blur" }
    ],
    banking: [
      { required: true, message: "银行不能为空", trigger: "blur" }
    ],
    environmentalProtection: [
      { required: true, message: "环保不能为空", trigger: "blur" }
    ],
    waterResources: [
      { required: true, message: "水利不能为空", trigger: "blur" }
    ],
    customs: [
      { required: true, message: "海关不能为空", trigger: "blur" }
    ],
    trademark: [
      { required: true, message: "商标不能为空", trigger: "blur" }
    ],
    insurance: [
      { required: true, message: "保险不能为空", trigger: "blur" }
    ],
    audit: [
      { required: true, message: "审计不能为空", trigger: "blur" }
    ],
    transportation: [
      { required: true, message: "交通运输不能为空", trigger: "blur" }
    ],
    finance: [
      { required: true, message: "金融不能为空", trigger: "blur" }
    ],
    education: [
      { required: true, message: "教育不能为空", trigger: "blur" }
    ],
    civilAffairsAndSocialSecurity: [
      { required: true, message: "民政与劳动社会保障不能为空", trigger: "blur" }
    ],
    energyAndPower: [
      { required: true, message: "能源与电力不能为空", trigger: "blur" }
    ],
    patent: [
      { required: true, message: "专利不能为空", trigger: "blur" }
    ],
    foodDrugMedical: [
      { required: true, message: "食品 药品 医疗不能为空", trigger: "blur" }
    ],
    judiciary: [
      { required: true, message: "司法不能为空", trigger: "blur" }
    ],
    copyright: [
      { required: true, message: "著作权不能为空", trigger: "blur" }
    ],
    industryAndInformationTechnology: [
      { required: true, message: "工信不能为空", trigger: "blur" }
    ],
    securitiesAndFutures: [
      { required: true, message: "证券期货不能为空", trigger: "blur" }
    ],
    financeAndTaxation: [
      { required: true, message: "财税不能为空", trigger: "blur" }
    ],
    cultureAndTourism: [
      { required: true, message: "文化和旅游不能为空", trigger: "blur" }
    ],
    internet: [
      { required: true, message: "互联网不能为空", trigger: "blur" }
    ],
    agricultureForestryFishing: [
      { required: true, message: "农林渔牧不能为空", trigger: "blur" }
    ],
    intellectualProperty: [
      { required: true, message: "知识产权不能为空", trigger: "blur" }
    ],
    marketRegulation: [
      { required: true, message: "市场监管不能为空", trigger: "blur" }
    ],
    publicSecurity: [
      { required: true, message: "公安不能为空", trigger: "blur" }
    ],
    accounting: [
      { required: true, message: "会计不能为空", trigger: "blur" }
    ],
    epidemicPreventionAndControl: [
      { required: true, message: "疫情防控不能为空", trigger: "blur" }
    ],
    province: [
      { required: true, message: "省份不能为空", trigger: "blur" }
    ],
    city: [
      { required: true, message: "城市不能为空", trigger: "blur" }
    ],
    county: [
      { required: true, message: "区县不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询案例库列表 */
const getList = async () => {
  loading.value = true;
  const res = await listApy(queryParams.value);
  apyList.value = res.rows;
  total.value = res.total;
  loading.value = false;
}

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
}

/** 表单重置 */
const reset = () => {
  form.value = {...initFormData};
  apyFormRef.value.resetFields();
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: ApyVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加案例库";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: ApyVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改案例库";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getApy(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  apyFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateApy(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addApy(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: ApyVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除案例库编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delApy(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('system/apy/export', {
    ...queryParams.value
  }, `apy_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
