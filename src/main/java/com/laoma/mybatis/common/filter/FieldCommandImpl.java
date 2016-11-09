package com.laoma.mybatis.common.filter;


public class FieldCommandImpl implements QueryCommand {

	private String property;
	private Object value;
	private String operation;

	public FieldCommandImpl(String property, Object value, String operation, DynamicSQLFilterImpl filter) {
		this.property = property;
		this.value = value;
		this.operation = operation;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
}
