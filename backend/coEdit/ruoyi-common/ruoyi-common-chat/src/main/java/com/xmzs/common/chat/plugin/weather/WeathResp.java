package com.xmzs.common.chat.plugin.weather;

import lombok.Data;

@Data
public class WeathResp {

        /**
         * 天气详情
         */
        private String weathDetail;



        public String getWeathDetail() {
            return weathDetail;
        }

        public void setWeathDetail(String weathDetail) {
            this.weathDetail = weathDetail;
        }

}
