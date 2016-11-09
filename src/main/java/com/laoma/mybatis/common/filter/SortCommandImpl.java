package com.laoma.mybatis.common.filter;

import org.apache.commons.lang3.builder.HashCodeBuilder;


public class SortCommandImpl implements QueryCommand{
	private String sortName;
	private boolean isDesc;

	public SortCommandImpl(String sortName, boolean isDesc) {
		this.sortName = sortName;
		this.isDesc = isDesc;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public boolean getIsDesc() {
		return isDesc;
	}

	public void setIsDesc(boolean isDesc) {
		this.isDesc = isDesc;
	}

	public int hashCode() {
		return (new HashCodeBuilder(0xfb187f93, 0xd642e94b)).append(sortName).append(isDesc?"desc":"asc").toHashCode();
	}
	public String getPartSql() {
		return (new StringBuilder(" order by ").append(QueryFilter.propertyToFieldName(String.valueOf(sortName)))).append(" ").append(isDesc?"desc":"asc").toString();
	}

	/**
	 * 第N(N>1)个order by条件
	 * @return
	 */
	public String getPartSql2() {
		return (new StringBuilder(", ").append(QueryFilter.propertyToFieldName(String.valueOf(sortName)))).append(" ").append(isDesc?"desc":"asc").toString();
	}

}
