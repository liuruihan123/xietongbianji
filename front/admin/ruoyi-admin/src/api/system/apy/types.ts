export interface ApyVO {
  /**
   * 
   */
  id: string | number;

  /**
   * 法规目录
   */
  library: string;

  /**
   * Gid
   */
  Gid: string | number;

  /**
   * 标题
   */
  Title: string;

  /**
   * 发文案号
   */
  DocumentNO: string;

  /**
   * 处罚日期
   */
  PunishmentDate: string;

  /**
   * 处罚对象
   */
  PunishmentObject: string;

  /**
   * 执法级别
   */
  EnforcementLevel: string;

  /**
   * 执法地域
   */
  LawRegional: string;

  /**
   * 执法机关
   */
  LawDepartment: string;

  /**
   * 处罚对象分类
   */
  PunishmentTarget: string;

  /**
   * 处罚种类
   */
  PunishmentType: string;

  /**
   * 处罚依据
   */
  AccordingLaw: string;

  /**
   * 主题分类
   */
  Category: string;

  /**
   * 正文
   */
  AllText: string;

  /**
   * 是否含有罚款
   */
  hasFk: number;

  /**
   * 罚款金额(元)
   */
  fkje: number;

  /**
   * AccordingLaw字符个数
   */
  AccordingLawCharCount: number;

  /**
   * AccordingLaw法条个数
   */
  AccordingLawNum: number;

  /**
   * AllText字符个数
   */
  AllTextCharCount: number;

  /**
   * 暂扣或者吊销许可证、暂扣或者吊销执照
   */
  licenseSuspensionOrRevocation: number;

  /**
   * 责令停产停业
   */
  orderToCeaseOperations: number;

  /**
   * 没收违法所得、没收非法财物
   */
  confiscationOfIllegalGains: number;

  /**
   * 罚款
   */
  fine: number;

  /**
   * 警告
   */
  warning: number;

  /**
   * 行政拘留
   */
  administrativeDetention: number;

  /**
   * 其他
   */
  otherPunishments: number;

  /**
   * 公共文化与传媒
   */
  publicCultureAndMedia: number;

  /**
   * 其他
   */
  otherCategories: number;

  /**
   * 外汇
   */
  foreignExchange: number;

  /**
   * 土地城建
   */
  landAndUrbanConstruction: number;

  /**
   * 招标采购
   */
  biddingAndProcurement: string | number;

  /**
   * 安全监管
   */
  safetyRegulation: number;

  /**
   * 体育
   */
  sports: number;

  /**
   * 税务
   */
  taxation: number;

  /**
   * 银行
   */
  banking: number;

  /**
   * 环保
   */
  environmentalProtection: number;

  /**
   * 水利
   */
  waterResources: number;

  /**
   * 海关
   */
  customs: number;

  /**
   * 商标
   */
  trademark: number;

  /**
   * 保险
   */
  insurance: number;

  /**
   * 审计
   */
  audit: number;

  /**
   * 交通运输
   */
  transportation: number;

  /**
   * 金融
   */
  finance: number;

  /**
   * 教育
   */
  education: number;

  /**
   * 民政与劳动社会保障
   */
  civilAffairsAndSocialSecurity: number;

  /**
   * 能源与电力
   */
  energyAndPower: number;

  /**
   * 专利
   */
  patent: number;

  /**
   * 食品 药品 医疗
   */
  foodDrugMedical: number;

  /**
   * 司法
   */
  judiciary: number;

  /**
   * 著作权
   */
  copyright: number;

  /**
   * 工信
   */
  industryAndInformationTechnology: number;

  /**
   * 证券期货
   */
  securitiesAndFutures: number;

  /**
   * 财税
   */
  financeAndTaxation: number;

  /**
   * 文化和旅游
   */
  cultureAndTourism: number;

  /**
   * 互联网
   */
  internet: number;

  /**
   * 农林渔牧
   */
  agricultureForestryFishing: number;

  /**
   * 知识产权
   */
  intellectualProperty: number;

  /**
   * 市场监管
   */
  marketRegulation: number;

  /**
   * 公安
   */
  publicSecurity: number;

  /**
   * 会计
   */
  accounting: number;

  /**
   * 疫情防控
   */
  epidemicPreventionAndControl: string | number;

  /**
   * 省份
   */
  province: string;

  /**
   * 城市
   */
  city: string;

  /**
   * 区县
   */
  county: string;

}

