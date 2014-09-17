/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.livinglogic.utils.MapUtils;

public class FunctionFormat extends Function
{
	public String nameUL4()
	{
		return "format";
	}

	private static final Signature signature = new Signature("obj", Signature.required, "fmt", Signature.required, "lang", null);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(List<Object> args)
	{
		return call(args.get(0), args.get(1), args.get(2));
	}

	private static HashMap<Integer, String> weekdayFormats;

	static
	{
		weekdayFormats = new HashMap<Integer, String>();
		weekdayFormats.put(Calendar.SUNDAY, "0");
		weekdayFormats.put(Calendar.MONDAY, "1");
		weekdayFormats.put(Calendar.TUESDAY, "2");
		weekdayFormats.put(Calendar.WEDNESDAY, "3");
		weekdayFormats.put(Calendar.THURSDAY, "4");
		weekdayFormats.put(Calendar.FRIDAY, "5");
		weekdayFormats.put(Calendar.SATURDAY, "6");
	}

	private static DecimalFormat twodigits = new DecimalFormat("00");
	private static DecimalFormat threedigits = new DecimalFormat("000");
	private static DecimalFormat fourdigits = new DecimalFormat("0000");
	private static DecimalFormat sixdigits = new DecimalFormat("000000");

	private static Map<String, String> cFormats = MapUtils.makeMap(
		"de", "%a %d %b %Y %H:%M:%S",
		"en", "%a %d %b %Y %I:%M:%S %p",
		"fr", "%a %d %b %Y %H:%M:%S",
		"es", "%a %d %b %Y %H:%M:%S",
		"it", "%a %d %b %Y %H:%M:%S",
		"da", "%a %d %b %Y %H:%M:%S",
		"sv", "%a %d %b %Y %H.%M.%S",
		"nl", "%a %d %b %Y %H:%M:%S",
		"pt", "%a %d %b %Y %H:%M:%S",
		"cs", "%a\u00a0%d.\u00a0%B\u00a0%Y,\u00a0%H:%M:%S",
		"sk", "%a\u00a0%d.\u00a0%B\u00a0%Y,\u00a0%H:%M:%S",
		"pl", "%a, %d %b %Y, %H:%M:%S",
		"hr", "%a %d %b %Y %H:%M:%S",
		"sr", "%A, %d. %B %Y. %H:%M:%S",
		"ro", "%a %d %b %Y %H:%M:%S",
		"hu", "%Y. %b. %d., %A, %H.%M.%S",
		"tr", "%a %d %b %Y %H:%M:%S",
		"ru", "%a %d %b %Y %H:%M:%S",
		"zh", "%Y\u5e74%b%d\u65e5 %A %H\u65f6%M\u5206%S\u79d2",
		"ko", "%Y\ub144 %B %d\uc77c (%a) %p %I\uc2dc %M\ubd84 %S\ucd08",
		"ja", "%Y\u5e74%B%d\u65e5 %H\u6642%M\u5206%S\u79d2"
	);

	private static Map<String, String> xFormats = MapUtils.makeMap(
		"de", "%d.%m.%Y",
		"en", "%m/%d/%Y",
		"fr", "%d/%m/%Y",
		"es", "%d/%m/%y",
		"it", "%d/%m/%Y",
		"da", "%d-%m-%Y",
		"sv", "%Y-%m-%d",
		"nl", "%d-%m-%y",
		"pt", "%d-%m-%Y",
		"cs", "%d.%m.%Y",
		"sk", "%d.%m.%Y",
		"pl", "%d.%m.%Y",
		"hr", "%d.%m.%Y",
		"sr", "%d.%m.%Y.",
		"ro", "%d.%m.%Y",
		"hu", "%Y-%m-%d",
		"tr", "%d-%m-%Y",
		"ru", "%d.%m.%Y",
		"zh", "%Y\u5e74%b%d\u65e5",
		"ko", "%Y\ub144 %B %d\uc77c",
		"ja", "%Y\u5e74%B%d\u65e5"
	);

