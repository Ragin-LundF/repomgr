package com.repomgr.repomanager.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response Object for common messages and a status, if action was successful (valid = true) or if action failed (valid = false)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto {
    @JsonProperty(value = "_status")
    private boolean status;
    @JsonProperty(value = "_message")
    private MessageDto message;

    public ResponseDto() {
    }

    public ResponseDto(boolean status) {
        this.status = status;
    }

    public ResponseDto(boolean status, MessageDto messageDto) {
        this.status = status;
        this.message = messageDto;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public MessageDto getMessage() {
        return message;
    }

    public void setMessage(MessageDto message) {
        this.message = message;
    }
}
