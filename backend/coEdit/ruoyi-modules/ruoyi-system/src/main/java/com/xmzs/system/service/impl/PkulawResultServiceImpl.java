package com.xmzs.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.xmzs.common.chat.config.ChatClientConfig;
import com.xmzs.common.chat.domain.request.ChatRequest;
import com.xmzs.common.chat.entity.chat.ChatCompletion;
import com.xmzs.common.chat.entity.chat.ChatCompletionResponse;
import com.xmzs.common.chat.entity.chat.Message;
import com.xmzs.common.chat.openai.OpenAiClient;
import com.xmzs.common.chat.openai.OpenAiStreamClient;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xmzs.system.domain.bo.PkulawApyBo;
import com.xmzs.system.domain.bo.PkulawLawBo;
import com.xmzs.system.domain.vo.LawsVo;
import com.xmzs.system.domain.vo.PkulawApyVo;
import com.xmzs.system.service.IPkulawApyService;
import com.xmzs.system.service.IPkulawLawService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xmzs.system.domain.bo.PkulawResultBo;
import com.xmzs.system.domain.vo.PkulawResultVo;
import com.xmzs.system.domain.PkulawResult;
import com.xmzs.system.mapper.PkulawResultMapper;
import com.xmzs.system.service.IPkulawResultService;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 判案结果Service业务层处理
 *
 * @author zuleed
 * @date 2025-05-10
 */
@RequiredArgsConstructor
@Service

public class PkulawResultServiceImpl implements IPkulawResultService {

    private final PkulawResultMapper baseMapper;
    @Autowired(required = false)
    private ChatClientConfig chatConfig;
//    @Autowired
//    private OpenAiStreamClient openAiStreamClient;

    @Autowired(required = false)
    private OpenAiClient openAiClient;

    private final IPkulawApyService pkulawApyService;

    private final IPkulawLawService pkulawLawService;


