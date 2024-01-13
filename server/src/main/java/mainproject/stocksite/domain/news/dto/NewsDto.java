package mainproject.stocksite.domain.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class NewsDto {
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String lastBuildDate;
        private Integer total;
        private Integer start;
        private Integer display;
        private List<Item> items;
        
        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        private static class Item {
            private String title;
            private String originallink;
            private String link;
            private String description;
            private String pubDate;
        }
    }
}
