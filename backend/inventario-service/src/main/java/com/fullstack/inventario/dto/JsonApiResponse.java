package com.fullstack.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonApiResponse<T> {
    private T data;
    private List<JsonApiError> errors;
    private Map<String, Object> meta;
    private Links links;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class JsonApiError {
        private String id;
        private Integer status;
        private String code;
        private String title;
        private String detail;
        private Map<String, Object> meta;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Links {
        private String self;
        private String first;
        private String last;
        private String next;
        private String prev;

        public Links(String self) {
            this.self = self;
        }
    }
}

