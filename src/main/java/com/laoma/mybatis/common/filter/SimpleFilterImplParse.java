package com.laoma.mybatis.common.filter;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 解析mybatis查询过滤器: 封装前端提交的查询请求
 * 
 * ×将between...and分解为gt和lt
 * 
 */
public class SimpleFilterImplParse {
	public static final Log logger = LogFactory.getLog(SimpleFilterImplParse.class);


	public static Map<String, Object> getParameters(SimpleFilterImpl filter) {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuffer where = new StringBuffer("1=1");
		Map<String, Object> parameters=filter.getParameters();
		for(Map.Entry<String, Object> entry:parameters.entrySet()){
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append( QueryFilter.propertyToFieldName(entry.getKey())+"="+entry.getValue());
		}
		params.put("WhereSQL", where);
		SortCommandImpl sortCmd=filter.getSortField();
		if(sortCmd != null){
			params.put("SortSQL", SortCommandImplParse.getPartSql(sortCmd));
		}
		return params;
	}

}
