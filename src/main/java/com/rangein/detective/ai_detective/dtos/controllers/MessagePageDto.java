package com.rangein.detective.ai_detective.dtos.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rangein.detective.ai_detective.dtos.base.PageDto;
import com.rangein.detective.ai_detective.dtos.repositories.MessageWithIdDto;
import lombok.Getter;

import java.util.List;

@Getter
public class MessagePageDto extends PageDto {
    public MessagePageDto(
            @JsonProperty("pageNumber") Integer pageNumber,
            @JsonProperty("pageSize") Integer pageSize,
            @JsonProperty("totalPages") Integer totalPages,
            @JsonProperty("totalElements") Integer totalElements,
            @JsonProperty("messages") List<MessageWithIdDto> messages
    ) {
        super(pageNumber, pageSize, totalPages, totalElements, messages);
        this.messages = messages;
    }

    private final List<MessageWithIdDto> messages;

}