	private static Map<String, String> XFormats = MapUtils.makeMap(
		"de", "%H:%M:%S",
		"en", "%H:%M:%S",
		"fr", "%H:%M:%S",
		"es", "%H:%M:%S",
		"it", "%H:%M:%S",
		"da", "%H:%M:%S",
		"sv", "%H.%M.%S",
		"nl", "%H:%M:%S",
		"pt", "%H:%M:%S",
		"cs", "%H:%M:%S",
		"sk", "%H:%M:%S",
		"pl", "%H:%M:%S",
		"hr", "%H:%M:%S",
		"sr", "%H:%M:%S",
		"ro", "%H:%M:%S",
		"hu", "%H.%M.%S",
		"tr", "%H:%M:%S",
		"ru", "%H:%M:%S",
		"zh", "%H\u65f6%M\u5206%S\u79d2",
		"ko", "%H\uc2dc %M\ubd84 %S\ucd08",
		"ja", "%H\u6642%M\u5206%S\u79d2"
	);

	public static String call(Date obj, String formatString, Locale locale)
	{
		if (locale == null)
			locale = Locale.ENGLISH;
		Calendar calendar = new GregorianCalendar();
		calendar.setTime((Date)obj);
		StringBuilder buffer = new StringBuilder();
		boolean escapeCharacterFound = false;
		int formatStringLength = formatString.length();
		for (int i = 0; i < formatStringLength; i++)
		{
			char c = formatString.charAt(i);
			if (escapeCharacterFound)
			{
				switch (c)
				{
					case 'a':
						buffer.append(new SimpleDateFormat("EE", locale).format(obj));
						break;
					case 'A':
						buffer.append(new SimpleDateFormat("EEEE", locale).format(obj));
						break;
					case 'b':
						buffer.append(new SimpleDateFormat("MMM", locale).format(obj));
						break;
					case 'B':
						buffer.append(new SimpleDateFormat("MMMM", locale).format(obj));
						break;
					case 'c':
					{
						String format = cFormats.get(locale.getLanguage());
						if (format == null)
							format = cFormats.get("en");
						buffer.append(call(obj, format, locale));
						break;
					}
					case 'd':
						buffer.append(twodigits.format(calendar.get(Calendar.DAY_OF_MONTH)));
						break;
					case 'f':
						buffer.append(sixdigits.format(calendar.get(Calendar.MILLISECOND)*1000));
						break;
					case 'H':
						buffer.append(twodigits.format(calendar.get(Calendar.HOUR_OF_DAY)));
						break;
					case 'I':
						buffer.append(twodigits.format(((calendar.get(Calendar.HOUR_OF_DAY) - 1) % 12) + 1));
						break;
					case 'j':
						buffer.append(threedigits.format(calendar.get(Calendar.DAY_OF_YEAR)));
						break;
					case 'm':
						buffer.append(twodigits.format(calendar.get(Calendar.MONTH)+1));
						break;
					case 'M':
						buffer.append(twodigits.format(calendar.get(Calendar.MINUTE)));
						break;
					case 'p':
						buffer.append(new SimpleDateFormat("aa", locale).format(obj));
						break;
					case 'S':
						buffer.append(twodigits.format(calendar.get(Calendar.SECOND)));
						break;
					case 'U':
						buffer.append(twodigits.format(BoundDateMethodWeek.call(obj, 6)));
						break;
					case 'w':
						buffer.append(weekdayFormats.get(calendar.get(Calendar.DAY_OF_WEEK)));
						break;
					case 'W':
						buffer.append(twodigits.format(BoundDateMethodWeek.call(obj, 0)));
						break;
					case 'x':
					{
						String format = xFormats.get(locale.getLanguage());
						if (format == null)
							format = xFormats.get("en");
						buffer.append(call(obj, format, locale));
						break;
					}
					case 'X':
					{
						String format = XFormats.get(locale.getLanguage());
						if (format == null)
							format = XFormats.get("en");
						buffer.append(call(obj, format, locale));
						break;
					}
					case 'y':
						buffer.append(twodigits.format(calendar.get(Calendar.YEAR) % 100));
						break;
					case 'Y':
						buffer.append(fourdigits.format(calendar.get(Calendar.YEAR)));
						break;
					default:
						buffer.append(c);
						break;
				}
				escapeCharacterFound = false;
			}
			else
			{
				if (c == '%')
					escapeCharacterFound = true;
				else
					buffer.append(c);
				
			}
		}
		if (escapeCharacterFound)
			buffer.append('%');
		return buffer.toString();
	}