    /**
     * 法律测试
     * @return
     */
    @Override
    public Boolean handleLawTest() {
        if (chatConfig == null) {
            System.out.println("ChatClientConfig is not available, skipping law test");
            return false;
        }
        openAiClient = chatConfig.getOpenAiClient();
        if (openAiClient == null) {
            System.out.println("OpenAiClient is not available, skipping law test");
            return false;
        }
        ChatRequest chatRequest = new ChatRequest();


        PageQuery pageQuery = new PageQuery();
        int runNumber = 1000;

        int num = 0;
        while (num<runNumber){
            pageQuery.setPageNum(num);
            pageQuery.setPageSize(100);
            TableDataInfo<PkulawApyVo>  apyys =  pkulawApyService.queryPageList(new PkulawApyBo() ,pageQuery);
             List<PkulawApyVo> apys = apyys.getRows();
            String lawCase = "";
            //遍历案例库
            loop1:for(PkulawApyVo apyVo:apys){

                if(StringUtils.isEmpty(apyVo.getAllText())){
                    continue;
                }
//                Document caseHtml =  Jsoup.parse(apyVo.getAllText());
                //提参
                List<Map<String, String>>  psMapList =    extractParameters(apyVo.getAllText());
                //需要的法律条文
                List<String> partLaws = new ArrayList<String>();

                String punishstr = "";
                if(psMapList.size()==0){
                    //todo

                }else{
                    List<String> gids = extractUniqueGids(psMapList);
                    List<LawsVo> lawsList  =  pkulawLawService.queryByGids(gids);

                    //根据法律条文参数找法律依据
//                遍历法律条文
                    psMapList.forEach(map -> {
                        String gid = map.get("gid");
                        String tiao = map.get("tiao");
                        String kuang = map.get("kuang");
                        String xiang = map.get("xiang");
                        // 构建字符串
                        StringBuilder result = new StringBuilder();

                        // 添加 tiao
                        if (tiao != null && !tiao.isEmpty()) {
                            result.append("tiao_").append(tiao);
                        }

                        // 添加 kuang
                        if (kuang != null && !kuang.isEmpty()) {
                            result.append("_kuan_").append(kuang);
                        }

                        // 添加 xiang
                        if (xiang != null && !xiang.isEmpty()) {
                            result.append("_xiang_").append(xiang);
                        }

                        // 获取最终字符串
                        String finalString = result.toString();
                        //翻译成中文法律条文
                        String cnlawString  =convertToChinese(finalString);


                        String lawContent = "";
                        String partLawContent = "";
                        loop2: for(LawsVo lawsVo:lawsList){
                            if(gid.equals(lawsVo.getGid())){
                                //获取内容
                                lawContent = lawsVo.getAllText();
                                //获取参数对应的法律条文
                                lawContent = lawContent.replaceAll("\\\\","").replaceAll("\n","");
                                partLawContent =  extractTextByAnchor(lawContent,finalString);
//                            partLaws.add( "《"+lawsVo.getTitle()+"》"+ cnlawString+"【"+partLawContent+"】");
                                partLaws.add( "《"+lawsVo.getTitle()+"》"+ partLawContent+"\n-- \n");
                                break loop2;
                            }

                        }

                    });
                }


                 try {
                      //案件详细信息
                     Map<String,String> resMap =  parseHtmlTableToString(apyVo.getAllText());
                     lawCase =   resMap.get("case");
                     punishstr =  resMap.get("punish");

                 } catch (JsonProcessingException e) {
                     throw new RuntimeException(e);
                 }
                //法规依据 partLaws
                String lawProvisions = String.join(".\n", partLaws);


                String userMsg = outputStandardTemplate(lawCase,lawProvisions);

                System.out.println("案件id：："+apyVo.getId());
                System.out.println("用户提问：："+userMsg);

                 Message sysmessage  =  new Message.Builder()
                     .role(ChatMessageRole.SYSTEM.value())
                     .content(getSystemTemplate())
                     .build();

                 Message userMessage  =  new Message.Builder()
                     .role(ChatMessageRole.USER.value())
                     .content(userMsg)
                     .build();




                 chatRequest.setModel("gpt-4.5-preview");
                 chatRequest.setSystemMessage(getSystemTemplate());
                 chatRequest.setTop_p(0.75);
                 chatRequest.setTemperature(0.1);
                 chatRequest.setContentNumber(10);
                 chatRequest.setMaxTokens(5000);


                 List<Message> msgList = new ArrayList<Message>();
                 msgList.add(sysmessage);
                 msgList.add(userMessage);

                 ChatCompletion completion = ChatCompletion
                     .builder()
                     .messages(msgList)
                     .model(chatRequest.getModel())
                     .temperature(chatRequest.getTemperature())
                     .topP(chatRequest.getTop_p())
                     .maxTokens(chatRequest.getMaxTokens())
                     .stream(false)
                     .build();

                ChatCompletionResponse chatCompletionResponse =  openAiClient.chatCompletion(completion);
                String aContent =   chatCompletionResponse.getChoices().get(0).getMessage().getContent();
                System.out.println("AI回复："+aContent);

                //插入数据库
                PkulawResultBo pkulawResultBo = new PkulawResultBo();
                pkulawResultBo.setGid(apyVo.getGid());
                pkulawResultBo.setPunishmentDate(apyVo.getPunishmentDate());
                pkulawResultBo.setLawRegional(apyVo.getLawRegional());
                pkulawResultBo.setPunishmentType(apyVo.getPunishmentType());
                pkulawResultBo.setAccordingLaw(apyVo.getAccordingLaw());
                pkulawResultBo.setReasonText(apyVo.getAllText());
                pkulawResultBo.setAijudgment(aContent);
                pkulawResultBo.setRealjudgment(punishstr);
                insertByBo(pkulawResultBo);

             }




            num++;

        }



        return null;
    }


    //提取法律条文内容
    // 提取法律条文内容
    public static String extractTextByAnchor(String html, String anchorName) {
        // 使用Jsoup解析HTML
        Document document = Jsoup.parse(html);

        // 查找指定锚点
        Element anchor = document.select("a[name=" + anchorName + "]").first();
        if (anchor != null) {
            // 获取锚点所在的父元素（tiao-wrap）
            Element tiaoWrap = anchor.closest(".tiao-wrap");
            if (tiaoWrap != null) {
                // 返回该元素下的所有文本内容
                return tiaoWrap.text().trim(); // 返回提取的文本内容
            }
        }
        return null; // 如果没有找到，返回null
    }




