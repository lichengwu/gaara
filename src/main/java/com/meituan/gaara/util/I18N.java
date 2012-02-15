/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * 国际化工具
 * <p>
 * 查找com.meituan.gaara.resources.lang包下的语言。 <b>默认为英文。</b>
 * 
 * @author lichengwu
 * @created 2012-1-23
 * 
 * @version 1.0
 */
final public class I18N {

	/**
	 * 资源包路径
	 */
	private static final String RESOURCE_BUNDLE_BASE_PATH = "com.meituan.gaara.resources.lang.language";
	/**
	 * 当前线程的loacal
	 */
	private static final ThreadLocal<Locale> LOCALE_CONTEXT = new ThreadLocal<Locale>();

	/**
	 * 绑定当前线程local(用于格式化数字，时间等).
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 * 
	 * @param locale
	 */
	public static void bindLocale(Locale locale) {
		LOCALE_CONTEXT.set(locale);
	}

	/**
	 * 获得当前线程定的{@link Locale}
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 * 
	 * @return 获得当前线程定的{@link Locale}，如果没有定义，返回{@link Locale#getDefault()}。
	 */
	public static Locale getCurrentLocale() {
		final Locale currentLocale = LOCALE_CONTEXT.get();
		if (currentLocale == null) {
			return Locale.getDefault();
		}
		return currentLocale;
	}

	/**
	 * 获得资源包
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 * 
	 * @return 根据当前线程的Local获得相应的资源包。
	 */
	public static ResourceBundle getResourceBundle() {
		final Locale currentLocale = getCurrentLocale();
		if (Locale.ENGLISH.getLanguage().equals(currentLocale.getLanguage())) {
			// 为了避免使用服务器的默认local，如果用户的Loacal是ENGLISH，因为没有language_en.properties资源文件。
			return ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_PATH, new Locale(""));
		}
		// 如果根据用户的local没有找到对应的资源，那默认返回language.properties的资源。
		return ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_PATH, currentLocale);
	}

	/**
	 * 解绑当前线程的local
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 * 
	 */
	public static void unbindLocale() {
		LOCALE_CONTEXT.remove();
	}

	/**
	 * 返回本地化的字符串
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 * 
	 * @param key
	 *            资源包中定义的key
	 * @return 本地化的字符串
	 */
	public static String getString(String key) {
		return getResourceBundle().getString(key);
	}

	/**
	 * 返回javascript编码后的本地化字符串
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 * 
	 * @param key
	 *            资源包中定义的key
	 * @return javascript编码后的本地化字符串
	 */
	public static String getStringForJavascript(String key) {
		return javascriptEncode(getString(key));
	}

	/**
	 * 将资源的位置{i}格式化，并返回本地化字符串
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 * 
	 * @param key
	 *            资源包中定义的key
	 * @param arguments
	 *            格式化参数
	 * @return 格式化后的本地化字符串
	 */
	public static String getFormattedString(String key, Object... arguments) {
		// MessageFormat特殊字符处理
		final String string = getString(key).replace("'", "''");
		return new MessageFormat(string, getCurrentLocale()).format(arguments);
	}

	/**
	 * 进行javascript编码
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 * 
	 * @param text
	 * @return 编码后的javascript字符串
	 */
	public static String javascriptEncode(String text) {
		return text.replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"")
		        .replace("'", "\\'");
	}

	/**
	 * html编码
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 * 
	 * @param text
	 * @param encodeSpace
	 *            是否对空格进行编码
	 * @return 编码后的html字符串
	 */
	public static String htmlEncode(String text, boolean encodeSpace) {
		String result = text.replaceAll("[&]", "&amp;").replaceAll("[<]", "&lt;")
		        .replaceAll("[>]", "&gt;").replaceAll("[\n]", "<br/>");
		if (encodeSpace) {
			result = result.replaceAll(" ", "&nbsp;");
		}
		return result;
	}

	/**
	 * 将html字符串写入字符流
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 * 
	 * @param html
	 *            <b>#</b>作为分隔符，两个<b>#</b>之间的字符串将作为key被翻译成本地字符串
	 * @param writer
	 *            将要写入的字符流
	 * @throws IOException
	 */
	public static void writeTo(String html, Writer writer) throws IOException {
		int index = html.indexOf('#');
		if (index == -1) {
			writer.write(html);
		} else {
			final ResourceBundle resourceBundle = getResourceBundle();
			int begin = 0;
			while (index != -1) {
				writer.write(html, begin, index - begin);
				final int nextIndex = html.indexOf('#', index + 1);
				final String key = html.substring(index + 1, nextIndex);
				writer.write(resourceBundle.getString(key));
				begin = nextIndex + 1;
				index = html.indexOf('#', begin);
			}
			writer.write(html, begin, html.length() - begin);
		}
	}

	/**
	 * 将html字符串写入字符流，并在结尾加上换行符。参考{@link #writeTo(String, Writer)}。
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 * 
	 * @param html
	 * @param writer
	 * @throws IOException
	 */
	public static void writelnTo(String html, Writer writer) throws IOException {
		writeTo(html, writer);
		writer.write('\n');
	}

	/**
	 * 创建整数格式化NumberFormat实例
	 * <p><b>格式: </b>10,043,223,420</p>
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 *
	 * @return {@link DecimalFormat}
	 */
	public static DecimalFormat createIntegerFormat() {
		return new DecimalFormat("#,##0", new DecimalFormatSymbols(getCurrentLocale()));
	}

	/**
	 * 创建百分比格式化NumberFormat实例
	 * <p><b>格式: </b>55.22</p>
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 *
	 * @return {@link DecimalFormat}
	 */
	public static DecimalFormat createPercentFormat() {
		return new DecimalFormat("0.00", new DecimalFormatSymbols(getCurrentLocale()));
	}

	/**
	 * 默认日期格式化
	 * <p><b>样式:</b>{@link DateFormat#SHORT}</p>
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 *
	 * @return 本地化的{@link DateFormat}
	 */
	public static DateFormat createDateFormat() {
		return DateFormat.getDateInstance(DateFormat.SHORT, getCurrentLocale());
	}

	/**
	 * 默认日期时间格式
	 * <p><b>样式:</b>{@link DateFormat#SHORT}</p>
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 *
	 * @return 本地化的{@link DateFormat}
	 */
	public static DateFormat createDateAndTimeFormat() {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
		        getCurrentLocale());
	}

	/**
	 * TODO 证明
	 * 时间间隔格式化 
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 *
	 * @return
	 */
	public static DateFormat createDurationFormat() {
		final DateFormat durationFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM,
		        Locale.FRENCH);
		durationFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return durationFormat;
	}

	/**
	 * 当前日期
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 *
	 * @return
	 */
	public static String getCurrentDate() {
		return createDateFormat().format(new Date());
	}

	/**
	 * 当前日期和时间
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 *
	 * @return
	 */
	public static String getCurrentDateAndTime() {
		return createDateAndTimeFormat().format(new Date());
	}
}