export interface ApyForm extends BaseEntity {
  /**
   * 
   */
  id?: string | number;

  /**
   * 法规目录
   */
  library?: string;

  /**
   * Gid
   */
  Gid?: string | number;

  /**
   * 标题
   */
  Title?: string;

  /**
   * 发文案号
   */
  DocumentNO?: string;

  /**
   * 处罚日期
   */
  PunishmentDate?: string;

  /**
   * 处罚对象
   */
  PunishmentObject?: string;

  /**
   * 执法级别
   */
  EnforcementLevel?: string;

  /**
   * 执法地域
   */
  LawRegional?: string;

  /**
   * 执法机关
   */
  LawDepartment?: string;

  /**
   * 处罚对象分类
   */
  PunishmentTarget?: string;

  /**
   * 处罚种类
   */
  PunishmentType?: string;

  /**
   * 处罚依据
   */
  AccordingLaw?: string;

  /**
   * 主题分类
   */
  Category?: string;

  /**
   * 正文
   */
  AllText?: string;

  /**
   * 是否含有罚款
   */
  hasFk?: number;

  /**
   * 罚款金额(元)
   */
  fkje?: number;

  /**
   * AccordingLaw字符个数
   */
  AccordingLawCharCount?: number;

  /**
   * AccordingLaw法条个数
   */
  AccordingLawNum?: number;

  /**
   * AllText字符个数
   */
  AllTextCharCount?: number;

  /**
   * 暂扣或者吊销许可证、暂扣或者吊销执照
   */
  licenseSuspensionOrRevocation?: number;

  /**
   * 责令停产停业
   */
  orderToCeaseOperations?: number;

  /**
   * 没收违法所得、没收非法财物
   */
  confiscationOfIllegalGains?: number;

  /**
   * 罚款
   */
  fine?: number;

  /**
   * 警告
   */
  warning?: number;

  /**
   * 行政拘留
   */
  administrativeDetention?: number;

  /**
   * 其他
   */
  otherPunishments?: number;

  /**
   * 公共文化与传媒
   */
  publicCultureAndMedia?: number;

  /**
   * 其他
   */
  otherCategories?: number;

  /**
   * 外汇
   */
  foreignExchange?: number;

  /**
   * 土地城建
   */
  landAndUrbanConstruction?: number;

  /**
   * 招标采购
   */
  biddingAndProcurement?: string | number;

  /**
   * 安全监管
   */
  safetyRegulation?: number;

  /**
   * 体育
   */
  sports?: number;

  /**
   * 税务
   */
  taxation?: number;

  /**
   * 银行
   */
  banking?: number;

  /**
   * 环保
   */
  environmentalProtection?: number;

  /**
   * 水利
   */
  waterResources?: number;

  /**
   * 海关
   */
  customs?: number;

  /**
   * 商标
   */
  trademark?: number;

  /**
   * 保险
   */
  insurance?: number;

  /**
   * 审计
   */
  audit?: number;

  /**
   * 交通运输
   */
  transportation?: number;

  /**
   * 金融
   */
  finance?: number;

  /**
   * 教育
   */
  education?: number;

  /**
   * 民政与劳动社会保障
   */
  civilAffairsAndSocialSecurity?: number;

  /**
   * 能源与电力
   */
  energyAndPower?: number;

  /**
   * 专利
   */
  patent?: number;

  /**
   * 食品 药品 医疗
   */
  foodDrugMedical?: number;

  /**
   * 司法
   */
  judiciary?: number;

  /**
   * 著作权
   */
  copyright?: number;

  /**
   * 工信
   */
  industryAndInformationTechnology?: number;

  /**
   * 证券期货
   */
  securitiesAndFutures?: number;

  /**
   * 财税
   */
  financeAndTaxation?: number;

  /**
   * 文化和旅游
   */
  cultureAndTourism?: number;

  /**
   * 互联网
   */
  internet?: number;

  /**
   * 农林渔牧
   */
  agricultureForestryFishing?: number;

  /**
   * 知识产权
   */
  intellectualProperty?: number;

  /**
   * 市场监管
   */
  marketRegulation?: number;

  /**
   * 公安
   */
  publicSecurity?: number;

  /**
   * 会计
   */
  accounting?: number;

  /**
   * 疫情防控
   */
  epidemicPreventionAndControl?: string | number;

  /**
   * 省份
   */
  province?: string;

