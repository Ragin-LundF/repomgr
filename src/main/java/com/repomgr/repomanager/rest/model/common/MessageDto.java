package com.repomgr.repomanager.rest.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Message object which contains a category and a message
 * <br />
 * The category can be:
 * <ul>
 *     <li>ERROR</li>
 *     <li>WARN</li>
 *     <li>INFO</li>
 * </ul>
 * To handle that, there are constants:
 * @see com.repomgr.repomanager.constants.Constants#REST_MESSAGE_CODE_ERROR
 * @see com.repomgr.repomanager.constants.Constants#REST_MESSAGE_CODE_WARN
 * @see com.repomgr.repomanager.constants.Constants#REST_MESSAGE_CODE_INFO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDto {
    private String category;
    private String message;

    public MessageDto() {
    }

    public MessageDto(String category, String message) {
        this.category = category;
        this.message = message;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
