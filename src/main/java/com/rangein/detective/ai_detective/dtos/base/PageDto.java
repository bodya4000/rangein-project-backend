package com.rangein.detective.ai_detective.dtos.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class PageDto<T> {

    private final Integer pageNumber;
    private final Integer pageSize;
    private final Integer totalPages;
    private final Integer totalElements;
    private final List<T> content;

    @JsonCreator
    public PageDto(
            @JsonProperty("pageNumber") Integer pageNumber,
            @JsonProperty("pageSize") Integer pageSize,
            @JsonProperty("totalPages") Integer totalPages,
            @JsonProperty("totalElements") Integer totalElements,
            @JsonProperty("content") List<T> content
    ) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.content = content;
    }

}
