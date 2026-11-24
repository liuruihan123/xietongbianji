package com.xmzs.common.chat.plugin.news;

import com.xmzs.common.chat.plugin.PluginParam;
import lombok.Data;

@Data
public class NewsReq extends PluginParam {
    /**将短语用引号 (") 括起来以实现完全匹配。
     前置必须带有 + 符号的单词或短语。例如：+比特币
     前面不得带有 - 符号的单词。例如：-比特币
     或者，您可以使用 AND / OR / NOT 关键字，并可以选择用括号将它们分组。例如：加密货币和（以太坊或莱特币）而不是比特币。**/
    private String query;

    /**
     * 时间从某某时候开始 例如 2024-04-22
     */
    private String from;

   /**排序方式
    * relevancy= 相关性比较密切的文章q先来。
    * popularity= 来自流行来源和出版商的文章优先。
    * publishedAt= 最新文章优先。
    * **/
    private String  sortBy;


    private Integer  pageSize;

}