    // 方法：提取去重后的 gid 列表
    public static List<String> extractUniqueGids(List<Map<String, String>> psMapList) {
        return psMapList.stream()
            .map(map -> map.get("gid"))  // 提取 gid
            .filter(gid -> gid != null && !gid.isEmpty())  // 过滤掉 null 和空值
            .distinct()  // 去重
            .collect(Collectors.toList());  // 收集到 List<String>
    }

    /**
     * 法律system问答模版
     * @param question
     * @return
     */
    private static  String getSystemTemplate(){

        String prompts = "你是一个非常熟悉中国法律专业知识经验老道的法官，我将提供一些案件需要你根据我提供的处罚依据来作出相应的判罚。 ";
        return prompts;
    }


    /**
     * 法律问答模版
     * @param lawCase
     * @param lawProvisions
     * @return
     */
    private static  String outputStandardTemplate(String lawCase,String lawProvisions){
        String prompts = "";
        if(StringUtils.isNotBlank(lawProvisions)){
             prompts = "我将案件信息放在 <case></case> 标记中的内容中，将案件中 处罚依据相关法律条文放在 <laws></laws> 标记中的内容中 。希望你根据处罚依据相关法律条文 以及 案件信息对案件作出合理的判罚 :\n" +
                "处罚依据相关法律条文：\n" +
                "<laws>\n"+lawProvisions+"\n</laws> \n" +
                "回答要求：\n" +
                "- 无需任何解释，直接告诉我你对案件的判罚结果。\n" +
                "- 你做为公正无私法官，需要充分考虑<laws></laws>内法律条文作为判罚依据。\n" +
                "- 避免提及你是从 <laws></laws> 获取的知识。\n" +
                "\n" +
                "案件信息:\"\"\"<case>"+lawCase+"</case>\"\"\"";
        }else{
            prompts = "我将案件信息放在 <case></case> 标记中的内容中，请根据案件的 处罚依据以及相关情节作出合理的判罚 :" +
                "处罚依据相关法律条文：\n" +
                "回答要求：\n" +
                "- 无需任何解释，直接告诉我你对案件的判罚结果。\n" +
                "- 你做为公正无私法官，需要充分考虑<laws></laws>内法律条文作为判罚依据。\n" +
                "- 避免提及你是从 <laws></laws> 获取的知识。\n" +
                "\n" +
                "案件信息:\"\"\"<case>"+lawCase+"</case>\"\"\"";
        }


        return prompts;
    }