  /**
   * 城市
   */
  city?: string;

  /**
   * 区县
   */
  county?: string;

}

export interface ApyQuery extends PageQuery {
  /**
   * 法规目录
   */
  library?: string;

  /**
   * Gid
   */
  Gid?: string | number;

  /**
   * 标题
   */
  Title?: string;

  /**
   * 发文案号
   */
  DocumentNO?: string;

  /**
   * 处罚日期
   */
  PunishmentDate?: string;

  /**
   * 处罚对象
   */
  PunishmentObject?: string;

  /**
   * 执法级别
   */
  EnforcementLevel?: string;

  /**
   * 执法地域
   */
  LawRegional?: string;

  /**
   * 执法机关
   */
  LawDepartment?: string;

  /**
   * 处罚对象分类
   */
  PunishmentTarget?: string;

  /**
   * 处罚种类
   */
  PunishmentType?: string;

  /**
   * 处罚依据
   */
  AccordingLaw?: string;

  /**
   * 主题分类
   */
  Category?: string;

  /**
   * 正文
   */
  AllText?: string;

  /**
   * 是否含有罚款
   */
  hasFk?: number;

  /**
   * AccordingLaw法条个数
   */
  AccordingLawNum?: number;

  /**
   * AllText字符个数
   */
  AllTextCharCount?: number;

  /**
   * 暂扣或者吊销许可证、暂扣或者吊销执照
   */
  licenseSuspensionOrRevocation?: number;

  /**
   * 责令停产停业
   */
  orderToCeaseOperations?: number;

  /**
   * 没收违法所得、没收非法财物
   */
  confiscationOfIllegalGains?: number;

  /**
   * 罚款
   */
  fine?: number;

  /**
   * 警告
   */
  warning?: number;

  /**
   * 行政拘留
   */
  administrativeDetention?: number;

  /**
   * 其他
   */
  otherPunishments?: number;

  /**
   * 公共文化与传媒
   */
  publicCultureAndMedia?: number;

  /**
   * 其他
   */
  otherCategories?: number;

  /**
   * 外汇
   */
  foreignExchange?: number;

  /**
   * 土地城建
   */
  landAndUrbanConstruction?: number;

  /**
   * 招标采购
   */
  biddingAndProcurement?: string | number;

  /**
   * 安全监管
   */
  safetyRegulation?: number;

  /**
   * 体育
   */
  sports?: number;

  /**
   * 税务
   */
  taxation?: number;

  /**
   * 银行
   */
  banking?: number;

  /**
   * 环保
   */
  environmentalProtection?: number;

  /**
   * 水利
   */
  waterResources?: number;

  /**
   * 海关
   */
  customs?: number;

  /**
   * 商标
   */
  trademark?: number;

  /**
   * 保险
   */
  insurance?: number;

  /**
   * 审计
   */
  audit?: number;

  /**
   * 交通运输
   */
  transportation?: number;

  /**
   * 金融
   */
  finance?: number;

  /**
   * 教育
   */
  education?: number;

  /**
   * 民政与劳动社会保障
   */
  civilAffairsAndSocialSecurity?: number;

  /**
   * 能源与电力
   */
  energyAndPower?: number;

  /**
   * 专利
   */
  patent?: number;

  /**
   * 食品 药品 医疗
   */
  foodDrugMedical?: number;

  /**
   * 司法
   */
  judiciary?: number;

  /**
   * 著作权
   */
  copyright?: number;

  /**
   * 工信
   */
  industryAndInformationTechnology?: number;

  /**
   * 证券期货
   */
  securitiesAndFutures?: number;

  /**
   * 财税
   */
  financeAndTaxation?: number;

  /**
   * 文化和旅游
   */
  cultureAndTourism?: number;

  /**
   * 互联网
   */
  internet?: number;

  /**
   * 农林渔牧
   */
  agricultureForestryFishing?: number;

  /**
   * 知识产权
   */
  intellectualProperty?: number;

  /**
   * 市场监管
   */
  marketRegulation?: number;

  /**
   * 公安
   */
  publicSecurity?: number;

  /**
   * 会计
   */
  accounting?: number;

  /**
   * 疫情防控
   */
  epidemicPreventionAndControl?: string | number;

  /**
   * 省份
   */
  province?: string;

  /**
   * 城市
   */
  city?: string;

  /**
   * 区县
   */
  county?: string;

}
