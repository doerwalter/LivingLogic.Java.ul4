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
import java.util.HashMap;
import java.util.Locale;

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
						int day = calendar.get(Calendar.DAY_OF_MONTH);
						output.append(new SimpleDateFormat("EEE MMM", locale).format(obj));
						output.append(' ');
						if (day < 10)
							output.append(' ');
						output.append(day);
						output.append(' ');
						output.append(twodigits.format(calendar.get(Calendar.HOUR_OF_DAY)));
						output.append(':');
						output.append(twodigits.format(calendar.get(Calendar.MINUTE)));
						output.append(':');
						output.append(twodigits.format(calendar.get(Calendar.SECOND)));
						output.append(' ');
						output.append(fourdigits.format(calendar.get(Calendar.YEAR)));
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
					{
						Calendar calendarFirstday = new GregorianCalendar();
						calendarFirstday.setTime(FunctionDate.call(MethodYear.call(obj), 1, 1));
						int firstday = calendar.get(Calendar.DAY_OF_WEEK) - 1;
						firstday = firstday != 0 ? firstday-1 : 6;
						int value = (MethodYearday.call(obj) + firstday - 1) / 7;
						output.append(twodigits.format(value));
						break;
					}
					case 'w':
						output.append(weekdayFormats.get(calendar.get(Calendar.DAY_OF_WEEK)));
						break;
					case 'W':
					{
						Calendar calendarFirstday = new GregorianCalendar();
						calendarFirstday.setTime(FunctionDate.call(MethodYear.call(obj), 1, 1));
						int firstday = calendar.get(Calendar.DAY_OF_WEEK) - 1;
						int value = (MethodYearday.call(obj) + firstday - 1) / 7;
						output.append(twodigits.format(value));
						break;
					}
					case 'x':
						output.append(twodigits.format(calendar.get(Calendar.MONTH)+1));
						output.append('/');
						output.append(twodigits.format(calendar.get(Calendar.DAY_OF_MONTH)));
						output.append('/');
						output.append(twodigits.format(calendar.get(Calendar.YEAR) % 100));
						break;
					case 'X':
						output.append(twodigits.format(calendar.get(Calendar.HOUR_OF_DAY)));
						output.append(':');
						output.append(twodigits.format(calendar.get(Calendar.MINUTE)));
						output.append(':');
						output.append(twodigits.format(calendar.get(Calendar.SECOND)));
						break;
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
