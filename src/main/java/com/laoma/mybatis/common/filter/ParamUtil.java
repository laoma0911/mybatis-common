package com.laoma.mybatis.common.filter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.util.Calendar;


public class ParamUtil {


	public ParamUtil() {
	}

	public static Object convertObject(String type, String paramValue) {
		if (StringUtils.isEmpty(paramValue))
			return null;
		Object value = null;
		try {
			if ("S".equals(type))
				value = paramValue;
			else if ("L".equals(type))
				value = new Long(paramValue);
			else if ("N".equals(type))
				value = new Integer(paramValue);
			else if ("BD".equals(type))
				value = new BigDecimal(paramValue);
			else if ("FT".equals(type))
				value = new Float(paramValue);
			else if ("SN".equals(type))
				value = new Short(paramValue);
			else if ("D".equals(type))
				value = DateUtils.parseDate(paramValue, new String[] { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss" });
			else if ("DL".equals(type)) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(DateUtils.parseDate(paramValue, new String[] { "yyyy-MM-dd" }));
				value = setStartDay(cal).getTime();
			} else if ("DG".equals(type)) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(DateUtils.parseDate(paramValue, new String[] { "yyyy-MM-dd" }));
				value = setEndDay(cal).getTime();
			} else {
				value = paramValue;
			}
		} catch (Exception ex) {
			throw new RuntimeException((new StringBuilder("the data value is not right for the query filed type:")).append(ex.getMessage()).toString());
		}
		return value;
	}
	public static Calendar setStartDay(Calendar cal) {
		cal.set(11, 0);
		cal.set(12, 0);
		cal.set(13, 0);
		return cal;
	}

	public static Calendar setEndDay(Calendar cal) {
		cal.set(11, 23);
		cal.set(12, 59);
		cal.set(13, 59);
		return cal;
	}
}