    /**
     * 解析html
     * @param html
     * @return
     */
    public Map<String, String> parseHtmlTable(String html) {
        Map<String, String> resultMap = new HashMap<>();

        // 解析HTML
        Document document = Jsoup.parse(html);
        Element table = document.select("table").first();

        if (table != null) {
            Elements rows = table.select("tr");
            for (Element row : rows) {
                Elements cells = row.select("td");
                if (cells.size() == 2) {
                    String key = cells.get(0).text().trim();
                    String value = cells.get(1).text().trim();
                    resultMap.put(key, value);
                }
            }
        }

        return resultMap;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * 获取表格内容 如果不是表格则获取纯文本
     * @param html
     * @return
     * @throws JsonProcessingException
     */
    public Map<String,String> parseHtmlTableToString(String html) throws JsonProcessingException {


        Map<String, String> returnMap = new HashMap<>();

        Map<String, String> resultMap = new HashMap<>();

        String tostr = "";
        // 解析HTML
        Document document = Jsoup.parse(html);
        if(ObjectUtil.isEmpty(document.select("table"))||document.select("table")==null){
            tostr =  document.text();
        }else{
            Element table = document.select("table").first();

            if (table != null) {
                Elements rows = table.select("tr");
               loop3: for (Element row : rows) {
                    Elements cells = row.select("td");
                    if (cells.size() == 2) {
                        String key = cells.get(0).text().trim();
                        if(StringUtils.isNotEmpty(key)&&key.equals("处罚结果")){
                            returnMap.put("punish",cells.get(1).text().trim());
                            continue ;
                        }
                        String value = cells.get(1).text().trim();
                        resultMap.put(key, value);
                    }
                }
            }
            tostr = objectMapper.writeValueAsString(resultMap);
        }

        returnMap.put("case",tostr);

        return returnMap;
    }


    public List<Map<String, String>> extractParameters(String html) {
        List<Map<String, String>> resultList = new ArrayList<>();
        try {
            // 解析HTML
            Document document = Jsoup.parse(html);
            // 获取所有的<a>标签
            Elements links = document.select("a[href]");

            // 改用Map来存储结果，按照基础ID+条号进行分组
            Map<String, Map<String, String>> groupedResults = new HashMap<>();

            for (Element link : links) {
                String href = link.attr("href");
                // 使用正则表达式提取参数
                Pattern pattern = Pattern.compile("SLC\\('([^']+)','([^']+)'(?:,(\\d+))?(?:,(\\d+))?(?:,(\\d+))?\\)");
                Matcher matcher = pattern.matcher(href);

                if (matcher.find()) {
                    String type = matcher.group(1); // 例如 'chl' 或 'lar'
                    String gid = matcher.group(2);
                    String tiao = matcher.groupCount() >= 3 && matcher.group(3) != null ? matcher.group(3) : "";
                    String kuang = matcher.groupCount() >= 4 && matcher.group(4) != null ? matcher.group(4) : "";
                    String xiang = matcher.groupCount() >= 5 && matcher.group(5) != null ? matcher.group(5) : "";

                    if(StringUtils.isEmpty(tiao)){
                        continue;
                    }
                    // 创建参数Map
                    Map<String, String> paramMap = new HashMap<>();
                    paramMap.put("gid", gid);
                    paramMap.put("tiao", tiao);
                    paramMap.put("kuang", kuang);
                    paramMap.put("xiang", xiang);

                    // 创建基础键，使用gid和tiao作为组合键
                    String baseKey = gid;
                    if (!tiao.isEmpty()) {
                        baseKey += "_" + tiao;
                    }

                    // 检查是否已存在相同baseKey的条目
                    if (groupedResults.containsKey(baseKey)) {
                        Map<String, String> existingMap = groupedResults.get(baseKey);
                        // 对比现有条目和新条目，决定是否覆盖
                        if (shouldOverride(existingMap, paramMap)) {
                            groupedResults.put(baseKey, paramMap);
                        }
                    } else {
                        groupedResults.put(baseKey, paramMap);
                    }
                }
            }

            // 将结果转换为List
            resultList.addAll(groupedResults.values());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * 判断新的参数Map是否应该覆盖现有的参数Map
     * 规则: 只要新的Map比旧的Map更详细(拥有更多非空字段)，就进行覆盖
     *
     * @param existingMap 现有的参数Map
     * @param newMap 新的参数Map
     * @return 如果应该用新Map覆盖旧Map，则返回true
     */
    private boolean shouldOverride(Map<String, String> existingMap, Map<String, String> newMap) {
        // 如果新Map有kuang而旧Map没有，或新Map的kuang比旧Map的更详细，则覆盖
        String existingKuang = existingMap.get("kuang");
        String newKuang = newMap.get("kuang");

        if ((existingKuang == null || existingKuang.isEmpty()) && newKuang != null && !newKuang.isEmpty()) {
            return true;
        }

        // 如果新Map有xiang而旧Map没有，则覆盖
        String existingXiang = existingMap.get("xiang");
        String newXiang = newMap.get("xiang");

        if ((existingXiang == null || existingXiang.isEmpty()) && newXiang != null && !newXiang.isEmpty()) {
            return true;
        }

        // 计算非空字段总数
        int existingNonEmptyCount = countNonEmptyFields(existingMap);
        int newNonEmptyCount = countNonEmptyFields(newMap);

        // 如果新Map有更多非空字段，则覆盖
        return newNonEmptyCount > existingNonEmptyCount;
    }

    /**
     * 计算Map中非空字段的数量
     *
     * @param map 要检查的Map
     * @return 非空字段的数量
     */
    private int countNonEmptyFields(Map<String, String> map) {
        int count = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                count++;
            }
        }
        return count;
    }



    //条文前缀翻译
    private static String convertToChinese(String input) {
        String[] parts = input.split("_");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.equals("tiao") && (i + 1) < parts.length) {
                String number = parts[i + 1]; // 获取下一个部分
                result.append("第"+convertNumberToChinese(number)).append("条");
                i++; // 跳过数字部分
            } else if (part.equals("kuan") && (i + 1) < parts.length) {
                String number = parts[i + 1]; // 获取下一个部分
                result.append(" / ").append("第"+convertNumberToChinese(number)).append("款");
                i++; // 跳过数字部分
            } else if (part.equals("xiang") && (i + 1) < parts.length) {
                String number = parts[i + 1]; // 获取下一个部分
                result.append(" / ").append("第"+convertNumberToChinese(number)).append("项");
                i++; // 跳过数字部分
            }
        }

