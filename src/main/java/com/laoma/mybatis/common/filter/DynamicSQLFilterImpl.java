package com.laoma.mybatis.common.filter;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * 查询过滤器: 封装前端提交的查询请求
 * 
 * ×将between...and分解为gt和lt
 * 
 */
public class DynamicSQLFilterImpl extends QueryFilter implements java.io.Serializable {
	private static final long serialVersionUID = 8790528179232633456L;
	public static final Log logger = LogFactory.getLog(DynamicSQLFilterImpl.class);

	private boolean searchAll;
	private String filterName;
	private List<Object> paramValues;
	private List<QueryCommand> commands;
	Integer start = 0;
	Integer limit = PagingBean.DEFAULT_PAGE_SIZE;

	public DynamicSQLFilterImpl() {
		searchAll = false;
		filterName = null;
		paramValues = new ArrayList<Object>();
		commands = new ArrayList<QueryCommand>();
		paramValues = new ArrayList<Object>();
		super.setPagingBean(new PagingBean(start, limit));
	}

	public void addFilter(Map<String, String[]> rawParaMap) {
		searchAll = false;

		Set<Map.Entry<String, String[]>> entrySet = rawParaMap.entrySet();
		for (Map.Entry<String, String[]> entry : entrySet) {
			String paraName = entry.getKey();
			String paramValue = entry.getValue()[0];
			if (paraName.startsWith("Q_")) {
				if (StringUtils.isNotBlank(paramValue)) {
					addFilter(paraName, paramValue);
					paramValues.add(paramValue);

				}
			}
		}

		String[] s_start = rawParaMap.get("start");
		String[] s_limit = rawParaMap.get("limit");
		if (s_start != null && StringUtils.isNotEmpty(s_start[0]))
			start = Integer.valueOf(s_start[0]);
		if (s_limit != null && StringUtils.isNotEmpty(s_limit[0]))
			limit = Integer.valueOf(s_limit[0]);
		String[] sortby = rawParaMap.get("sortby");
		String[] order = rawParaMap.get("sort");
		if (sortby != null && order != null && StringUtils.isNotEmpty(sortby[0]) && StringUtils.isNotEmpty(order[0]))
			setSortField(sortby[0], order[0].toLowerCase().equals("desc") ? true : false);
		if (rawParaMap.get("searchAll") != null && "true".equals(rawParaMap.get("searchAll")[0])) {
			searchAll = true;
			super.setPagingBean(new PagingBean());
		} else {
			super.setPagingBean(new PagingBean(start, limit));
		}
	}

	public void addFilter(String paramName, String paramValue) {
		String fieldInfo[] = paramName.split("_");
		Object value = null;
		if (fieldInfo != null && fieldInfo.length == 4) {
			value = ParamUtil.convertObject(fieldInfo[2], paramValue);
			if (value != null) {
				FieldCommandImpl fieldCommand = new FieldCommandImpl(fieldInfo[1], value, fieldInfo[3], this);
				commands.add(fieldCommand);
				paramValues.add(value);
			}
		} else if (fieldInfo != null && fieldInfo.length == 3) {
			FieldCommandImpl fieldCommand = new FieldCommandImpl(fieldInfo[1], value, fieldInfo[2], this);
			commands.add(fieldCommand);
			paramValues.add(value);
		} else {
			logger.error((new StringBuilder("Query param name [")).append(paramName).append("] is not right format.")
					.toString());
		}
	}

	public void setSortField(String orderBy, boolean isDesc) {
		commands.add(new SortCommandImpl(orderBy, isDesc));
	}

	public List<QueryCommand> getCommands() {
		return commands;
	}

	public boolean getSearchAll() {
		return searchAll;
	}

	public void setSearchAll(boolean searchAll) {
		this.searchAll = searchAll;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public List<Object> getParamValueList() {
		return this.paramValues;
	}

	public Map<String, Object> getParameters() {
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
				if (orderBy == null) {
					orderBy = ((SortCommandImpl) cmd).getPartSql();
				} else {
					orderBy += ((SortCommandImpl) cmd).getPartSql2();
				}
			}
		}

