package com.xmzs.knowledge.agent.prompt.function.functionObj;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.xmzs.knowledge.agent.core.function.FunctionType;
import lombok.Data;

@Data
public class FunctionTypeFuncObj {

    @JsonPropertyDescription("type of function required to complete task,AI or TOOL")
    @JsonProperty(required = true)
    private FunctionType functionType;
}