	private static class IntegerFormat
	{
		// The format string is
		// [[fill]align][sign][#][0][minimumwidth][type]
		private char fill = ' ';
		private char align = '>'; // '<', '>', '=' or '^'
		private char sign = '-'; // '+', '-' or ' '
		private boolean alternate = false;
		private int minimumwidth = 0;
		private char type = 'd'; // 'b', 'c', 'd', 'o', 'x', 'X' or 'n'

		public IntegerFormat(String formatString)
		{
			String workStr = formatString;

			// Determine output type
			if (workStr.endsWith("b") || workStr.endsWith("c") || workStr.endsWith("d") || workStr.endsWith("o") || workStr.endsWith("x") || workStr.endsWith("X") || workStr.endsWith("n"))
			{
				this.type = workStr.charAt(workStr.length()-1);
				workStr = workStr.substring(0, workStr.length()-1);
			}

			// Extract minimum width
			int minimumwidthPos = workStr.length();
			while (minimumwidthPos > 0 && Character.isDigit(workStr.charAt(minimumwidthPos-1)))
				--minimumwidthPos;
			String minimumwidthStr = workStr.substring(minimumwidthPos);
			workStr = workStr.substring(0, minimumwidthPos);
			if (minimumwidthStr.length() > 0)
			{
				if (minimumwidthStr.startsWith("0"))
				{
					this.align = '=';
					this.fill = '0';
				}
				this.minimumwidth = Integer.parseInt(minimumwidthStr);
			}

			// Alternate form?
			if (workStr.endsWith("#"))
			{
				this.alternate = true;
				workStr = workStr.substring(0, workStr.length()-1);
			}

			// Determine sign
			if (workStr.endsWith("+") || workStr.endsWith("-") || workStr.endsWith(" "))
			{
				if (this.type == 'c')
					throw new RuntimeException("sign not allowed for integer format type 'c'");
				this.sign = workStr.charAt(workStr.length()-1);
				workStr = workStr.substring(0, workStr.length()-1);
			}

			// Extract fill and align char
			if (workStr.length() >= 3)
			{
				throw new RuntimeException("illegal integer format string " + FunctionRepr.call(formatString));
			}
			else if (workStr.length() == 2)
			{
				if (workStr.endsWith("<") || workStr.endsWith(">") || workStr.endsWith("=") || workStr.endsWith("^"))
				{
					this.align = workStr.charAt(1);
					this.fill = workStr.charAt(0);
				}
				else
					throw new RuntimeException("illegal integer format string " + FunctionRepr.call(formatString));
			}
			else if (workStr.length() == 1)
			{
				if (workStr.equals("<") || workStr.equals(">") || workStr.equals("=") || workStr.equals("^"))
					this.align = workStr.charAt(0);
				else
					throw new RuntimeException("illegal integer format string " + FunctionRepr.call(formatString));
			}
		}

		public String toString()
		{
			return "fill=" + Character.toString(fill) + "; align=" + Character.toString(align) + "; sign=" + Character.toString(sign) + "; alternate=" + (alternate ? "true" : "false") + "; minimumwidth=" + minimumwidth + "; type=" + Character.toString(type);
		}
	}

	private static String formatIntegerString(String string, boolean neg, IntegerFormat format)
	{
		if (format.align == '=')
		{
			int minimumwidth = format.minimumwidth;
			if (neg || format.sign != '-')
				--minimumwidth;
			if (format.alternate && (format.type == 'b' || format.type == 'o' || format.type == 'x' || format.type == 'X'))
				minimumwidth -= 2;

			if (string.length() < minimumwidth)
				string = StringUtils.repeat(Character.toString(format.fill), minimumwidth-string.length()) + string;

			if (format.alternate && (format.type == 'b' || format.type == 'o' || format.type == 'x' || format.type == 'X'))
				string = "0" + Character.toString(format.type) + string;

			if (neg)
				string = "-" + string;
			else
			{
				if (format.sign != '-')
					string = Character.toString(format.sign) + string;
			}
			return string;
		}
		else
		{
			if (format.alternate && (format.type == 'b' || format.type == 'o' || format.type == 'x' || format.type == 'X'))
				string = "0" + Character.toString(format.type) + string;
			if (neg)
				string = "-" + string;
			else
			{
				if (format.sign != '-')
					string = Character.toString(format.sign) + string;
			}
			if (string.length() < format.minimumwidth)
			{
				if (format.align == '<')
					string = string + StringUtils.repeat(Character.toString(format.fill), format.minimumwidth-string.length());
				else if (format.align == '>')
					string = StringUtils.repeat(Character.toString(format.fill), format.minimumwidth-string.length()) + string;
				else // if (format.align == '^')
				{
					int pad = format.minimumwidth - string.length();
					int padBefore = pad/2;
					int padAfter = pad-padBefore;
					string = StringUtils.repeat(Character.toString(format.fill), padBefore) + string + StringUtils.repeat(Character.toString(format.fill), padAfter);
				}
			}
			return string;
		}
	}