        return result.toString();
    }


    private static String convertNumberToChinese(String number) {
        // 将数字转换为汉字
        String[] units = {"", "十", "百", "千"};
        String[] digits = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        StringBuilder chineseNumber = new StringBuilder();

        int len = number.length();
        for (int i = 0; i < len; i++) {
            int digit = Character.getNumericValue(number.charAt(i)); // 获取每个数字
            if (digit != 0) {
                chineseNumber.append(digits[digit]); // 添加数字汉字
                chineseNumber.append(units[len - i - 1]); // 添加单位
            } else if (chineseNumber.length() > 0 && chineseNumber.charAt(chineseNumber.length() - 1) != '零') {
                chineseNumber.append("零"); // 添加零
            }
        }

        // 去掉末尾的零
        if (chineseNumber.length() > 0 && chineseNumber.charAt(chineseNumber.length() - 1) == '零') {
            chineseNumber.deleteCharAt(chineseNumber.length() - 1);
        }

        return chineseNumber.toString();
    }



    /**
     * 查询判案结果
     */
    @Override
    public PkulawResultVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询判案结果列表
     */
    @Override
    public TableDataInfo<PkulawResultVo> queryPageList(PkulawResultBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PkulawResult> lqw = buildQueryWrapper(bo);
        Page<PkulawResultVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询判案结果列表
     */
    @Override
    public List<PkulawResultVo> queryList(PkulawResultBo bo) {
        LambdaQueryWrapper<PkulawResult> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<PkulawResult> buildQueryWrapper(PkulawResultBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<PkulawResult> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getGid()), PkulawResult::getGid, bo.getGid());
        lqw.between(params.get("beginPunishmentDate") != null && params.get("endPunishmentDate") != null,
            PkulawResult::getPunishmentDate ,params.get("beginPunishmentDate"), params.get("endPunishmentDate"));
        lqw.eq(StringUtils.isNotBlank(bo.getLawRegional()), PkulawResult::getLawRegional, bo.getLawRegional());
        lqw.eq(StringUtils.isNotBlank(bo.getPunishmentType()), PkulawResult::getPunishmentType, bo.getPunishmentType());
        lqw.eq(StringUtils.isNotBlank(bo.getAccordingLaw()), PkulawResult::getAccordingLaw, bo.getAccordingLaw());
        lqw.like(StringUtils.isNotBlank(bo.getReasonText()), PkulawResult::getReasonText, bo.getReasonText());
        lqw.like(StringUtils.isNotBlank(bo.getAijudgment()), PkulawResult::getAijudgment, bo.getAijudgment());
        lqw.like(StringUtils.isNotBlank(bo.getRealjudgment()), PkulawResult::getRealjudgment, bo.getRealjudgment());
        return lqw;
    }

    /**
     * 新增判案结果
     */
    @Override
    public Boolean insertByBo(PkulawResultBo bo) {
        PkulawResult add = MapstructUtils.convert(bo, PkulawResult.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }



    /**
     * 修改判案结果
     */
    @Override
    public Boolean updateByBo(PkulawResultBo bo) {
        PkulawResult update = MapstructUtils.convert(bo, PkulawResult.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(PkulawResult entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除判案结果
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }






}
