package com.xmzs.common.chat.plugin.news;

import lombok.Data;

import java.util.List;

@Data
public class NewsResp {

    List<newEn> newsList;

    public static class newEn {
        /**
         * 标题
         */
        private String title;
        /**
         * 描述
         */
        private String description;

        /**
         * 新闻详情
         */
        private String newsUrl;

        /**
         * 文章发布的日期和时间，采用 UTC (+000)
         */
        private String publishedAt;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getNewsUrl() {
            return newsUrl;
        }

        public void setNewsUrl(String newsUrl) {
            this.newsUrl = newsUrl;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }
    }

}
