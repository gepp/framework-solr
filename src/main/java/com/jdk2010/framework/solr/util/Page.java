package com.jdk2010.framework.solr.util;

import java.util.List;

public class Page {
    private int pageSize;
    private int pageIndex;
    private long totalCount;
    private long totalPage;

    private List list;

    public long getTotalPage() {

        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        totalPage = totalCount / pageSize + (totalCount % pageSize == 0 ? 0 : 1);

    }
}
