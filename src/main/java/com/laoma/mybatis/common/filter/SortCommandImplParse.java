package com.laoma.mybatis.common.filter;


public class SortCommandImplParse {
	
	public static String getPartSql(SortCommandImpl sortCommandImpl) {
		return (new StringBuilder(" order by ").append(QueryFilter.propertyToFieldName(String.valueOf(sortCommandImpl.getSortName())))).append(" ").append(sortCommandImpl.getIsDesc() ?"desc":"asc").toString();
	}

}
