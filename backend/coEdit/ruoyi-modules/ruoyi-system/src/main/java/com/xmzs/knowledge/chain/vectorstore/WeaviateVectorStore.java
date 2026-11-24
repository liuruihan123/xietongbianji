package com.xmzs.knowledge.chain.vectorstore;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.internal.LinkedTreeMap;
import com.xmzs.knowledge.chain.retrieve.PromptRetrieverProperties;
import io.weaviate.client.Config;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.data.model.WeaviateObject;
import io.weaviate.client.v1.data.replication.model.ConsistencyLevel;
import io.weaviate.client.v1.filters.Operator;
import io.weaviate.client.v1.filters.WhereFilter;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.argument.NearTextArgument;
import io.weaviate.client.v1.graphql.query.argument.NearVectorArgument;
import io.weaviate.client.v1.graphql.query.fields.Field;
import io.weaviate.client.v1.misc.model.Meta;
import io.weaviate.client.v1.misc.model.ReplicationConfig;
import io.weaviate.client.v1.misc.model.ShardingConfig;
import io.weaviate.client.v1.misc.model.VectorIndexConfig;
import io.weaviate.client.v1.schema.model.DataType;
import io.weaviate.client.v1.schema.model.Property;
import io.weaviate.client.v1.schema.model.Schema;
import io.weaviate.client.v1.schema.model.WeaviateClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class WeaviateVectorStore implements VectorStore {

    @Value("${chain.vector.store.weaviate.protocol}")
    private String protocol;
    @Value("${chain.vector.store.weaviate.host}")
    private String host;

    @Value("${chain.vector.store.weaviate.classname}")
    private String className;

    private final PromptRetrieverProperties promptRetrieverProperties;

    public WeaviateVectorStore(PromptRetrieverProperties promptRetrieverProperties) {
        this.promptRetrieverProperties = promptRetrieverProperties;
    }

    public WeaviateClient getClient() {
        Config config = new Config(protocol, host);
        WeaviateClient client = new WeaviateClient(config);
        return client;
    }

    public Result<Meta> getMeta() {
        WeaviateClient client = getClient();
        Result<Meta> meta = client.misc().metaGetter().run();
        if (meta.getError() == null) {
            System.out.printf("meta.hostname: %s\n", meta.getResult().getHostname());
            System.out.printf("meta.version: %s\n", meta.getResult().getVersion());
            System.out.printf("meta.modules: %s\n", meta.getResult().getModules());
        } else {
            System.out.printf("Error: %s\n", meta.getError().getMessages());
        }
        return meta;
    }

    public Result<Schema> getSchemas() {
        WeaviateClient client = getClient();
        Result<Schema> result = client.schema().getter().run();
        if (result.hasErrors()) {
            System.out.println(result.getError());
        } else {
            System.out.println(result.getResult());
        }
        return result;
    }


    public Result<Boolean> createSchema(String kid) {
        WeaviateClient client = getClient();

        VectorIndexConfig vectorIndexConfig = VectorIndexConfig.builder()
            .distance("cosine")
            .cleanupIntervalSeconds(300)
            .efConstruction(128)
            .maxConnections(64)
            .vectorCacheMaxObjects(500000L)
            .ef(-1)
            .skip(false)
            .dynamicEfFactor(8)
            .dynamicEfMax(500)
            .dynamicEfMin(100)
            .flatSearchCutoff(40000)
            .build();

        ShardingConfig shardingConfig = ShardingConfig.builder()
            .desiredCount(3)
            .desiredVirtualCount(128)
            .function("murmur3")
            .key("_id")
            .strategy("hash")
            .virtualPerPhysical(128)
            .build();

        ReplicationConfig replicationConfig = ReplicationConfig.builder()
            .factor(1)
            .build();

        JSONObject classModuleConfigValue = new JSONObject();
        classModuleConfigValue.put("vectorizeClassName", false);
        JSONObject classModuleConfig = new JSONObject();
        classModuleConfig.put("text2vec-transformers", classModuleConfigValue);

        JSONObject propertyModuleConfigValueSkipTrue = new JSONObject();
        propertyModuleConfigValueSkipTrue.put("vectorizePropertyName", false);
        propertyModuleConfigValueSkipTrue.put("skip", true);
        JSONObject propertyModuleConfigSkipTrue = new JSONObject();
        propertyModuleConfigSkipTrue.put("text2vec-transformers", propertyModuleConfigValueSkipTrue);

        JSONObject propertyModuleConfigValueSkipFalse = new JSONObject();
        propertyModuleConfigValueSkipFalse.put("vectorizePropertyName", false);
        propertyModuleConfigValueSkipFalse.put("skip", false);
        JSONObject propertyModuleConfigSkipFalse = new JSONObject();
        propertyModuleConfigSkipFalse.put("text2vec-transformers", propertyModuleConfigValueSkipFalse);

        WeaviateClass clazz = WeaviateClass.builder()
            .className(className + kid)
            .description("local knowledge")
            .vectorIndexType("hnsw")
            .vectorizer("text2vec-transformers")
            .shardingConfig(shardingConfig)
            .vectorIndexConfig(vectorIndexConfig)
            .replicationConfig(replicationConfig)
            .moduleConfig(classModuleConfig)
            .properties(new ArrayList() {
                {
                    add(Property.builder()
                        .dataType(new ArrayList() {
                            {
                                add(DataType.TEXT);
                            }
                        })
                        .name("content")
                        .description("The content of the local knowledge,for search")
                        .moduleConfig(propertyModuleConfigSkipFalse)
                        .build());
                    add(Property.builder()
                        .dataType(new ArrayList() {
                            {
                                add(DataType.TEXT);
                            }
                        })
                        .name("kid")
                        .description("The knowledge id of the local knowledge,for search")
                        .moduleConfig(propertyModuleConfigSkipTrue)
                        .build());
                    add(Property.builder()
                        .dataType(new ArrayList() {
                            {
                                add(DataType.TEXT);
                            }
                        })
                        .name("docId")
                        .description("The doc id of the local knowledge,for search")
                        .moduleConfig(propertyModuleConfigSkipTrue)
                        .build());
                    add(Property.builder()
                        .dataType(new ArrayList() {
                            {
                                add(DataType.TEXT);
                            }
                        })
                        .name("fid")
                        .description("The fragment id of the local knowledge,for search")
                        .moduleConfig(propertyModuleConfigSkipTrue)
                        .build());
                    add(Property.builder()
                        .dataType(new ArrayList() {
                            {
                                add(DataType.TEXT);
                            }
                        })
                        .name("uuid")
                        .description("The uuid id of the local knowledge fragment(same with id properties),for search")
                        .moduleConfig(propertyModuleConfigSkipTrue)
                        .build());
                } })
            .build();

        Result<Boolean> result = client.schema().classCreator().withClass(clazz).run();
        if (result.hasErrors()) {
            System.out.println(result.getError());
        }
        System.out.println(result.getResult());
        return result;
    }

    @Override
    public void newSchema(String kid) {
        createSchema(kid);
    }

    @Override
    public void removeByKidAndFid(String kid, String fid) {
        List<String> resultList = new ArrayList<>();
        WeaviateClient client = getClient();
        Field fieldId = Field.builder().name("uuid").build();
        WhereFilter where = WhereFilter.builder()
            .path(new String[]{"fid"})
            .operator(Operator.Equal)
            .valueString(fid)
            .build();
        Result<GraphQLResponse> result = client.graphQL().get()
            .withClassName(className + kid)
            .withFields(fieldId)
            .withWhere(where)
            .run();
        LinkedTreeMap<String, Object> t = (LinkedTreeMap<String, Object>) result.getResult().getData();
        LinkedTreeMap<String, ArrayList<LinkedTreeMap>> l = (LinkedTreeMap<String, ArrayList<LinkedTreeMap>>) t.get("Get");
        ArrayList<LinkedTreeMap> m = l.get(className + kid);
        for (LinkedTreeMap linkedTreeMap : m) {
            String uuid = linkedTreeMap.get("uuid").toString();
            resultList.add(uuid);
        }
        for (String uuid : resultList) {
            Result<Boolean> deleteResult = client.data().deleter()
                .withID(uuid)
                .withClassName(className + kid)
                .withConsistencyLevel(ConsistencyLevel.ALL)  // default QUORUM
                .run();
        }
    }

    @Override
    public void storeEmbeddings(List<String> chunkList, List<List<Double>> vectorList, String kid, String docId, List<String> fidList) {
        WeaviateClient client = getClient();
        for (int i = 0; i < chunkList.size(); i++) {
            if (vectorList != null) {
                List<Double> vector = vectorList.get(i);
                Float[] vf = new Float[vector.size()];
                for (int j = 0; j < vector.size(); j++) {
                    Double value = vector.get(j);
                    vf[j] = value.floatValue();
                }
                Map<String, Object> dataSchema = new HashMap<>();
                dataSchema.put("content", chunkList.get(i));
                dataSchema.put("kid", kid);
                dataSchema.put("docId", docId);
                dataSchema.put("fid", fidList.get(i));
                String uuid = UUID.randomUUID(true).toString();
                dataSchema.put("uuid", uuid);
                Result<WeaviateObject> result = client.data().creator()
                    .withClassName(className + kid)
                    .withID(uuid)
                    .withVector(vf)
                    .withProperties(dataSchema)
                    .run();
            }
        }
    }

    @Override
    public void removeByDocId(String kid, String docId) {
        List<String> resultList = new ArrayList<>();
        WeaviateClient client = getClient();
        Field fieldId = Field.builder().name("uuid").build();
        WhereFilter where = WhereFilter.builder()
            .path(new String[]{"docId"})
            .operator(Operator.Equal)
            .valueString(docId)
            .build();
        Result<GraphQLResponse> result = client.graphQL().get()
            .withClassName(className + kid)
            .withFields(fieldId)
            .withWhere(where)
            .run();
        LinkedTreeMap<String, Object> t = (LinkedTreeMap<String, Object>) result.getResult().getData();
        LinkedTreeMap<String, ArrayList<LinkedTreeMap>> l = (LinkedTreeMap<String, ArrayList<LinkedTreeMap>>) t.get("Get");
        ArrayList<LinkedTreeMap> m = l.get(className + kid);
        for (LinkedTreeMap linkedTreeMap : m) {
            String uuid = linkedTreeMap.get("uuid").toString();
            resultList.add(uuid);
        }
        for (String uuid : resultList) {
            Result<Boolean> deleteResult = client.data().deleter()
                .withID(uuid)
                .withClassName(className + kid)
                .withConsistencyLevel(ConsistencyLevel.ALL)  // default QUORUM
                .run();
        }
    }

    @Override
    public void removeByKid(String kid) {
        WeaviateClient client = getClient();
        Result<Boolean> result = client.schema().classDeleter().withClassName(className + kid).run();
        if (result.hasErrors()) {
            System.out.println("删除schema失败" + result.getError());
        } else {
            System.out.println("删除schema成功" + result.getResult());
        }
        log.info("drop schema by kid, result = {}", result);
    }

    @Override
    public List<String> nearest(List<Double> queryVector, String kid) {
        if (StringUtils.isBlank(kid)) {
            return new ArrayList<String>();
        }
        List<String> resultList = new ArrayList<>();
        Float[] vf = new Float[queryVector.size()];
        for (int j = 0; j < queryVector.size(); j++) {
            Double value = queryVector.get(j);
            vf[j] = value.floatValue();
        }
        WeaviateClient client = getClient();
        Field contentField = Field.builder().name("content").build();
        Field _additional = Field.builder()
            .name("_additional")
            .fields(new Field[]{
                Field.builder().name("distance").build()
            }).build();
        NearVectorArgument nearVector = NearVectorArgument.builder()
            .vector(vf)
            .distance(1.6f) // certainty = 1f - distance /2f
            .build();
        Result<GraphQLResponse> result = client.graphQL().get()
            .withClassName(className + kid)
            .withFields(contentField, _additional)
            .withNearVector(nearVector)
            .withLimit(promptRetrieverProperties.getLimits())
            .run();
        LinkedTreeMap<String, Object> t = (LinkedTreeMap<String, Object>) result.getResult().getData();
        LinkedTreeMap<String, ArrayList<LinkedTreeMap>> l = (LinkedTreeMap<String, ArrayList<LinkedTreeMap>>) t.get("Get");
        ArrayList<LinkedTreeMap> m = l.get(className + kid);
        for (LinkedTreeMap linkedTreeMap : m) {
            String content = linkedTreeMap.get("content").toString();
            resultList.add(content);
        }
        return resultList;
    }


    //支持多个向量数据同时搜索
    @Override
    public List<String> nearestByMore(List<Double> queryVector, String kid) {
        if (StringUtils.isBlank(kid)) {
            return new ArrayList<>();
        }

        // 将查询向量转换为 Float 数组
        Float[] vf = queryVector.stream()
            .map(Double::floatValue)
            .toArray(Float[]::new);

        WeaviateClient client = getClient();
        Field contentField = Field.builder().name("content").build();
        Field additionalField = Field.builder()
            .name("_additional")
            .fields(new Field[]{
                Field.builder().name("distance").build()
            })
            .build();
        NearVectorArgument nearVector = NearVectorArgument.builder()
            .vector(vf)
            .distance(1.6f) // certainty = 1f - distance / 2f
            .build();

        // 使用优先级队列来存储结果
        PriorityQueue<LinkedTreeMap<String, Object>> priorityQueue = new PriorityQueue<>(
            promptRetrieverProperties.getLimits(),
            (a, b) -> {
                // 根据距离进行排序，使用 Double 进行比较
                Double distanceA = (Double) ((LinkedTreeMap) a.get("_additional")).get("distance");
                Double distanceB = (Double) ((LinkedTreeMap) b.get("_additional")).get("distance");
                return distanceA.compareTo(distanceB);
            }
        );

        String[] kids = kid.split(",");

        for (String singleKid : kids) {
            singleKid = singleKid.trim();
            if (StringUtils.isBlank(singleKid)) {
                continue; // 跳过空的 kid
            }

            // 执行查询
            Result<GraphQLResponse> result = client.graphQL().get()
                .withClassName(className + singleKid)
                .withFields(contentField, additionalField)
                .withNearVector(nearVector)
                .withLimit(promptRetrieverProperties.getLimits())
                .run();

            // 处理查询结果
            LinkedTreeMap<String, Object> data = (LinkedTreeMap<String, Object>) result.getResult().getData();
            if(ObjectUtil.isEmpty(data)||data==null){
                return new ArrayList<>();
            }
            LinkedTreeMap<String, ArrayList<LinkedTreeMap>> getData = (LinkedTreeMap<String, ArrayList<LinkedTreeMap>>) data.get("Get");
            ArrayList<LinkedTreeMap> items = getData != null ? getData.get(className + singleKid) : null;

            if (items != null) {
                for (LinkedTreeMap item : items) {
                    // 将结果添加到优先级队列
                    priorityQueue.offer(item);
                    // 如果队列的大小超过限制，则移除最不相关的元素
                    if (priorityQueue.size() > promptRetrieverProperties.getLimits()) {
                        priorityQueue.poll();
                    }
                }
            }
        }

        // 从优先级队列中提取结果
        List<String> resultList = new ArrayList<>();
        while (!priorityQueue.isEmpty()) {
            LinkedTreeMap<String, Object> item = priorityQueue.poll();
            String content = (String) item.get("content");
            resultList.add(content);
        }

        // 由于是优先级队列，可能需要反转结果以保持相关度顺序
        Collections.reverse(resultList);
        return resultList;
    }


    //弃用原因 结果需要使用 创建一个优先级队列 限制结果数量
//    public List<String> nearestByMore(List<Double> queryVector, String kid) {
//        if (StringUtils.isBlank(kid)) {
//            return new ArrayList<>();
//        }
//
//        // 将查询向量转换为 Float 数组
//        Float[] vf = queryVector.stream()
//            .map(Double::floatValue)
//            .toArray(Float[]::new);
//
//        WeaviateClient client = getClient();
//        Field contentField = Field.builder().name("content").build();
//        Field additionalField = Field.builder()
//            .name("_additional")
//            .fields(new Field[]{
//                Field.builder().name("distance").build()
//            })
//            .build();
//        NearVectorArgument nearVector = NearVectorArgument.builder()
//            .vector(vf)
//            .distance(1.6f) // certainty = 1f - distance / 2f
//            .build();
//
//        List<String> resultList = new ArrayList<>();
//        String[] kids = kid.split(",");
//
//        for (String singleKid : kids) {
//            singleKid = singleKid.trim();
//            if (StringUtils.isBlank(singleKid)) {
//                continue; // 跳过空的 kid
//            }
//
//            // 执行查询
//            Result<GraphQLResponse> result = client.graphQL().get()
//                .withClassName(className + singleKid)
//                .withFields(contentField, additionalField)
//                .withNearVector(nearVector)
//                .withLimit(promptRetrieverProperties.getLimits())
//                .run();
//
//            // 处理查询结果
//            LinkedTreeMap<String, Object> data = (LinkedTreeMap<String, Object>) result.getResult().getData();
//            LinkedTreeMap<String, ArrayList<LinkedTreeMap>> getData = (LinkedTreeMap<String, ArrayList<LinkedTreeMap>>) data.get("Get");
//            ArrayList<LinkedTreeMap> items = getData != null ? getData.get(className + singleKid) : null;
//
//            if (items != null) {
//                for (LinkedTreeMap item : items) {
//                    String content = (String) item.get("content");
//                    resultList.add(content);
//                }
//            }
//        }
//
//        return resultList;
//    }


    @Override
    public List<String> nearest(String query, String kid) {
        if (StringUtils.isBlank(kid)) {
            return new ArrayList<String>();
        }
        List<String> resultList = new ArrayList<>();
        WeaviateClient client = getClient();
        Field contentField = Field.builder().name("content").build();
        Field _additional = Field.builder()
            .name("_additional")
            .fields(new Field[]{
                Field.builder().name("distance").build()
            }).build();
        NearTextArgument nearText = client.graphQL().arguments().nearTextArgBuilder()
            .concepts(new String[]{query})
            .distance(1.6f) // certainty = 1f - distance /2f
            .build();

        Result<GraphQLResponse> result = client.graphQL().get()
            .withClassName(className + kid)
            .withFields(contentField, _additional)
            .withNearText(nearText)
            .withLimit(promptRetrieverProperties.getLimits())
            .run();
        LinkedTreeMap<String, Object> t = (LinkedTreeMap<String, Object>) result.getResult().getData();
        LinkedTreeMap<String, ArrayList<LinkedTreeMap>> l = (LinkedTreeMap<String, ArrayList<LinkedTreeMap>>) t.get("Get");
        ArrayList<LinkedTreeMap> m = l.get(className + kid);
        for (LinkedTreeMap linkedTreeMap : m) {
            String content = linkedTreeMap.get("content").toString();
            resultList.add(content);
        }
        return resultList;
    }

    public Result<Boolean> deleteSchema(String kid) {
        WeaviateClient client = getClient();
        Result<Boolean> result = client.schema().classDeleter().withClassName(className + kid).run();
        if (result.hasErrors()) {
            System.out.println(result.getError());
        } else {
            System.out.println(result.getResult());
        }
        return result;
    }

    /**
     * 检查知识库是否存在
     * @param kid 知识库ID
     * @return true表示知识库存在，false表示不存在
     */
    public boolean checkKnowledgeBaseExists(String kid) {
        if (StringUtils.isBlank(kid)) {
            log.error("知识库ID不能为空");
            return false;
        }

        try {
            WeaviateClient client = getClient();

            // 只查询一条记录，并且只返回kid字段即可
            Field kidField = Field.builder().name("kid").build();

            Result<GraphQLResponse> result = client.graphQL().get()
                .withClassName(className + kid)
                .withFields(kidField)
                .withLimit(1)
                .run();

            if (result.hasErrors()) {
                // 如果返回的错误信息包含类不存在的提示，说明知识库不存在
                String error = result.getError().toString().toLowerCase();
                if (error.contains("class") && error.contains("not found")) {
                    log.info("知识库不存在: kid={}", kid);
                    return false;
                }
                log.error("查询Weaviate时发生错误: {}", result.getError());
                return false;
            }

            // 处理查询结果
            LinkedTreeMap<String, Object> data = (LinkedTreeMap<String, Object>) result.getResult().getData();
            LinkedTreeMap<String, ArrayList<LinkedTreeMap>> getData = (LinkedTreeMap<String, ArrayList<LinkedTreeMap>>) data.get("Get");

            // 如果能够正常获取到Get数据，说明知识库存在
            return getData != null && getData.containsKey(className + kid);

        } catch (Exception e) {
            log.error("检查知识库是否存在时发生异常", e);
            return false;
        }
    }



    /**
     * 通过fid查询特定知识库中的完整数据
     * @param kid 知识库ID
     * @param fid 文档片段ID
     * @return 查询结果，包含所有字段
     */
    public Map<String, Object> queryWeaviateFullData(String kid, String fid) {
        if (StringUtils.isBlank(kid) || StringUtils.isBlank(fid)) {
            log.error("查询参数不能为空: kid={}, fid={}", kid, fid);
            return null;
        }

        try {
            WeaviateClient client = getClient();

            // 定义要返回的所有字段
            Field fidField = Field.builder().name("fid").build();
            Field kidField = Field.builder().name("kid").build();
            Field uuidField = Field.builder().name("uuid").build();
            Field contentField = Field.builder().name("content").build();
            Field docIdField = Field.builder().name("docId").build();

            // 构建where过滤条件
            WhereFilter where = WhereFilter.builder()
                .path(new String[]{"fid"})
                .operator(Operator.Equal)
                .valueString(fid)
                .build();

            // 执行GraphQL查询
            Result<GraphQLResponse> result = client.graphQL().get()
                .withClassName(className + kid)
                .withFields(fidField, kidField, uuidField, contentField, docIdField)
                .withWhere(where)
                .run();

            if (result.hasErrors()) {
                log.error("查询Weaviate时发生错误: {}", result.getError());
                return null;
            }

            // 处理查询结果
            LinkedTreeMap<String, Object> data = (LinkedTreeMap<String, Object>) result.getResult().getData();
            LinkedTreeMap<String, ArrayList<LinkedTreeMap>> getData = (LinkedTreeMap<String, ArrayList<LinkedTreeMap>>) data.get("Get");
            ArrayList<LinkedTreeMap> items = getData != null ? getData.get(className + kid) : null;

            if (items == null || items.isEmpty()) {
//                log.info("未找到匹配的数据: kid={}, fid={}", kid, fid);
                return null;
            }

            // 获取第一个匹配结果并转换为Map
            return new HashMap<>(items.get(0));
        } catch (Exception e) {
            log.error("查询Weaviate时发生异常", e);
            return null;
        }
    }

    /**
     * 通过docId查询特定知识库中的所有片段数据
     * @param kid 知识库ID
     * @param docId 文档ID
     * @return 查询结果列表
     */
    public List<Map<String, Object>> queryWeaviateByDocId(String kid, String docId) {
        if (StringUtils.isBlank(kid) || StringUtils.isBlank(docId)) {
            log.error("查询参数不能为空: kid={}, docId={}", kid, docId);
            return Collections.emptyList();
        }

        try {
            WeaviateClient client = getClient();

            // 定义要返回的所有字段
            Field fidField = Field.builder().name("fid").build();
            Field kidField = Field.builder().name("kid").build();
            Field uuidField = Field.builder().name("uuid").build();
            Field contentField = Field.builder().name("content").build();
            Field docIdField = Field.builder().name("docId").build();

            // 构建where过滤条件
            WhereFilter where = WhereFilter.builder()
                .path(new String[]{"docId"})
                .operator(Operator.Equal)
                .valueString(docId)
                .build();

            // 执行GraphQL查询
            Result<GraphQLResponse> result = client.graphQL().get()
                .withClassName(className + kid)
                .withFields(fidField, kidField, uuidField, contentField, docIdField)
                .withWhere(where)
                .withLimit(100) // 设置合理的限制
                .run();

            if (result.hasErrors()) {
                log.error("查询Weaviate时发生错误: {}", result.getError());
                return Collections.emptyList();
            }

            // 处理查询结果
            LinkedTreeMap<String, Object> data = (LinkedTreeMap<String, Object>) result.getResult().getData();
            LinkedTreeMap<String, ArrayList<LinkedTreeMap>> getData = (LinkedTreeMap<String, ArrayList<LinkedTreeMap>>) data.get("Get");
            ArrayList<LinkedTreeMap> items = getData != null ? getData.get(className + kid) : null;

            if (items == null || items.isEmpty()) {
                log.info("未找到匹配的数据: kid={}, docId={}", kid, docId);
                return Collections.emptyList();
            }

            // 转换结果为Map列表
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (LinkedTreeMap item : items) {
                resultList.add(new HashMap<>(item));
            }

            return resultList;
        } catch (Exception e) {
            log.error("查询Weaviate时发生异常", e);
            return Collections.emptyList();
        }
    }

    /**
     * 执行自定义GraphQL查询
     * @param kid 知识库ID
     * @param whereFilter 过滤条件
     * @param fields 需要返回的字段
     * @param limit 结果数量限制
     * @return 查询结果列表
     */
    public List<Map<String, Object>> queryWeaviate(String kid, WhereFilter whereFilter, Field[] fields, int limit) {
        if (StringUtils.isBlank(kid)) {
            log.error("知识库ID不能为空");
            return Collections.emptyList();
        }

        if (fields == null || fields.length == 0) {
            log.error("查询字段不能为空");
            return Collections.emptyList();
        }

        try {
            WeaviateClient client = getClient();

            // 构建查询
            io.weaviate.client.v1.graphql.query.Get query = client.graphQL().get()
                .withClassName(className + kid)
                .withFields(fields);

            // 添加过滤条件（如果有）
            if (whereFilter != null) {
                query = query.withWhere(whereFilter);
            }

            // 设置结果数量限制
            if (limit > 0) {
                query = query.withLimit(limit);
            }

            // 执行查询
            Result<GraphQLResponse> result = query.run();

            if (result.hasErrors()) {
                log.error("查询Weaviate时发生错误: {}", result.getError());
                return Collections.emptyList();
            }

            // 处理查询结果
            LinkedTreeMap<String, Object> data = (LinkedTreeMap<String, Object>) result.getResult().getData();
            LinkedTreeMap<String, ArrayList<LinkedTreeMap>> getData = (LinkedTreeMap<String, ArrayList<LinkedTreeMap>>) data.get("Get");
            ArrayList<LinkedTreeMap> items = getData != null ? getData.get(className + kid) : null;

            if (items == null || items.isEmpty()) {
                log.info("未找到匹配的数据");
                return Collections.emptyList();
            }

            // 转换结果为Map列表
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (LinkedTreeMap item : items) {
                resultList.add(new HashMap<>(item));
            }

            return resultList;
        } catch (Exception e) {
            log.error("查询Weaviate时发生异常", e);
            return Collections.emptyList();
        }
    }





}
