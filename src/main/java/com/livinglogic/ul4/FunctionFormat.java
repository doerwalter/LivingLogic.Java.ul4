/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;

import com.livinglogic.utils.MapUtils;

public class FunctionFormat implements Function
{
	public String getName()
	{
		return "format";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 2)
			return call(args[0], args[1]);
		else if (args.length == 3)
			return call(args[0], args[1], args[2]);
		throw new ArgumentCountMismatchException("function", "format", args.length, 2, 3);
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
		StringBuffer output = new StringBuffer();
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
						output.append(new SimpleDateFormat("EE", locale).format(obj));
						break;
					case 'A':
						output.append(new SimpleDateFormat("EEEE", locale).format(obj));
						break;
					case 'b':
						output.append(new SimpleDateFormat("MMM", locale).format(obj));
						break;
					case 'B':
						output.append(new SimpleDateFormat("MMMM", locale).format(obj));
						break;
					case 'c':
					{
						String format = cFormats.get(locale.getLanguage());
						if (format == null)
							format = cFormats.get("en");
						output.append(call(obj, format, locale));
						break;
					}
					case 'd':
						output.append(twodigits.format(calendar.get(Calendar.DAY_OF_MONTH)));
						break;
					case 'f':
						output.append(sixdigits.format(calendar.get(Calendar.MILLISECOND)*1000));
						break;
					case 'H':
						output.append(twodigits.format(calendar.get(Calendar.HOUR_OF_DAY)));
						break;
					case 'I':
						output.append(twodigits.format(((calendar.get(Calendar.HOUR_OF_DAY) - 1) % 12) + 1));
						break;
					case 'j':
						output.append(threedigits.format(calendar.get(Calendar.DAY_OF_YEAR)));
						break;
					case 'm':
						output.append(twodigits.format(calendar.get(Calendar.MONTH)+1));
						break;
					case 'M':
						output.append(twodigits.format(calendar.get(Calendar.MINUTE)));
						break;
					case 'p':
						output.append(new SimpleDateFormat("aa", locale).format(obj));
						break;
					case 'S':
						output.append(twodigits.format(calendar.get(Calendar.SECOND)));
						break;
					case 'U':
						output.append(twodigits.format(MethodWeek.call(obj, 6)));
						break;
					case 'w':
						output.append(weekdayFormats.get(calendar.get(Calendar.DAY_OF_WEEK)));
						break;
					case 'W':
						output.append(twodigits.format(MethodWeek.call(obj, 0)));
						break;
					case 'x':
					{
						String format = xFormats.get(locale.getLanguage());
						if (format == null)
							format = xFormats.get("en");
						output.append(call(obj, format, locale));
						break;
					}
					case 'X':
					{
						String format = XFormats.get(locale.getLanguage());
						if (format == null)
							format = XFormats.get("en");
						output.append(call(obj, format, locale));
						break;
					}
					case 'y':
						output.append(twodigits.format(calendar.get(Calendar.YEAR) % 100));
						break;
					case 'Y':
						output.append(fourdigits.format(calendar.get(Calendar.YEAR)));
						break;
					default:
						output.append(c);
						break;
				}
				escapeCharacterFound = false;
			}
			else
			{
				if (c == '%')
					escapeCharacterFound = true;
				else
					output.append(c);
				
			}
		}
		if (escapeCharacterFound)
			output.append('%');
		return output.toString();
	}

	public static String call(Object obj, String formatString, Locale locale)
	{
		if (obj instanceof Date)
		{
			return call((Date)obj, formatString, locale);
		}
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
