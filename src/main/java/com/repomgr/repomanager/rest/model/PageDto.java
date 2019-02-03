package com.repomgr.repomanager.rest.model;

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

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }
}
