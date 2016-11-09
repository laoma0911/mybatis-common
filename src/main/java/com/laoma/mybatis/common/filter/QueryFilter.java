package com.laoma.mybatis.common.filter;


import java.util.Map;


/**
 * 查询过滤器: 封装前端提交的查询请求
 * 
 */
public abstract class QueryFilter implements java.io.Serializable {
	private PagingBean pagingBean;

	protected Map<String, String> fieldMappings;
	public abstract void addFilter(Map<String, String[]> rawParaMap);

	public abstract void addFilter(String paramName, String paramValue);


	public PagingBean getPagingBean(){
		return this.pagingBean;
	};

	public void setPagingBean(PagingBean pagingBean) {
		this.pagingBean = pagingBean;
	}

	
	public abstract void setSortField(String orderBy, boolean isDesc);
	public static String propertyToFieldName(String property) {
		return property.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
	}
	public Map<String, String> getFieldMappings() {
		return fieldMappings;
	}

	public void setFieldMappings(Map<String, String> fieldMappings) {
		this.fieldMappings = fieldMappings;
	}
	
	public abstract Map<String, Object> getParameters();

}
