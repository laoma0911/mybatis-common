package com.laoma.mybatis.common.filter;

import com.google.common.base.Splitter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析适合mybatis查询过滤器: 封装前端提交的查询请求
 * 
 * 
 */
public class DynamicSQLFilterParse {
	public static final Log logger = LogFactory
			.getLog(DynamicSQLFilterParse.class);

	public static Map<String, Object> getParameters(DynamicSQLFilterImpl filter) {
		List<QueryCommand> commands = filter.getCommands();
		Map<String, Object> map = new HashMap<String, Object>();
		QueryCommand cmd = null;
		StringBuffer where = new StringBuffer("1=1");
		String orderBy = null;
		for (int i = 0; i < commands.size(); i++) {
			cmd = commands.get(i);
			if (cmd instanceof FieldCommandImpl) {
				if (where.length() > 0) {
					where.append(" and ");
				}
				where.append(getPartSql((FieldCommandImpl) cmd, map));
			} else if (cmd instanceof SortCommandImpl) {
				orderBy = SortCommandImplParse
						.getPartSql((SortCommandImpl) cmd);
			}
		}

		map.put("WhereSQL", where.toString());
		map.put("SortSQL", orderBy);
		return map;
	}

	public static String getPartSql(FieldCommandImpl cmd,
			Map<String, Object> parameterMap) {
		String property = cmd.getProperty();
		String operation = cmd.getOperation();
		Object value = cmd.getValue();

		String partHql = "";
		String fieldKey = operation + "_" + property;

		String fieldKeyReplace = "#{" + operation + "_" + property + "}";
		property = QueryFilter.propertyToFieldName(property);
		if ("LT".equals(operation)) {
			partHql = (new StringBuilder(String.valueOf(property)))
					.append(" <").append(fieldKeyReplace).toString();
			parameterMap.put(fieldKey, value);
		} else if ("GT".equals(operation)) {
			partHql = (new StringBuilder(String.valueOf(property)))
					.append(" > ").append(fieldKeyReplace).toString();
			parameterMap.put(fieldKey, value);
		} else if ("LE".equals(operation)) {
			partHql = (new StringBuilder(String.valueOf(property)))
					.append(" <= ").append(fieldKeyReplace).toString();
			parameterMap.put(fieldKey, value);
		} else if ("GE".equals(operation)) {
			partHql = (new StringBuilder(String.valueOf(property)))
					.append(" >= ").append(fieldKeyReplace).toString();
			parameterMap.put(fieldKey, value);
		} else if ("LK".equals(operation)) {
//			partHql = (new StringBuilder(String.valueOf(property)))
//					.append(" like CONCAT('%',").append(fieldKeyReplace).append(",'%')")//此处对数据库应该是不兼容
//					.toString();
			partHql = (new StringBuilder(String.valueOf(property)))
					.append(" like ").append(fieldKeyReplace).append("")
					.toString();
			parameterMap.put(fieldKey, "%"+value+"%");
		} else if ("LFK".equals(operation)) {
			partHql = (new StringBuilder(String.valueOf(property)))
					.append(" like '").append(fieldKeyReplace).append("%'")
					.toString();
			parameterMap.put(fieldKey, value);
		} else if ("RHK".equals(operation)) {
			partHql = (new StringBuilder(String.valueOf(property)))
					.append(" like '%").append(fieldKeyReplace).append("'")
					.toString();
			parameterMap.put(fieldKey, value);
		} else if ("NULL".equals(operation))
			partHql = (new StringBuilder(String.valueOf(property))).append(
					" is null ").toString();
		else if ("NOTNULL".equals(operation))
			partHql = (new StringBuilder(String.valueOf(property))).append(
					" is not null ").toString();
		else if ("IN".equals(operation)) {
			String fieldName = QueryFilter.propertyToFieldName(property);
			List<String> valueList = Splitter.on(',').trimResults()
					.splitToList(String.valueOf(value));
			StringBuilder sb = new StringBuilder(
					String.valueOf(property)).append(" in ( ");
			for (int i = 0; i < valueList.size(); i++) {
				if (i != 0) {
					sb.append(",");
				}
				sb.append("#{").append(operation).append("_").append(fieldName)
						.append("_").append(i).append("}");
				parameterMap.put(operation+"_"+fieldName + "_" + i, valueList.get(i));
			}
			partHql = sb.append(" )").toString();
		}else if ("NOTIN".equals(operation)) {
			String fieldName = QueryFilter.propertyToFieldName(property);
			List<String> valueList = Splitter.on(',').trimResults()
					.splitToList(String.valueOf(value));
			StringBuilder sb = new StringBuilder(
					String.valueOf(property)).append(" not in ( ");
			for (int i = 0; i < valueList.size(); i++) {
				if (i != 0) {
					sb.append(",");
				}
				sb.append("#{").append(operation).append("_").append(fieldName)
						.append("_").append(i).append("}");
				parameterMap.put(fieldKey + "_" + i, valueList.get(i));
			}
			partHql = sb.append(" )").toString();
		}else if (!"EMP".equals(operation) && !"NOTEMP".equals(operation))
			if ("NEQ".equals(operation)) {
				partHql = (new StringBuilder(String.valueOf(property)))
						.append(" !=").append(fieldKeyReplace).toString();
				parameterMap.put(fieldKey, value);
			} else {
				partHql = (new StringBuilder(String.valueOf(partHql)))
						.append(property).append(" =").append(fieldKeyReplace)
						.toString();
				parameterMap.put(fieldKey, value);
			}
		return partHql;
	}
}
