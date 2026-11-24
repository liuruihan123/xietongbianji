package com.xmzs.common.chat.plugin.ConvertText;

import lombok.Data;

@Data
public class ConvertTextResp {

        /**
         * 转化后的内容
         */
        private String convertText;



        public String getConvertText() {
            return convertText;
        }

        public void setConvertText(String convertText) {
            this.convertText = convertText;
        }

}
