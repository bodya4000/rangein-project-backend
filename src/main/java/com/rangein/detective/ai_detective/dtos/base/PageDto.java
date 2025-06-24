package com.rangein.detective.ai_detective.dtos.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PageDto {

    private final Integer pageNumber;
    private final Integer pageSize;
    private final Integer totalPages;
    private final Integer totalElements;

    @JsonCreator
    public PageDto(
            @JsonProperty("pageNumber") Integer pageNumber,
            @JsonProperty("pageSize") Integer pageSize,
            @JsonProperty("totalPages") Integer totalPages,
            @JsonProperty("totalElements") Integer totalElements
    ) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

}