	public static String call(BigInteger obj, String formatString, Locale locale)
	{
		IntegerFormat format = new IntegerFormat(formatString);

		if (locale == null)
			locale = Locale.ENGLISH;

		String output = null;

		boolean neg = obj.signum() < 0;
		if (neg)
			obj = obj.negate();

		switch (format.type)
		{
			case 'b':
				output = obj.toString(2);
				break;
			case 'c':
				if (neg || obj.compareTo(new BigInteger("65535")) > 0)
					throw new RuntimeException("value out of bounds for c format");
				output = Character.toString((char)obj.intValue());
				break;
			case 'd':
				output = obj.toString();
				break;
			case 'o':
				output = obj.toString(8);
				break;
			case 'x':
				output = obj.toString(16);
				break;
			case 'X':
				output = obj.toString(16).toUpperCase();
				break;
			case 'n':
				// FIXME: locale formatting
				output = obj.toString();
				break;
		}
		return formatIntegerString(output, neg, format);
	}

	public static String call(long obj, String formatString, Locale locale)
	{
		IntegerFormat format = new IntegerFormat(formatString);

		if (locale == null)
			locale = Locale.ENGLISH;

		String output = null;

		boolean neg = obj < 0;
		if (neg)
			obj = -obj;

		switch (format.type)
		{
			case 'b':
				output = Long.toBinaryString(obj);
				break;
			case 'c':
				if (neg || obj > 0xffff)
					throw new RuntimeException("value out of bounds for c format");
				output = Character.toString((char)obj);
				break;
			case 'd':
				output = Long.toString(obj);
				break;
			case 'o':
				output = Long.toOctalString(obj);
				break;
			case 'x':
				output = Long.toHexString(obj);
				break;
			case 'X':
				output = Long.toHexString(obj).toUpperCase();
				break;
			case 'n':
				// FIXME: locale formatting
				output = Long.toString(obj);
				break;
		}
		return formatIntegerString(output, neg, format);
	}

	public static String call(Object obj, String formatString, Locale locale)
	{
		if (obj instanceof Date)
			return call((Date)obj, formatString, locale);
		else if (obj instanceof Integer || obj instanceof Long || obj instanceof Byte || obj instanceof Short || obj instanceof Boolean)
			return call(Utils.toLong(obj), formatString, locale);
		else if (obj instanceof BigInteger)
			return call((BigInteger)obj, formatString, locale);
		throw new ArgumentTypeMismatchException("format({}, {}, {})", obj, formatString, locale);
	}

	public static String call(Object obj, Object formatString, Object lang)
	{
		if (formatString instanceof String)
		{
			if (lang == null)
				return call(obj, (String)formatString, null);
			else if (lang instanceof String)
			{
				Locale locale;
				int seppos = ((String)lang).indexOf("_");
				if (seppos >= 0)
					locale = new Locale(((String)lang).substring(0, seppos), ((String)lang).substring(seppos+1));
				else
					locale = new Locale((String)lang);
				return call(obj, (String)formatString, locale);
			}
		}
		throw new ArgumentTypeMismatchException("format({}, {}, {})", obj, formatString, lang);
	}

	public static String call(Object obj, String formatString)
	{
		return call(obj, formatString, null);
	}

	public static String call(Object obj, Object formatString)
	{
		return call(obj, formatString, null);
	}
}
