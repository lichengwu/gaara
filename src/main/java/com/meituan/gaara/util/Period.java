/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

import java.util.Locale;

/**
 * 周期
 * 
 * @author lichengwu
 * @created 2012-1-23
 * 
 * @version 1.0
 */

public enum Period {
	/**
	 * 天
	 */
	DAY(1, "day"),
	/**
	 * 星期
	 */
	WEEK(7, "week"),
	/**
	 * 月
	 */
	MONTH(31, "month"),

	/**
	 * 季度
	 */
	QUARTER(30 * 4, "quarter"),
	/**
	 * 年
	 */
	YEAR(366, "year"),

	/**
	 * 全部(2年)
	 */
	ALL(2 * 366, "all");

	private final String code;
	private final String mailCode;
	private final int durationDays;
	private final int durationSeconds;
	private final TimeRange range;

	private Period(int durationDays, String mailCode) {
		this.durationDays = durationDays;
		this.durationSeconds = durationDays * 24 * 60 * 60;
		this.mailCode = mailCode;
		this.code = this.toString().toLowerCase(Locale.getDefault());
		this.range = TimeRange.createPeriodRange(this);
	}

	public static Period valueOfIgnoreCase(String code) {
		if(!StringUtil.isBlank(code)){
			code = code.toLowerCase();
			for(Period period : Period.values()){
				if(period.getCode().equals(code)){
					return period;
				}
			}
		}
		return null;
	}

	public String getCode() {
		return code;
	}

	public String getMailCode() {
		return mailCode;
	}

	public String getLabel() {
		return I18N.tryString(code + "_label");
	}

	public String getLinkLabel() {
		return I18N.tryString(code + "_link_label");
	}

	public int getDurationDays() {
		return durationDays;
	}

	public int getDurationSeconds() {
		return durationSeconds;
	}

	public TimeRange getRange() {
		return range;
	}
}