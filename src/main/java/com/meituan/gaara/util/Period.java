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
	/** Jour. */
	JOUR(1, "calendar_view_day.png", "day"),
	/** Semaine. */
	SEMAINE(7, "calendar_view_week.png", "week"),
	/** Mois. */
	MOIS(31, "calendar_view_month.png", "month"),
	/** Année. */
	ANNEE(366, "calendar.png", "year"),
	/** Tout.
	 * (affiche les graphs sur 2 ans et toutes les requêtes y compris les dernières minutes) */
	TOUT(2 * 366, "calendar.png", "all");

	private final String code;
	private final String mailCode;
	private final int durationDays;
	private final int durationSeconds;
	private final String iconName;
	private final TimeRange range;

	private Period(int durationDays, String iconName, String mailCode) {
		this.durationDays = durationDays;
		this.durationSeconds = durationDays * 24 * 60 * 60;
		this.iconName = iconName;
		this.mailCode = mailCode;
		this.code = this.toString().toLowerCase(Locale.getDefault());
		this.range = TimeRange.createPeriodRange(this);
	}

	public static Period valueOfIgnoreCase(String period) {
		return valueOf(period.toUpperCase(Locale.getDefault()).trim());
	}

	public static Period valueOfByMailCode(String mailPeriod) {
		final String mailCode = mailPeriod.toLowerCase(Locale.getDefault()).trim();
		for (final Period period : values()) {
			if (period.mailCode.equals(mailCode)) {
				return period;
			}
		}
		throw new IllegalArgumentException(mailPeriod);
	}

	public String getCode() {
		return code;
	}

	public String getMailCode() {
		return mailCode;
	}

	public String getLabel() {
		return I18N.getString(code + "_label");
	}

	public String getLinkLabel() {
		return I18N.getString(code + "_link_label");
	}

	public int getDurationDays() {
		return durationDays;
	}

	public int getDurationSeconds() {
		return durationSeconds;
	}

	public String getIconName() {
		return iconName;
	}

	public TimeRange getRange() {
		return range;
	}
}