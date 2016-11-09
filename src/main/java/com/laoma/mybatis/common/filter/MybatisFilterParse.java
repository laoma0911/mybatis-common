package com.laoma.mybatis.common.filter;

import java.util.Map;

public class MybatisFilterParse {
	public static Map<String, Object> getParameters(QueryFilter filter) {
		if(filter==null){
			return null;
		}
		if(filter instanceof SimpleFilterImpl){
			return SimpleFilterImplParse.getParameters((SimpleFilterImpl)filter);
		}else {
			return DynamicSQLFilterParse.getParameters((DynamicSQLFilterImpl)filter);
		}
	}
}
