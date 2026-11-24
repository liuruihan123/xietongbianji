package com.xmzs.system.service.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmzs.system.controller.Test;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * packageName com.xmzs.system.service.impl
 *
 * @author zhuzi
 * @date 2024/8/18
 * @description 主要用于检查json格式问题以及问题所在原因
 */
public class DataJsonChecker {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final JsonFactory jsonFactory = new JsonFactory();

    public static ValidationResult validateJson(String jsonString) {
        try {
            // 尝试解析 JSON 字符串
            objectMapper.readTree(jsonString);

            // 进一步检查 JSON 的完整性
            try (JsonParser parser = jsonFactory.createParser(jsonString)) {
                JsonToken token;
                while ((token = parser.nextToken()) != null) {
                    // 遍历所有的 JSON 令牌，确保没有解析错误
                }
            }



            //再次检测是否包含换行
            String regex = "\\}\\]\\}[\\n\\r]*\\{\"messages";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(jsonString);
            while (matcher.find()) {
                String matched = matcher.group();
                if (matched.contains("\n")) {
//                    System.out.println("包含" + matched);
                } else {
//                    System.out.println("不包含 " + matched);
                    return new ValidationResult(false, "json no newline ", 1, "");
                }
            }

            //检测 关于 The last message must be from the assistant
            Map<String,String> hasEndOfAssistant   = checkLastOfASS(jsonString);

            if("yes".equals(hasEndOfAssistant.get("hasError"))){
                String errorMsg ="The last message must be from the assistant, Lines "+ hasEndOfAssistant.get("num")+" are invalid,plase check json";
                return new ValidationResult(false, errorMsg, 1, "");

            }


            return new ValidationResult(true, null, -1, null);
        } catch (JsonProcessingException e) {
            // JSON 解析失败，获取错误位置和信息
            int errorLine = e.getLocation().getLineNr();
            int errorColumn = e.getLocation().getColumnNr();
            String errorMessage = e.getOriginalMessage();
            String errorSnippet = getErrorSnippet(jsonString, errorLine, errorColumn);

            return new ValidationResult(false, errorMessage, errorLine, errorSnippet);
        } catch (IOException e) {
            // 其他 IO 错误
            return new ValidationResult(false, e.getMessage(), -1, null);
        }
    }

    public static Map<String,String> checkLastOfASS(String multipleJsons){
        // 调用方法检查多个JSON对象
        List<Integer> invalidIndexes = checkMultipleJsons(multipleJsons);

        Map<String,String> map = new HashMap<>();
        // 打印结果
        if (invalidIndexes.isEmpty()) {
            System.out.println("所有JSON对象的最后一个role都是'assistant'。");
            map.put("hasError","no");
            map.put("num","0");
        } else {
            System.out.println("以下JSON对象的最后一个role不是'assistant'：");
            map.put("hasError","yes");
            String jsonno = "";
            for (int index : invalidIndexes) {
                System.out.println("JSON对象序号: " + (index + 1));
                jsonno += (index + 1) +",";
            }
            map.put("num",jsonno);

        }
        return map;
    }

    public static List<Integer> checkMultipleJsons(String jsonStrings) {
        List<Integer> invalidIndexes = new ArrayList<>();

        // 将多个JSON对象分割为独立的JSON字符串
        String[] jsonArray = jsonStrings.split("(?<=\\})\\s*(?=\\{)");

        for (int i = 0; i < jsonArray.length; i++) {
            String jsonString = jsonArray[i];
            if (!checkLastRoleIsAssistant(jsonString)) {
                invalidIndexes.add(i); // 记录不符合条件的JSON对象的索引
            }
        }

        return invalidIndexes;
    }


