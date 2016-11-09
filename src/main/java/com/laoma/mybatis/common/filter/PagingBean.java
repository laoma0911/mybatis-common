package com.laoma.mybatis.common.filter;

import java.io.Serializable;

public class PagingBean implements Serializable{

	public static final String PAGING_BEAN = "_paging_bean";
	public static final Integer DEFAULT_PAGE_SIZE = Integer.valueOf(20);
	public static final int SHOW_PAGES = 6;
	public Integer start;
	private Integer pageSize;
	private Integer totalItems;

	public PagingBean(Integer start, Integer limit) {
		pageSize = limit;
		this.start = start;
	}

	public PagingBean() {
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = Integer.valueOf(pageSize);
	}

	public int getTotalItems() {
		return totalItems.intValue();
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public void setTotalItems(Integer totalItems) {
		this.totalItems = totalItems;
	}

	public Integer getFirstResult() {
		return start;
	}
}
