package com.repomgr.repomanager.rest.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Page Object for pageable queries
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageDto {
    private Long totalElements;
    private Integer totalPages;
    private Integer currentPage;
    private Integer numberOfElements;

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(final Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(final Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(final Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(final Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }
}