    public static boolean checkLastRoleIsAssistant(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray messages = jsonObject.getJSONArray("messages");

            if (messages.length() == 0) {
                return false; // 如果数组为空，返回false
            }

            JSONObject lastMessage = messages.getJSONObject(messages.length() - 1);
            String role = lastMessage.getString("role");

            return "assistant".equals(role);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String getErrorSnippet(String jsonString, int errorLine, int errorColumn) {
        String[] lines = jsonString.split("\n");
        if (errorLine > 0 && errorLine <= lines.length) {
            String errorLineContent = lines[errorLine - 1];
            int snippetStart = Math.max(0, errorColumn - 10);
            int snippetEnd = Math.min(errorLineContent.length(), errorColumn + 10);
            return errorLineContent.substring(snippetStart, snippetEnd);
        }
        return null;
    }


    /**
     * 解析错误
     * @param result
     * @return
     */
    public static String printValidationResult(ValidationResult result) {
        String errorMsg="";
        if (result.isValid()) {
            System.out.println("JSON is valid.");
        } else {
            errorMsg = "JSON is invalid."+"Error message: " +
                result.getErrorMessage()+",Error line: " + result.getErrorLine()+
                "Error snippet: " + result.getErrorSnippet();
            System.out.println("JSON is invalid.");
            System.out.println("Error message: " + result.getErrorMessage());
            System.out.println("Error line: " + result.getErrorLine());
            System.out.println("Error snippet: " + result.getErrorSnippet());
        }
        return errorMsg;
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        private final int errorLine;
        private final String errorSnippet;

        public ValidationResult(boolean valid, String errorMessage, int errorLine, String errorSnippet) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.errorLine = errorLine;
            this.errorSnippet = errorSnippet;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public int getErrorLine() {
            return errorLine;
        }

        public String getErrorSnippet() {
            return errorSnippet;
        }
    }



    public static void main(String[] args) {

        String str ="{\"messages\": [{\"role\": \"system\", \"content\": \"You are an interviewer from a health organization, guiding a candidate through a case study on supply chain optimization for vaccination programs.\"}, {\"role\": \"assistant\", \"content\": \"Welcome! Today, we're discussing how to reduce supply chain costs while expanding the vaccination program for polio and measles. Can you start by telling me if there are any other mission goals or objectives you should be concerned with?\"}, {\"role\": \"user\", \"content\": \"The Gates Foundation has asked us to reduce its supply chain costs and expand its polio and measles vaccination program. Are there any other mission goals or objectives I should be concerned with?\"}, {\"role\": \"assistant\", \"content\": \"No, let's focus on that. Can you tell me a little about the current vaccination program?\"}, {\"role\": \"user\", \"content\": \"Can you tell me a little about the current vaccination program?\"}, {\"role\": \"assistant\", \"content\": \"Vaccines are among the most cost-effective health interventions ever developed. Immunization has led to the eradication of smallpox, a 74 percent reduction in childhood deaths from measles over the past decade, and the near-eradication of polio. However, one in five children worldwide is not fully protected with even the most basic vaccines, leading to an estimated 1.5 million children dying each year from preventable diseases. The main issues are the high cost of vaccines in the poorest countries, supply shortages, and a lack of trained health workers.\"}, {\"role\": \"user\", \"content\": \"You mentioned supply shortages. What companies manufacture these vaccines? And why are there shortages?\"}, {\"role\": \"assistant\", \"content\": \"The two manufacturers are French drugmaker Sanofi Pasteur and Serum Institute of India. Production has been about 40 percent below what was requested, leaving about 50 countries without adequate supplies. So why do you think there are shortages?\"}, {\"role\": \"user\", \"content\": \"I would like to think that the companies are having a hard time getting the ingredients. But the skeptic in me thinks they are producing less so there will always be a need. Because if they produce enough and polio is eradicated, then they are out of business.\"}, {\"role\": \"assistant\", \"content\": \"Interesting perspective, but you are way off base. Both firms are very reputable. Let's move on. How would you structure your thoughts on this issue?\"}, {\"role\": \"user\", \"content\": \"I’d like to take a minute to structure my thoughts. [The student takes about two minutes.] I’d like to break this down into several buckets – the key stakeholders, their roles and responsibilities, ways to reduce the supply chain costs, and ways to expand the vaccine program.\"}, {\"role\": \"assistant\", \"content\": \"That sounds like a solid approach. Can you elaborate on the key stakeholders?\"}, {\"role\": \"user\", \"content\": \"First, we have the key stakeholders – the Gates Foundation, maybe the World Health Organization, the 50 countries and their decision-makers, maybe other NGOs, the drug manufacturers, and the storage and transportation companies or organizations.\"}, {\"role\": \"assistant\", \"content\": \"Great! And what about the supply chain itself?\"}, {\"role\": \"user\", \"content\": \"Second, the supply chain. We need to analyze the current process and then investigate whether there are options to consolidate, automate, simplify, or even eliminate parts of the process that are either slowing things down or just redundant.\"}, {\"role\": \"assistant\", \"content\": \"Excellent. And how do you plan to expand the program?\"}, {\"role\": \"user\", \"content\": \"Third, I’d like to look at ways to expand the program, provided we can determine how to eliminate the two major problems of supply and transportation.\"}, {\"role\": \"assistant\", \"content\": \"What can you tell me about the stakeholders and decision-makers involved?\"}, {\"role\": \"user\", \"content\": \"As you can imagine, with a program like this you have many stakeholders. You have national governments, other donors, international organizations like the WHO, UNICEF, and the GAVI Alliance, the private sector, academia, civil society organizations, faith-based organizations, and local communities – just to name a few. They all want to and need to participate.\"}, {\"role\": \"assistant\", \"content\": \"That seems overwhelming. What exactly is the Gates Foundation’s part? What are its priorities?\"}, {\"role\": \"user\", \"content\": \"This seems overwhelming. And I’d assume there is a lot of redundancy. What exactly is the Gates Foundation’s part? What are its priorities?\"}, {\"role\": \"assistant\", \"content\": \"Gates supports the collection, analysis, and use of high-quality vaccine-related data, improving the measurement and evaluation of vaccination efforts. Gates also supports new diagnostic tools to help health workers access need. They support strengthening supply chain logistics, including storage, transportation, and distribution of vaccines.\"}, {\"role\": \"user\", \"content\": \"When you say support, you mean give money.\"}, {\"role\": \"assistant\", \"content\": \"While that’s a big part of it, they also contribute other things.\"}, {\"role\": \"user\", \"content\": \"Pardon me. I’d like to look at this problem from a 30,000-foot level. You have millions of children each year who need to be vaccinated. You have scores of organizations and agencies wanting to help out, clamoring for their share of the process. Helping out, yes, but also increasing the complexity of the situation. But you still have two major problems – a shortage of vaccine and the logistics of getting it to the end-user, the child. The Gates Foundation has thrown a lot of money and resources at this problem, and it has done a lot of good, but these two main problems remain.\"}, {\"role\": \"assistant\", \"content\": \"Okay. What do you suggest?\"}, {\"role\": \"user\", \"content\": \"Tackle something you can control. The Gates Foundation should acquire the polio vaccine business from either Sanofi Pasteur or the Serum Institute. I would think that these firms would be interested in selling a division that is slowly becoming obsolete. By acquiring the division, the cost of the vaccine should go down. We could increase production, ensuring that there is enough vaccine to reach all those in need. They could also start to produce vaccines for measles and other early childhood diseases.\"}, {\"role\": \"assistant\", \"content\": \"Both of those companies are huge. Serum is privately held, and Sanofi Pasteur did around $40 billion in revenues last year. Even if polio and measles make up only 3 percent of its revenues, that’s still around $1.2 billion. Gates would have to pay at least $3 billion for the division. Besides, Sanofi just opened a $430 million vaccine plant in Canada, so maybe production will increase.\"}, {\"role\": \"user\", \"content\": \"The other problem is logistics. What if Gates partnered with FedEx, or UPS or Amazon? Private-sector firms are more efficient and have greater experience in the area of logistics and transportation than the NGOs do.\"}, {\"role\": \"assistant\", \"content\": \"Clearly you don’t have the patience or tolerance to work with government and nonprofit clients. These are very complex problems with many partners and stakeholders. Yes, there is redundancy, even corruption to a certain degree, but …\"}, {\"role\": \"user\", \"content\": \"That’s not true. I have great respect for the work the NGOs of the world do. I apologize if it sounded like I underestimated their desire and abilities. But I have a hard time believing that Amazon or FedEx couldn’t do a better job at transportation and distribution than the WHO. Yes, there would be organizations that would lose some funding if Gates allocated a big part of its budget to working with FedEx. But in the end, it is all about the children; they are the ultimate stakeholder.\"}, {\"role\": \"assistant\", \"content\": \"Okay. Walk me through your plan to partner with … pick one.\"}, {\"role\": \"user\", \"content\": \"FedEx has established distribution networks within most, if not all, of these countries. They already partner with reliable companies to ensure the delivery of packages for the last mile or miles. Gates can contribute by buying refrigeration units sited at these companies’ warehouses. While these transportation companies already go to many of the towns, they might not go to all of the remote villages. This means two things. One, if they do start going to the remote villages, it would allow other vendors to get their goods to the village, and for village goods to be transported out to other markets. If they don’t, then the NGO’s distribution chain has an established central location where they can pick up the vaccines for transport to the villages. So instead of the NGOs taking them across country, they would be responsible for only that last leg.\"}, {\"role\": \"assistant\", \"content\": \"That sounds promising. Can you provide an example of FedEx's experience in disaster relief?\"}, {\"role\": \"user\", \"content\": \"In addition, FedEx won an award for its relief efforts in response to the 2015 Nepal earthquake. If I remember correctly, they partnered with Direct Relief and other nonprofits to transport critically needed medicines, medical supplies, food, shelter, and water purification equipment from the US to Kathmandu. Their dedication to disaster relief programs makes them an excellent potential partner for the Gates Foundation.\"}, {\"role\": \"assistant\", \"content\": \"You have a meeting with the director of the Gates Foundation vaccine program. What’s your recommendation?\"}{\"messages\": [{\"role\": \"system\", \"content\": \"You are a business operations interview panelist focusing on supply chain optimization and mergers & acquisitions.\"}, {\"role\": \"assistant\", \"content\": \"Welcome! Today, we will discuss a case involving two packaging companies and the potential synergies in their supply chains. To start, can you give me an overview of the two companies and their operations?\"}, {\"role\": \"user\", \"content\": \"Certainly! The two companies are Packaging Inc. and Aluminum Inc. Packaging Inc. specializes in food packaging containers, primarily made of foam, and sells to retail and restaurant customers through large distributors. Aluminum Inc. produces aluminum packaging products for similar customer segments. They have different product characteristics, with Packaging Inc.'s products being lightweight but voluminous, while Aluminum Inc.'s products are dense and heavy.\"}, {\"role\": \"assistant\", \"content\": \"Great! Now, what potential synergies do you think could arise from their merger, particularly in their supply chains?\"}, {\"role\": \"user\", \"content\": \"There are several potential synergies. In procurement, they could consolidate suppliers and buy raw materials in bulk, reducing unit costs. In manufacturing, they could optimize plant utilization and share expertise. Transportation could see benefits from optimized truck loads, and warehousing could be streamlined by consolidating facilities. Lastly, inventory management could improve by reducing seasonal impacts and holding locations.\"}, {\"role\": \"assistant\", \"content\": \"Interesting points! How would you quantify the potential benefits of these synergies?\"}, {\"role\": \"user\", \"content\": \"To quantify the benefits, we could use benchmarks and data modeling. We can analyze existing contracts to identify cost savings. For example, we could look at freight expenses and calculate potential savings from optimized transportation routes and modes.\"}, {\"role\": \"assistant\", \"content\": \"Let's dive deeper into the analysis. What do you learn about the two companies and the potential synergy opportunities by looking at their current footprints?\"}, {\"role\": \"user\", \"content\": \"By examining the map, we can see that Packaging Inc. has many more sites than Aluminum Inc., with most plants and warehouses co-located. This presents opportunities for Aluminum Inc. to expand its reach and reduce freight costs by consolidating facilities in overlapping regions.\"}, {\"role\": \"assistant\", \"content\": \"Excellent observation! Now, given the freight expenses data, what savings can you estimate by combining the two companies?\"}, {\"role\": \"user\", \"content\": \"To estimate savings, we can calculate the effects of reduced rates and mode optimization. For instance, if 50% of previous Less Than Truckload shipments can now go Truckload, we can see significant reductions in costs. After analyzing the data, I estimate total savings of approximately $24.75 million per year.\"}, {\"role\": \"assistant\", \"content\": \"That's a substantial amount! What would be your recommendations based on these findings?\"}, {\"role\": \"user\", \"content\": \"I would recommend pursuing the identified synergies in procurement, manufacturing, freight, warehousing, and inventory management. The freight savings alone are significant, but we should also assess potential risks, such as the feasibility of our assumptions and the complexity of implementation.\"}, {\"role\": \"assistant\", \"content\": \"Very insightful! What would be your next steps following this analysis?\"}, {\"role\": \"user\", \"content\": \"The next steps would include investigating other supply chain opportunities to prioritize initiatives, assessing capital requirements to evaluate the projects' NPVs, and beginning implementation on the highest NPV projects.\"}]}\n" +
            "{\"messages\": [{\"role\": \"system\", \"content\": \"You are a case interview interviewer specializing in banking and insurance mergers.\"}, {\"role\": \"assistant\", \"content\": \"Let's discuss the potential acquisition of an insurance company by a retail bank. What benefits do you think the bank could gain from merging an insurance business into its operations?\"}, {\"role\": \"user\", \"content\": \"The bank could benefit from increased revenue streams by offering insurance products alongside banking services. This could enhance customer loyalty and cross-selling opportunities.\"}, {\"role\": \"assistant\", \"content\": \"That sounds promising. Can you elaborate on any other benefits?\"}, {\"role\": \"user\", \"content\": \"Certainly! The bank could also achieve operational efficiencies by integrating processes and systems, potentially reducing costs in the long run.\"}, {\"role\": \"assistant\", \"content\": \"Great point! Now, let's consider the costs associated with this acquisition. What do you think they might be?\"}, {\"role\": \"user\", \"content\": \"There could be significant upfront costs related to the acquisition itself, including legal fees and integration expenses. Additionally, there might be ongoing costs related to training staff and aligning corporate cultures.\"}, {\"role\": \"assistant\", \"content\": \"Excellent insights. Now, what about the risks involved in this acquisition?\"}, {\"role\": \"user\", \"content\": \"The risks could include regulatory challenges, as the merging of banks and insurance companies has faced scrutiny. There’s also the risk of not achieving the expected synergies or customer acceptance of the new offerings.\"}, {\"role\": \"assistant\", \"content\": \"Those are valid concerns. How would you structure your analysis to present these benefits, costs, and risks effectively?\"}, {\"role\": \"user\", \"content\": \"I would create a simple table to categorize the benefits, costs, and risks. This would help in visualizing the trade-offs and facilitate a clearer discussion.\"}, {\"role\": \"assistant\", \"content\": \"That sounds like a solid approach. Remember, it's important to stay focused and discuss each point in detail as we go along. Are there any additional thoughts you have on this topic?\"}, {\"role\": \"user\", \"content\": \"I think it's crucial to remain flexible during the discussion, as some benefits might not have direct costs associated with them, and vice versa.\"}]}\n" ;

             //        System.out.println("Is valid JSON? " + isValidJson(str));
        ValidationResult result = validateJson(str);
        printValidationResult(result);




        //        try {
//            // 创建一个临时文件
//            File tempFile = File.createTempFile("finetune" + DateUtils.dateTime(), ".jsonl");
//            System.out.println("文件地址：" + tempFile.getAbsoluteFile());
//            // 将字符串写入临时文件
//            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
//                writer.write(str);
//            }
//            System.out.println(tempFile.getAbsoluteFile()+"~~~");
//        } catch (Exception e) {
//        }



    }
}
