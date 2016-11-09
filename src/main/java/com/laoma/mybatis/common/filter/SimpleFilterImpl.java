package com.laoma.mybatis.common.filter;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 查询过滤器: 封装前端提交的查询请求
 * 
 * ×将between...and分解为gt和lt
 * 
 */
public class SimpleFilterImpl extends QueryFilter {

	private Map<String, Object> parameters;
	private PagingBean pagingBean;
	private SortCommandImpl sortCmd;
	Integer start = 0;
	Integer limit = PagingBean.DEFAULT_PAGE_SIZE;

	public SimpleFilterImpl() {
		parameters = new HashMap<String, Object>();
		pagingBean = new PagingBean(start, limit);
	}

	public void addFilter(Map<String, String[]> rawParaMap) {
		pagingBean = null;
		parameters = new HashMap<String, Object>();

		Set<Map.Entry<String, String[]>> entrySet = rawParaMap.entrySet();
		for (Map.Entry<String, String[]> entry : entrySet) {
			String paraName = entry.getKey();
			String paramValue = entry.getValue()[0];
			if (StringUtils.isNotBlank(paramValue)) {
				addFilter(paraName, paramValue);
			}
		}

		String[] s_start = rawParaMap.get("start");
		String[] s_limit = rawParaMap.get("limit");
		if (s_start != null && StringUtils.isNotEmpty(s_start[0]))
			start = Integer.valueOf(s_start[0]);
		if (s_limit != null && StringUtils.isNotEmpty(s_limit[0]))
			limit = Integer.valueOf(s_limit[0]);
		pagingBean = new PagingBean(start, limit);
	}

	public void addFilter(String paramName, String paramValue) {
		parameters.put(paramName, paramValue);
	}

	public PagingBean getPagingBean() {
		return pagingBean;
	}

	public void setPagingBean(PagingBean pagingBean) {
		this.pagingBean = pagingBean;
	}

	public Map<String, Object> getParameters() {
		if(parameters==null || parameters.size()==0){
			throw new RuntimeException("查询条件不可以为空");
		}
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuffer where = new StringBuffer("1=1");
		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(QueryFilter.propertyToFieldName(entry.getKey()) + "="
					+ entry.getValue());
		}
		params.put("WhereSQL", where);
		if (sortCmd != null) {
			params.put("SortSQL", getPartSql(sortCmd));
		}
		return params;
	}

	public void setSortField(String orderBy, boolean isDesc) {
		sortCmd = new SortCommandImpl(orderBy, isDesc);
	}

	public SortCommandImpl getSortField() {
		return this.sortCmd;
	}

	public static String getPartSql(SortCommandImpl sortCommandImpl) {
		return (new StringBuilder(" order by ").append(QueryFilter
				.propertyToFieldName(String.valueOf(sortCommandImpl
						.getSortName())))).append(" ")
				.append(sortCommandImpl.getIsDesc() ? "desc" : "asc")
				.toString();
	}
}
