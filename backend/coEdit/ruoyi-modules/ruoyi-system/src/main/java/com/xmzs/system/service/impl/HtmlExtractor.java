package com.xmzs.system.service.impl;

import com.xmzs.common.core.utils.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlExtractor {

    public static void main(String[] args) {
        String htmlContent = "<table align=\"center\" border=\"1\" cellspacing=\"0\" style=\"border-collapse: collapse;margin-top: 15px;width:50%;\" cellpadding=\"10\" class=\"c_tab\"><tbody><tr><td width=\"353\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">行政处罚决定书文号</p></td><td width=\"460\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">深市监光处罚〔2022〕公明26号</p></td></tr><tr><td width=\"353\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">被处罚对象</p></td><td width=\"460\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">深圳市光明新区公明综合市场江锡兵鱼档</p></td></tr><tr><td width=\"353\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">统一社会信用代码</p></td><td width=\"460\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">92440300L91022485K</p></td></tr><tr><td width=\"353\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">法定代表人</p></td><td width=\"460\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">江锡兵</p></td></tr><tr><td width=\"353\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">处罚类别</p></td><td width=\"460\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">罚款;没收违法所得</p></td></tr><tr><td width=\"353\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">处罚事由</p></td><td width=\"460\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">食品违法行为</p></td></tr><tr><td width=\"353\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">处罚依据</p></td><td width=\"460\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\"><a href=\"javascript:SLC('chl','6add260f579ab541bdfb')\" class='alink' onmouseover=\"AJI('chl','6add260f579ab541bdfb')\">中华人民共和国行政处罚法</a> / 第一章总则 / 第<a href=\"javascript:SLC('chl','6add260f579ab541bdfb',5)\" class='alink' onmouseover=\"AJI('chl','6add260f579ab541bdfb',5)\">五条</a> / 第<a href=\"javascript:SLC('chl','6add260f579ab541bdfb',5,2)\" class='alink' onmouseover=\"AJI('chl','6add260f579ab541bdfb',5,2)\">二款</a>设定和实施行政处罚必须以事实为依据，与违法行为的事实、性质、情节以及社会危害程度相当。,<a href=\"javascript:SLC('chl','6add260f579ab541bdfb')\" class='alink' onmouseover=\"AJI('chl','6add260f579ab541bdfb')\">中华人民共和国行政处罚法</a> / 第一章总则 / 第<a href=\"javascript:SLC('chl','6add260f579ab541bdfb',6)\" class='alink' onmouseover=\"AJI('chl','6add260f579ab541bdfb',6)\">六条</a>实施行政处罚，纠正违法行为，应当坚持处罚与教育相结合，教育公民、法人或者其他组织自觉守法。,<a href=\"javascript:SLC('chl','6add260f579ab541bdfb')\" class='alink' onmouseover=\"AJI('chl','6add260f579ab541bdfb')\">中华人民共和国行政处罚法</a> / 第四章行政处罚的管辖和适用 / 第<a href=\"javascript:SLC('chl','6add260f579ab541bdfb',32)\" class='alink' onmouseover=\"AJI('chl','6add260f579ab541bdfb',32)\">三十二条</a> / 第<a href=\"javascript:SLC('chl','6add260f579ab541bdfb',32,1)\" class='alink' onmouseover=\"AJI('chl','6add260f579ab541bdfb',32,1)\">一款</a>当事人有下列情形之一，应当从轻或者减轻行政处罚： / （一）主动消除或者减轻违法行为危害后果的；,<a href=\"javascript:SLC('chl','ac73cbe3ed780d7fbdfb')\" class='alink' onmouseover=\"AJI('chl','ac73cbe3ed780d7fbdfb')\">中华人民共和国食品安全法</a> / 第四章食品生产经营 / 第一节一般规定 / 第<a href=\"javascript:SLC('chl','ac73cbe3ed780d7fbdfb',34)\" class='alink' onmouseover=\"AJI('chl','ac73cbe3ed780d7fbdfb',34)\">三十四条</a> / 第<a href=\"javascript:SLC('chl','ac73cbe3ed780d7fbdfb',34,1)\" class='alink' onmouseover=\"AJI('chl','ac73cbe3ed780d7fbdfb',34,1)\">一款</a>禁止生产经营下列食品、食品添加剂、食品相关产品： / （一）用非食品原料生产的食品或者添加食品添加剂以外的化学物质和其他可能危害人体健康物质的食品，或者用回收食品作为原料生产的食品；,<a href=\"javascript:SLC('chl','ac73cbe3ed780d7fbdfb')\" class='alink' onmouseover=\"AJI('chl','ac73cbe3ed780d7fbdfb')\">中华人民共和国食品安全法</a> / 第九章法律责任 / 第<a href=\"javascript:SLC('chl','ac73cbe3ed780d7fbdfb',123)\" class='alink' onmouseover=\"AJI('chl','ac73cbe3ed780d7fbdfb',123)\">一百二十三条</a> / 第<a href=\"javascript:SLC('chl','ac73cbe3ed780d7fbdfb',123,1)\" class='alink' onmouseover=\"AJI('chl','ac73cbe3ed780d7fbdfb',123,1)\">一款</a>违反本法规定，有下列情形之一，尚不构成犯罪的，由县级以上人民政府食品安全监督管理部门没收违法所得和违法生产经营的食品，并可以没收用于违法生产经营的工具、设备、原料等物品；违法生产经营的食品货值金额不足一万元的，并处十万元以上十五万元以下罚款；货值金额一万元以上的，并处货值金额十五倍以上三十倍以下罚款；情节严重的，吊销许可证，并可以由公安机关对其直接负责的主管人员和其他直接责任人员处五日以上十五日以下拘留： / （一）用非食品原料生产食品、在食品中添加食品添加剂以外的化学物质和其他可能危害人体健康的物质，或者用回收食品作为原料生产食品，或者经营上述食品；</p></td></tr><tr><td width=\"353\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">处罚结果</p></td><td width=\"460\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">罚款10000元;没收违法所得</p></td></tr><tr><td width=\"353\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">处罚决定日期</p></td><td width=\"460\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">2022-01-25</p></td></tr><tr><td width=\"353\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">处罚机关</p></td><td width=\"460\" valign=\"center\" style=\"font-size: 16px;\"><p style=\"text-align: center; font-size: 16px;\">深圳市市场监督管理局光明监管局</p></td></tr></tbody></table>\n";
        HtmlExtractor extractor = new HtmlExtractor();
        List<Map<String, String>> parameters = extractor.extractParameters(htmlContent);
        System.out.println(parameters);
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
}
