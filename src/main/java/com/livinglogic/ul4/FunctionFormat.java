/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.livinglogic.utils.MapUtils;

public class FunctionFormat extends Function
{
	@Override
	public String getNameUL4()
	{
		return "format";
	}

	private static final Signature signature = new Signature().addBoth("obj").addBoth("fmt").addBoth("lang", null);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, args.get(0), args.getString(1), args.getString(2, null));
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

	private static int week(EvaluationContext context, Date object, int firstWeekday)
	{
		int yearday = DateTime.yearday(object)+6;
		int jan1Weekday = Date_.weekday(Date_.call(DateTime.year(object), 1, 1));
		while (jan1Weekday != firstWeekday)
		{
			--yearday;
			jan1Weekday = (++jan1Weekday) % 7;
		}
		return yearday/7;
	}

	private static int week(EvaluationContext context, LocalDate object, int firstWeekday)
	{
		int yearday = object.getDayOfYear()+6;
		int jan1Weekday = Date_.weekday(LocalDate.of(object.getYear(), 1, 1));
		while (jan1Weekday != firstWeekday)
		{
			--yearday;
			jan1Weekday = (++jan1Weekday) % 7;
		}
		return yearday/7;
	}

	public static String call(EvaluationContext context, Date obj, String formatString, Locale locale)
	{
		if (locale == null)
			locale = Locale.US;
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
						buffer.append(call(context, obj, format, locale));
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
						buffer.append(twodigits.format(week(context, obj, 6)));
						break;
					case 'w':
						buffer.append(weekdayFormats.get(calendar.get(Calendar.DAY_OF_WEEK)));
						break;
					case 'W':
						buffer.append(twodigits.format(week(context, obj, 0)));
						break;
					case 'x':
					{
						String format = xFormats.get(locale.getLanguage());
						if (format == null)
							format = xFormats.get("en");
						buffer.append(call(context, obj, format, locale));
						break;
					}
					case 'X':
					{
						String format = XFormats.get(locale.getLanguage());
						if (format == null)
							format = XFormats.get("en");
						buffer.append(call(context, obj, format, locale));
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

	public static String call(EvaluationContext context, LocalDate obj, String formatString, Locale locale)
	{
		if (locale == null)
			locale = Locale.US;
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
						buffer.append(DateTimeFormatter.ofPattern("EE", locale).format(obj));
						break;
					case 'A':
						buffer.append(DateTimeFormatter.ofPattern("EEEE", locale).format(obj));
						break;
					case 'b':
						buffer.append(DateTimeFormatter.ofPattern("MMM", locale).format(obj));
						break;
					case 'B':
						buffer.append(DateTimeFormatter.ofPattern("MMMM", locale).format(obj));
						break;
					case 'c':
					{
						String format = cFormats.get(locale.getLanguage());
						if (format == null)
							format = cFormats.get("en");
						buffer.append(call(context, obj, format, locale));
						break;
					}
					case 'd':
						buffer.append(twodigits.format(obj.getDayOfMonth()));
						break;
					case 'f':
						buffer.append("000000");
						break;
					case 'H':
						buffer.append("00");
						break;
					case 'I':
						buffer.append("00");
						break;
					case 'j':
						buffer.append(threedigits.format(obj.getDayOfYear()));
						break;
					case 'm':
						buffer.append(twodigits.format(obj.getMonthValue()));
						break;
					case 'M':
						buffer.append("00");
						break;
					case 'p':
						buffer.append("AM");
						break;
					case 'S':
						buffer.append("00");
						break;
					case 'U':
						buffer.append(twodigits.format(week(context, obj, 6)));
						break;
					case 'w':
						buffer.append(obj.getDayOfWeek().getValue() % 7);
						break;
					case 'W':
						buffer.append(twodigits.format(week(context, obj, 0)));
						break;
					case 'x':
					{
						String format = xFormats.get(locale.getLanguage());
						if (format == null)
							format = xFormats.get("en");
						buffer.append(call(context, obj, format, locale));
						break;
					}
					case 'X':
					{
						String format = XFormats.get(locale.getLanguage());
						if (format == null)
							format = XFormats.get("en");
						buffer.append(call(context, obj, format, locale));
						break;
					}
					case 'y':
						buffer.append(twodigits.format(obj.getYear() % 100));
						break;
					case 'Y':
						buffer.append(fourdigits.format(obj.getYear()));
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

	public static String call(EvaluationContext context, LocalDateTime obj, String formatString, Locale locale)
	{
		if (locale == null)
			locale = Locale.US;
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
						buffer.append(DateTimeFormatter.ofPattern("EE", locale).format(obj));
						break;
					case 'A':
						buffer.append(DateTimeFormatter.ofPattern("EEEE", locale).format(obj));
						break;
					case 'b':
						buffer.append(DateTimeFormatter.ofPattern("MMM", locale).format(obj));
						break;
					case 'B':
						buffer.append(DateTimeFormatter.ofPattern("MMMM", locale).format(obj));
						break;
					case 'c':
					{
						String format = cFormats.get(locale.getLanguage());
						if (format == null)
							format = cFormats.get("en");
						buffer.append(call(context, obj, format, locale));
						break;
					}
					case 'd':
						buffer.append(twodigits.format(obj.getDayOfMonth()));
						break;
					case 'f':
						buffer.append(sixdigits.format(obj.getNano()/1000));
						break;
					case 'H':
						buffer.append(twodigits.format(obj.getHour()));
						break;
					case 'I':
						buffer.append(twodigits.format(((obj.getHour() - 1) % 12) + 1));
						break;
					case 'j':
						buffer.append(threedigits.format(obj.getDayOfYear()));
						break;
					case 'm':
						buffer.append(twodigits.format(obj.getMonthValue()));
						break;
					case 'M':
						buffer.append(twodigits.format(obj.getMinute()));
						break;
					case 'p':
						buffer.append(DateTimeFormatter.ofPattern("a", locale).format(obj));
						break;
					case 'S':
						buffer.append(twodigits.format(obj.getSecond()));
						break;
					case 'U':
						buffer.append(twodigits.format(week(context, obj.toLocalDate(), 6)));
						break;
					case 'w':
						buffer.append(obj.getDayOfWeek().getValue() % 7);
						break;
					case 'W':
						buffer.append(twodigits.format(week(context, obj.toLocalDate(), 0)));
						break;
					case 'x':
					{
						String format = xFormats.get(locale.getLanguage());
						if (format == null)
							format = xFormats.get("en");
						buffer.append(call(context, obj, format, locale));
						break;
					}
					case 'X':
					{
						String format = XFormats.get(locale.getLanguage());
						if (format == null)
							format = XFormats.get("en");
						buffer.append(call(context, obj, format, locale));
						break;
					}
					case 'y':
						buffer.append(twodigits.format(obj.getYear() % 100));
						break;
					case 'Y':
						buffer.append(fourdigits.format(obj.getYear()));
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

	private abstract static class Format
	{
		// The format string is
		// [[fill]align][sign][#][0][minimumwidth][.precision][type]
		protected char fill = ' ';
		protected char align = '>'; // '<', '>', '=' or '^'
		protected char sign = '-'; // '+', '-' or ' '
		protected boolean alternate = false;
		protected int minimumwidth = 0;
		protected int precision = 6;
		protected char type = getDefaultType();

		abstract protected char getDefaultType();
		abstract protected List<Character> getTypes();
		abstract protected boolean determinePrecision();

		protected Format(String formatString)
		{
			String workStr = formatString;

			// Determine output type
			for (Character c: getTypes())
			{
				if (workStr.endsWith(Character.toString(c)))
				{
					this.type = workStr.charAt(workStr.length() - 1);
					workStr = workStr.substring(0, workStr.length() - 1);
					break;
				}
			}

			// determine precision
			if (determinePrecision())
			{
				int indexOfDot = workStr.lastIndexOf('.');
				if (indexOfDot > -1)
				{
					String precisionString = workStr.substring(indexOfDot + 1);
					try
					{
						precision = Integer.valueOf(precisionString);
						workStr = workStr.substring(0, indexOfDot);
					}
					catch (NumberFormatException nfe)
					{
						throw new RuntimeException(String.format("'%s' is not a valid value for precision - an integer is expected", precisionString));
					}
				}
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
			String precisionStr = determinePrecision() ? ("; precision=" + precision) : "";
			return "fill=" + Character.toString(fill) + "; align=" + Character.toString(align) + "; sign=" + Character.toString(sign) + "; alternate=" + (alternate ? "true" : "false") + "; minimumwidth=" + minimumwidth + precisionStr + "; type=" + Character.toString(type);
		}
	}

	private static class InternalDecimalFormat extends Format
	{
		// The format string is
		// [[fill]align][sign][#][0][minimumwidth][.precision][type]

		@Override
		protected char getDefaultType()
		{
			return 'g';
		}

		@Override
		protected List<Character> getTypes()
		{
			return List.of('e', 'E', 'f', 'F');
		}

		@Override
		protected boolean determinePrecision()
		{
			return true;
		}

		/**
		 * Create a format string for the String::format method so that the post formatting is as simple
		 * as possible.
		 * @return
		 */
		public String getJavaFormatString()
		{
			String result = "%";

			if (alternate)
				result += "#";

			if (minimumwidth > 0)
				result += String.valueOf(minimumwidth);

			result += "." + String.valueOf(precision);

			result += type;

			return result;
		}

		public InternalDecimalFormat(String formatString)
		{
			super(formatString);
		}
	}

	private static class IntegerFormat extends Format
	{
		// The format string is
		// [[fill]align][sign][#][0][minimumwidth][type]

		@Override
		protected char getDefaultType()
		{
			return 'd';
		}

		@Override
		protected List<Character> getTypes()
		{
			return List.of('b', 'c', 'd', 'o', 'x', 'X', 'n');
		}

		@Override
		protected boolean determinePrecision()
		{
			return false;
		}

		public IntegerFormat(String formatString)
		{
			super(formatString);
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

	public static String call(EvaluationContext context, BigInteger obj, String formatString, Locale locale)
	{
		IntegerFormat format = new IntegerFormat(formatString);

		if (locale == null)
			locale = Locale.US;

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

	public static String call(EvaluationContext context, long obj, String formatString, Locale locale)
	{
		IntegerFormat format = new IntegerFormat(formatString);

		if (locale == null)
			locale = Locale.US;

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

	private static String formatDoubleString(String string, boolean neg, InternalDecimalFormat format)
	{
		string = string.trim();

		if (format.align == '=')
		{
			int minimumwidth = format.minimumwidth;
			if (neg || format.sign != '-')
				--minimumwidth;

			if (string.length() < minimumwidth)
				string = StringUtils.repeat(Character.toString(format.fill), minimumwidth-string.length()) + string;

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

	public static String call(EvaluationContext context, double obj, String formatString, Locale locale)
	{
		InternalDecimalFormat format = new InternalDecimalFormat(formatString);

		if (locale == null)
			locale = Locale.US;

		String output = null;

		boolean neg = obj < 0;
		if (neg)
			obj = -obj;

		output =  String.format(locale, format.getJavaFormatString(), obj);

		return formatDoubleString(output, neg, format);
	}

	public static String call(EvaluationContext context, BigDecimal obj, String formatString, Locale locale)
	{
		InternalDecimalFormat format = new InternalDecimalFormat(formatString);

		if (locale == null)
			locale = Locale.US;

		String output = null;

		boolean neg = obj.signum() < 0;
		if (neg)
			obj = obj.negate();

		output =  String.format(locale, format.getJavaFormatString(), obj);

		return formatDoubleString(output, neg, format);
	}

	public static String call(EvaluationContext context, Object obj, String formatString, Locale locale)
	{
		if (obj instanceof Date)
			return call(context, (Date)obj, formatString, locale);
		else if (obj instanceof LocalDate)
			return call(context, (LocalDate)obj, formatString, locale);
		else if (obj instanceof LocalDateTime)
			return call(context, (LocalDateTime)obj, formatString, locale);
		else if (obj instanceof Integer || obj instanceof Long || obj instanceof Byte || obj instanceof Short || obj instanceof Boolean)
			return call(context, Utils.toLong(obj), formatString, locale);
		else if (obj instanceof BigInteger)
			return call(context, (BigInteger)obj, formatString, locale);
		else if (obj instanceof Float || obj instanceof Double)
			return call(context, Utils.toDouble(obj), formatString, locale);
		else if (obj instanceof BigDecimal)
			return call(context, (BigDecimal)obj, formatString, locale);
		throw new ArgumentTypeMismatchException("format({!t}, {!t}, {!t}) not supported", obj, formatString, locale);
	}

	public static String call(EvaluationContext context, Object obj, String formatString, String lang)
	{
		Locale locale = null;
		if (lang != null)
		{
			int seppos = ((String)lang).indexOf("_");
			if (seppos >= 0)
				locale = new Locale(((String)lang).substring(0, seppos), ((String)lang).substring(seppos+1));
			else
				locale = new Locale((String)lang);
		}
		return call(context, obj, (String)formatString, locale);
	}

	public static String call(EvaluationContext context, Object obj, Object formatString, Object lang)
	{
		if (formatString instanceof String)
		{
			if (lang == null)
				return call(context, obj, (String)formatString, (Locale)null);
			else if (lang instanceof String)
				return call(context, obj, (String)formatString, (String)lang);
		}
		throw new ArgumentTypeMismatchException("format({!t}, {!t}, {!t}) not supported", obj, formatString, lang);
	}

	public static String call(EvaluationContext context, Object obj, String formatString)
	{
		return call(context, obj, formatString, (Locale)null);
	}

	public static String call(EvaluationContext context, Object obj, Object formatString)
	{
		return call(context, obj, formatString, (Locale)null);
	}

	public static final Function function = new FunctionFormat();
}