		map.put("WhereSQL", where.toString());
		map.put("SortSQL", orderBy);
		return map;
	}

	/**
	 * 组装面向数据库表的SQL
	 * @param cmd
	 * @param parameterMap
	 * @return
	 */
	public String getPartSql(FieldCommandImpl cmd, Map<String, Object> parameterMap) {
		String property = cmd.getProperty();
		String operation = cmd.getOperation();
		Object value = cmd.getValue();

		String partHql = "";
		String fieldKey = operation + "_" + property;

		String fieldKeyReplace = "#{" + operation + "_" + property + "}";
		String fieldName = propertyToFieldName(property);
		String fieldNameWithTable = (fieldMappings != null && fieldMappings.containsKey(fieldName))?fieldMappings.get(fieldName):fieldName;
		if ("LT".equals(operation)) {
			partHql = (new StringBuilder(String.valueOf(fieldNameWithTable))).append(" <").append(fieldKeyReplace).toString();
			parameterMap.put(fieldKey, value);
		} else if ("GT".equals(operation)) {
			partHql = (new StringBuilder(String.valueOf(fieldNameWithTable))).append(" > ").append(fieldKeyReplace).toString();
			parameterMap.put(fieldKey, value);
		} else if ("LE".equals(operation)) {
			partHql = (new StringBuilder(String.valueOf(fieldNameWithTable))).append(" <= ").append(fieldKeyReplace).toString();
			parameterMap.put(fieldKey, value);
		} else if ("GE".equals(operation)) {
			partHql = (new StringBuilder(String.valueOf(fieldNameWithTable))).append(" >= ").append(fieldKeyReplace).toString();
			parameterMap.put(fieldKey, value);
		} else if ("LK".equals(operation)) {
			partHql = (new StringBuilder(String.valueOf(fieldNameWithTable))).append(" like ").append(fieldKeyReplace).toString();
			parameterMap.put(fieldKey, "%" + value + "%");
		} else if ("LFK".equals(operation)) {
			partHql = (new StringBuilder(String.valueOf(fieldNameWithTable))).append(" like ").append(fieldKeyReplace).toString();
			parameterMap.put(fieldKey, value + "%");
		} else if ("RHK".equals(operation)) {
			partHql = (new StringBuilder(String.valueOf(fieldNameWithTable))).append(" like ").append(fieldKeyReplace).toString();
			parameterMap.put(fieldKey, "%" + value);
		} else if ("NULL".equals(operation)) {
			partHql = (new StringBuilder(String.valueOf(fieldNameWithTable))).append(" is null ").toString();
		} else if ("NOTNULL".equals(operation)) {
			partHql = (new StringBuilder(String.valueOf(fieldNameWithTable))).append(" is not null ").toString();
		} else if("IN".equals(operation) || "NIN".equals(operation)) {
			List<String> valueList = Splitter.on(',').trimResults().splitToList(String.valueOf(value));
			StringBuilder sb = new StringBuilder(String.valueOf(fieldNameWithTable)).append("NIN".equals(operation)?" not  ":" ").append("in").append(" ( ");
			for (int i = 0; i < valueList.size(); i++) {
				if (i != 0) {
					sb.append(",");
				}
				sb.append("#{").append(fieldKey).append("_").append(i).append("}");
				parameterMap.put(fieldKey + "_" + i, valueList.get(i));
			}
			partHql = sb.append(" )").toString();
		} else if (!"EMP".equals(operation) && !"NOTEMP".equals(operation)) {
			if ("NEQ".equals(operation)) {
				partHql = (new StringBuilder(String.valueOf(fieldNameWithTable))).append(" !=").append(fieldKeyReplace)
						.toString();
				parameterMap.put(fieldKey, value);
			} else {
				partHql = (new StringBuilder(String.valueOf(partHql))).append(fieldNameWithTable).append(" =")
						.append(fieldKeyReplace).toString();
				parameterMap.put(fieldKey, value);
			}
		}else{
			throw new RuntimeException("Syntax Invalid:Property=" + property + "\tOperation=" + operation + "\tValue=" +value);
		}
		return partHql;
	}


}