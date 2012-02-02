/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间范围
 * 
 * @author lichengwu
 * @created 2012-1-23
 * 
 * @version 1.0
 */
final public class TimeRange implements Serializable {

	private static final long serialVersionUID = 4658258882827669495L;

	private static final long MILLISECONDS_PER_DAY = 24L * 60 * 60 * 1000;

	private  Period period;

	private  Date startDate;

	private  Date endDate;

	private TimeRange(Period period, Date startDate, Date endDate) {
		super();
		assert period != null && startDate == null && endDate == null || period == null
		        && startDate != null && endDate != null && startDate.getTime() <= endDate.getTime();
		this.period = period;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public static TimeRange createPeriodRange(Period period) {
		return new TimeRange(period, null, null);
	}

	public static TimeRange createCustomRange(Date startDate, Date endDate) {
		return new TimeRange(null, startDate, endDate);
	}

	public static TimeRange parse(String value) {
		final int index = value.indexOf('-');
		if (index == -1) {
			return Period.valueOfIgnoreCase(value).getRange();
		}
		// rq: on pourrait essayer aussi des dateFormat alternatifs,
		// par exemple même pattern mais sans les slashs ou juste avec jour et
		// mois
		final DateFormat dateFormat = I18N.createDateFormat();
		Date startDate;
		try {
			startDate = dateFormat.parse(value.substring(0, index));
		} catch (final ParseException e) {
			startDate = new Date();
		}
		final Calendar minimum = Calendar.getInstance();
		minimum.add(Calendar.YEAR, -2);
		if (startDate.getTime() < minimum.getTimeInMillis()) {
			// pour raison de performance, on limite à 2 ans (et non 2000 ans)
			startDate = minimum.getTime();
		}
		if (startDate.getTime() > System.currentTimeMillis()) {
			// pour raison de performance, on limite à aujourd'hui (et non 2000
			// ans)
			startDate = new Date();
		}

		Date endDate;
		if (index < value.length() - 1) {
			try {
				endDate = dateFormat.parse(value.substring(index + 1));
			} catch (final ParseException e) {
				endDate = new Date();
			}
			if (endDate.getTime() > System.currentTimeMillis()) {
				// pour raison de performance, on limite à aujourd'hui (et non
				// 2000 ans)
				endDate = new Date();
			}
		} else {
			endDate = new Date();
		}
		if (startDate.after(endDate)) {
			endDate = startDate;
		}

		// la date de fin est incluse jusqu'à 23h59m59s
		// (et le formatage de cette date reste donc au même jour)
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		final Date includedEndDate = calendar.getTime();

		return TimeRange.createCustomRange(startDate, includedEndDate);
	}

	public Period getPeriod() {
		return period;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getValue() {
		if (period == null) {
			final DateFormat dateFormat = I18N.createDateFormat();
			return dateFormat.format(startDate) + '-' + dateFormat.format(endDate);
		}
		return period.getCode();
	}

	public String getLabel() {
		if (period == null) {
			final DateFormat dateFormat = I18N.createDateFormat();
			return dateFormat.format(startDate) + " - " + dateFormat.format(endDate);
		}
		return period.getLabel();
	}

	public int getDurationDays() {
		if (period == null) {
			// attention endDate contient le dernier jour inclus jusqu'à
			// 23h59m59s (cf parse),
			// donc on ajoute 1s pour compter le dernier jour
			return (int) ((endDate.getTime() + 1000 - startDate.getTime()) / MILLISECONDS_PER_DAY);
		}
		return period.getDurationDays();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[period=" + getPeriod() + ", startDate="
		        + getStartDate() + ", endDate=" + getEndDate() + ']';
	}
}
