/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.StringTokenizer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.List;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import static com.livinglogic.utils.MapUtils.makeMap;


class StringIterator implements Iterator<String>
{
	String string;

	int stringSize;

	int index;

	public StringIterator(String string)
	{
		this.string = string;
		stringSize = string.length();
		index = 0;
	}

	public boolean hasNext()
	{
		return index < stringSize;
	}

	public String next()
	{
		if (index >= stringSize)
		{
			throw new NoSuchElementException("No more characters available!");
		}
		return String.valueOf(string.charAt(index++));
	}

	public void remove()
	{
		throw new UnsupportedOperationException("Strings don't support character removal!");
	}
}

public class Utils
{
	public static String objectType(Object obj)
	{
		if (obj == null)
			return "null";
		else if (obj instanceof Undefined)
			return obj.toString();
		else
			return "<" + obj.getClass().getName() + ">";
	}

	public static BigInteger toBigInteger(int arg)
	{
		return new BigInteger(Integer.toString(arg));
	}

	public static BigInteger toBigInteger(long arg)
	{
		return new BigInteger(Long.toString(arg));
	}

	public static BigDecimal toBigDecimal(int arg)
	{
		return new BigDecimal(Integer.toString(arg));
	}

	public static BigDecimal toBigDecimal(long arg)
	{
		return new BigDecimal(Long.toString(arg));
	}

	public static int toInt(Object arg)
	{
		if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1 : 0;
		else if (arg instanceof Number)
			return ((Number)arg).intValue();
		throw new UnsupportedOperationException(formatMessage("Can't convert {!t} to int!", arg));
	}

	public static long toLong(Object arg)
	{
		if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1L : 0L;
		else if (arg instanceof Number)
			return ((Number)arg).longValue();
		throw new UnsupportedOperationException(formatMessage("Can't convert {!t} to long!", arg));
	}

	public static float toFloat(Object arg)
	{
		if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1.0f : 0.0f;
		else if (arg instanceof Number)
			return ((Number)arg).floatValue();
		throw new UnsupportedOperationException(formatMessage("Can't convert {!t} to float!", arg));
	}

	public static double toDouble(Object arg)
	{
		if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1.0d : 0.0d;
		else if (arg instanceof Number)
			return ((Number)arg).doubleValue();
		throw new UnsupportedOperationException(formatMessage("Can't convert {!t} to double!", arg));
	}

	private static BigInteger intMinValue = new BigInteger(String.valueOf(Integer.MIN_VALUE));
	private static BigInteger intMaxValue = new BigInteger(String.valueOf(Integer.MAX_VALUE));
	private static BigInteger longMinValue = new BigInteger(String.valueOf(Long.MIN_VALUE));
	private static BigInteger longMaxValue = new BigInteger(String.valueOf(Long.MAX_VALUE));

	public static Object narrowBigDecimal(BigDecimal arg)
	{
		try
		{
			return arg.intValueExact();
		}
		catch (ArithmeticException ex1)
		{
			try
			{
				return arg.longValueExact();
			}
			catch (ArithmeticException ex2)
			{
				try
				{
					return arg.toBigIntegerExact();
				}
				catch (ArithmeticException ex3)
				{
					return arg;
				}
			}
		}
	}

	public static Object narrowBigInteger(BigInteger arg)
	{
		if (intMinValue.compareTo(arg) <= 0 && arg.compareTo(intMaxValue) <= 0)
			return arg.intValue();
		else if (longMinValue.compareTo(arg) <= 0 && arg.compareTo(longMaxValue) <= 0)
			return arg.longValue();
		return arg;
	}

	public static int narrowBigIntegerToInt(BigInteger arg)
	{
		if (intMinValue.compareTo(arg) <= 0 && arg.compareTo(intMaxValue) <= 0)
			return arg.intValue();
		else
			throw new IllegalArgumentException("BigInteger value exceeds int");
	}

	public static int narrowLongToInt(long arg)
	{
		if (Integer.MIN_VALUE <= arg && arg <= Integer.MAX_VALUE)
			return (int)arg;
		else
			throw new IllegalArgumentException("long value exceeds int");
	}

	public static LocalDateTime toLocalDateTime(Date date)
	{
		if (date == null)
			return null;
		if (date instanceof java.sql.Date)
			date = new Date(date.getTime());
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static LocalDate toLocalDate(Date date)
	{
		if (date == null)
			return null;
		if (date instanceof java.sql.Date)
			return ((java.sql.Date)date).toLocalDate();
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static BigInteger powerOfTen(int exponent)
	{
		if (exponent == 0)
			return BigInteger.ONE;
		else if (exponent == 1)
			return BigInteger.TEN;
		else
		{
			StringBuilder buffer = new StringBuilder(exponent+1);
			buffer.append("1");
			for (; exponent > 0;--exponent)
				buffer.append("0");
			return new BigInteger(buffer.toString());
		}
	}

	public static int cmp(boolean arg1, boolean arg2)
	{
		return (arg1 ? 1 : 0) - (arg2 ? 1 : 0);
	}

	public static int cmp(int arg1, int arg2)
	{
		return ((arg1 > arg2) ? 1 : 0) - ((arg1 < arg2) ? 1 : 0);
	}

	public static int cmp(long arg1, long arg2)
	{
		return ((arg1 > arg2) ? 1 : 0) - ((arg1 < arg2) ? 1 : 0);
	}

	public static int cmp(float arg1, float arg2)
	{
		return ((arg1 > arg2) ? 1 : 0) - ((arg1 < arg2) ? 1 : 0);
	}

	public static int cmp(double arg1, double arg2)
	{
		return ((arg1 > arg2) ? 1 : 0) - ((arg1 < arg2) ? 1 : 0);
	}

	public static int cmp(List arg1, List arg2, String op)
	{
		if (arg1 == arg2)
			return 0;
		int i = 0;
		int arg2size = arg2.size();
		for (Object item : arg1)
		{
			if (i >= arg2size)
				return 1;
			int res = cmp(arg1.get(i), arg2.get(i), op);
			if (res != 0)
				return res;
			++i;
		}
		int arg1size = arg1.size();
		return arg1size < arg2size ? -1 : 0;
	}

	public static int cmp(Comparable arg1, Comparable arg2)
	{
		return arg1.compareTo(arg2);
	}

	public static int cmp(Object arg1, Object arg2, String op)
	{
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return cmp(toInt(arg1), toInt(arg2));
			else if (arg2 instanceof Long)
				return cmp(toLong(arg1), toLong(arg2));
			else if (arg2 instanceof Float)
				return cmp(toFloat(arg1), toFloat(arg2));
			else if (arg2 instanceof Double)
				return cmp(toDouble(arg1), toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return cmp(toBigInteger(toInt(arg1)), (BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return cmp(new BigDecimal(toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return cmp(toLong(arg1), toLong(arg2));
			else if (arg2 instanceof Float)
				return cmp(toFloat(arg1), toFloat(arg2));
			else if (arg2 instanceof Double)
				return cmp(toDouble(arg1), toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return cmp(toBigInteger(toLong(arg1)), (BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return cmp(new BigDecimal(toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return cmp(toFloat(arg1), toFloat(arg2));
			else if (arg2 instanceof Double)
				return cmp(toDouble(arg1), (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return cmp(new BigDecimal(toDouble(arg1)), new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return cmp(new BigDecimal(toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Double)
		{
			double value1 = (((Double)arg1).doubleValue());
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return cmp(value1, toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return cmp(new BigDecimal(value1), new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return cmp(new BigDecimal(value1), (BigDecimal)arg2);
		}
		else if (arg1 instanceof BigInteger)
		{
			BigInteger value1 = (BigInteger)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return cmp(value1, toBigInteger(toInt(arg2)));
			else if (arg2 instanceof Long)
				return cmp(value1, toBigInteger(toLong(arg2)));
			else if (arg2 instanceof Float)
				return cmp(new BigDecimal(value1), new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return cmp(new BigDecimal(value1), new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return cmp(value1, (BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return cmp(new BigDecimal(value1), (BigDecimal)arg2);
		}
		else if (arg1 instanceof BigDecimal)
		{
			BigDecimal value1 = (BigDecimal)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return cmp(value1, toBigDecimal(toInt(arg2)));
			else if (arg2 instanceof Long)
				return cmp(value1, toBigDecimal(toLong(arg2)));
			else if (arg2 instanceof Float)
				return cmp(value1, new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return cmp(value1, new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return cmp(value1, new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return cmp(value1, (BigDecimal)arg2);
		}
		else if (arg1 instanceof List)
		{
			if (arg2 instanceof List)
				return cmp((List)arg1, (List)arg2, op);
		}
		else if (arg1 instanceof Comparable)
		{
			if (arg2 instanceof Comparable)
				return cmp((Comparable)arg1, (Comparable)arg2);
		}
		throw new UnorderableTypesException(op, arg1, arg2);
	}

	public static boolean eq(Object arg1, Object arg2)
	{
		if ((arg1 == null) || (arg2 == null))
		{
			return arg1 == arg2;
		}
		else if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return toInt(arg1) == toInt(arg2);
			else if (arg2 instanceof Long)
				return toLong(arg1) == toLong(arg2);
			else if (arg2 instanceof Float)
				return toFloat(arg1) == toFloat(arg2);
			else if (arg2 instanceof Double)
				return toDouble(arg1) == toDouble(arg2);
			else if (arg2 instanceof BigInteger)
				return toBigInteger(toInt(arg1)).equals(arg2);
			else if (arg2 instanceof BigDecimal)
				// We don't want {@code equals}, as this would require the same scale on both numbers for them to be equal
				return new BigDecimal(toDouble(arg1)).compareTo((BigDecimal)arg2) == 0;
			else
				return false;
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return toLong(arg1) == toLong(arg2);
			else if (arg2 instanceof Float)
				return toFloat(arg1) == toFloat(arg2);
			else if (arg2 instanceof Double)
				return toDouble(arg1) == toDouble(arg2);
			else if (arg2 instanceof BigInteger)
				return toBigInteger(toLong(arg1)).equals(arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(toDouble(arg1)).compareTo((BigDecimal)arg2) == 0;
			else
				return false;
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return toFloat(arg1) == toFloat(arg2);
			else if (arg2 instanceof Double)
				return toDouble(arg1) == (((Double)arg2).doubleValue());
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(toDouble(arg1)).compareTo(new BigDecimal((BigInteger)arg2)) == 0;
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(toDouble(arg1)).compareTo((BigDecimal)arg2) == 0;
			else
				return false;
		}
		else if (arg1 instanceof Double)
		{
			double value1 = (((Double)arg1).doubleValue());
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return value1 == toDouble(arg2);
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(value1).compareTo(new BigDecimal((BigInteger)arg2)) == 0;
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(value1).compareTo((BigDecimal)arg2) == 0;
		}
		else if (arg1 instanceof BigInteger)
		{
			BigInteger value1 = (BigInteger)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return value1.equals(toBigInteger(toInt(arg2)));
			else if (arg2 instanceof Long)
				return value1.equals(toBigInteger(toLong(arg2)));
			else if (arg2 instanceof Float)
				return new BigDecimal(value1).compareTo(new BigDecimal(((Float)arg2).doubleValue())) == 0;
			else if (arg2 instanceof Double)
				return new BigDecimal(value1).compareTo(new BigDecimal(((Double)arg2).doubleValue())) == 0;
			else if (arg2 instanceof BigInteger)
				return value1.equals((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(value1).compareTo((BigDecimal)arg2) == 0;
		}
		else if (arg1 instanceof BigDecimal)
		{
			BigDecimal value1 = (BigDecimal)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return value1.compareTo(new BigDecimal(Integer.toString(toInt(arg2)))) == 0;
			else if (arg2 instanceof Long)
				return value1.compareTo(new BigDecimal(Long.toString(toLong(arg2)))) == 0;
			else if (arg2 instanceof Float)
				return value1.compareTo(new BigDecimal(((Float)arg2).doubleValue())) == 0;
			else if (arg2 instanceof Double)
				return value1.compareTo(new BigDecimal(((Double)arg2).doubleValue())) == 0;
			else if (arg2 instanceof BigInteger)
				return value1.compareTo(new BigDecimal((BigInteger)arg2)) == 0;
			else if (arg2 instanceof BigDecimal)
				return value1.compareTo((BigDecimal)arg2) == 0;
		}
		else if (arg1 instanceof List)
		{
			if (arg1 == arg2)
				return true;
			if (!(arg2 instanceof List))
				return false;

			ListIterator iter1 = ((List)arg1).listIterator();
			ListIterator iter2 = ((List)arg2).listIterator();
			while (iter1.hasNext() && iter2.hasNext())
			{
				Object item1 = iter1.next();
				Object item2 = iter2.next();
				if (!eq(item1, item2))
					return false;
			}
			return !(iter1.hasNext() || iter2.hasNext());
		}
		else if (arg1 instanceof Map)
		{
			if (arg1 == arg2)
				return true;
			if (!(arg2 instanceof Map))
				return false;

			Map map1 = (Map)arg1;
			Map map2 = (Map)arg2;

			if (map1.size() != map2.size())
				return false;

			Iterator<Map.Entry> iter = map1.entrySet().iterator();
			while (iter.hasNext())
			{
				Map.Entry entry = iter.next();
				Object key = entry.getKey();
				Object value = entry.getValue();
				if (value == null)
				{
					if (!(map2.get(key) == null && map2.containsKey(key)))
						return false;
				}
				else
				{
					if (!Utils.eq(value, map2.get(key)))
						return false;
				}
			}

			return true;
		}
		return arg1.equals(arg2);
	}

	public static Iterator iterator(Object obj)
	{
		if (obj instanceof String)
			return new StringIterator((String)obj);
		else if (obj instanceof Iterable)
			return ((Iterable)obj).iterator();
		else if (obj instanceof Map)
			return ((Map)obj).keySet().iterator();
		else if (obj instanceof Object[])
			return Arrays.asList((Object[])obj).iterator();
		else if (obj instanceof Iterator)
			return (Iterator)obj;
		throw new NotIterableException(obj);
	}

	public static DateTimeFormatter isoDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
	public static DateTimeFormatter isoDateTime0Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'", Locale.US);
	public static DateTimeFormatter isoDateTime1Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.US);
	public static DateTimeFormatter isoDateTime2Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
	public static DateTimeFormatter isoDateTime3Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.US);

	public static LocalDate isoParseDate(String format)
	{
		return LocalDate.parse(format, isoDateFormatter);
	}

	public static LocalDateTime isoParseDateTime(String format)
	{
		int length = format.length();
		if (length == 11)
			return LocalDate.parse(format, isoDateTime0Formatter).atStartOfDay();
		else if (length == 16)
			return LocalDateTime.parse(format, isoDateTime1Formatter);
		else if (length == 19)
			return LocalDateTime.parse(format, isoDateTime2Formatter);
		else if (length == 26)
			return LocalDateTime.parse(format, isoDateTime3Formatter);
		else
			throw new RuntimeException("can't parse " + FunctionRepr.call(format));
	}

	public static int getSliceStartPos(int sequenceSize, int virtualPos)
	{
		int retVal = virtualPos;
		if (0 > retVal)
		{
			retVal += sequenceSize;
		}
		if (0 > retVal)
		{
			retVal = 0;
		}
		else if (sequenceSize < retVal)
		{
			retVal = sequenceSize;
		}
		return retVal;
	}

	public static int getSliceEndPos(int sequenceSize, int virtualPos)
	{
		int retVal = virtualPos;
		if (0 > retVal)
		{
			retVal += sequenceSize;
		}
		if (0 > retVal)
		{
			retVal = 0;
		}
		else if (sequenceSize < retVal)
		{
			retVal = sequenceSize;
		}
		return retVal;
	}

	private static String savesubstr(String string, int start, int stop)
	{
		if (start > string.length())
			start = string.length();
		if (stop > string.length())
			stop = string.length();
		return string.substring(start, stop);
	}

	public static int indexOf(String haystack, int fromIndex, String... needles)
	{
		int bestPos = -1;

		for (int i = 0; i < needles.length; ++i)
		{
			int pos = haystack.indexOf(needles[i], fromIndex);
			if (pos != -1 && (bestPos == -1 || pos < bestPos))
				bestPos = pos;
		}
		return bestPos;
	}

	/**
	Can be used to convert an array into a real list.
	**/
	public static List array2List(Object[] array)
	{
		return new ArrayList(Arrays.asList(array));
	}

	public static void checkZeroDivisorBoolean(Boolean value)
	{
		if (!value.booleanValue())
			throw new ArithmeticException("division by zero");
	}

	public static void checkZeroDivisorInteger(Number value)
	{
		if (((Number)value).longValue() == 0)
			throw new ArithmeticException("division by zero");
	}

	public static void checkZeroDivisorFloat(Number value)
	{
		if (((Number)value).doubleValue() == 0)
			throw new ArithmeticException("division by zero");
	}

	public static void checkZeroDivisorBigInteger(BigInteger value)
	{
		if (value.compareTo(BigInteger.ZERO) == 0)
			throw new ArithmeticException("division by zero");
	}

	public static void checkZeroDivisorBigDecimal(BigDecimal value)
	{
		if (value.compareTo(BigDecimal.ZERO) == 0)
			throw new ArithmeticException("division by zero");
	}

	/**
	Format a message replacing placeholders of the form {}, {!r}, {!r} or
	{0}, {0!r}, {0!t}.
	**/
	public static String formatMessage(String template, Object... args)
	{
		StringBuilder buffer = new StringBuilder();
		int argIndex = 0;

		StringTokenizer tokenizer = new StringTokenizer(template, "{}", true);

		boolean outsidePlaceholder = true;

		int form = 0;
		String position = null;
		while (tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();
			if ("{".equals(token))
			{
				outsidePlaceholder = false;
				form = 0;
				token = null;
			}
			else if ("}".equals(token))
			{
				Object arg = args[position == null || position.length() == 0 ? argIndex++ : Integer.parseInt(position)];
				switch (form)
				{
					case 0:
						buffer.append(arg != null ? arg.toString() : "null");
						break;
					case 1:
						String repr = FunctionRepr.call(arg);
						if (repr.length() <= 300)
							buffer.append(repr);
						else
						{
							buffer.append(objectType(arg));
							buffer.append(" instance");
						}
						break;
					case 2:
						buffer.append(FunctionRepr.call(arg));
						break;
					case 3:
						buffer.append(objectType(arg));
						break;
				}
				outsidePlaceholder = true;
			}
			else if (outsidePlaceholder)
				buffer.append(token);
			else
			{
				if (token.endsWith("!r"))
				{
					form = 1;
					position = token.substring(0, token.length() - 2);
				}
				else if (token.endsWith("!R"))
				{
					form = 2;
					position = token.substring(0, token.length() - 2);
				}
				else if (token.endsWith("!t"))
				{
					form = 3;
					position = token.substring(0, token.length() - 2);
				}
				else
				{
					form = 0;
					position = token;
				}
			}
		}
		return buffer.toString();
	}

	public static String unescapeUL4String(String string)
	{
		if (string == null)
			return null;
		StringBuilder output = new StringBuilder(string.length());
		for (int i = 0; i < string.length();)
		{
			char c = string.charAt(i);
			if (c != '\\' || i == string.length()-1)
			{
				output.append(c);
				++i;
			}
			else
			{
				char c2 = string.charAt(++i);
				switch (c2)
				{
					case '\\':
						output.append('\\');
						++i; // already skipped the '/'
						break;
					case 'n':
						output.append('\n');
						++i; // already skipped the '/'
						break;
					case 'r':
						output.append('\r');
						++i; // already skipped the '/'
						break;
					case 't':
						output.append('\t');
						++i; // already skipped the '/'
						break;
					case 'f':
						output.append('\f');
						++i; // already skipped the '/'
						break;
					case 'b':
						output.append('\b');
						++i; // already skipped the '/'
						break;
					case 'a':
						output.append('\u0007');
						++i; // already skipped the '/'
						break;
					case '"':
						output.append('"');
						++i; // already skipped the '/'
						break;
					case '\'':
						output.append('\'');
						++i; // already skipped the '/'
						break;
					case 'x':
						int cx;
						try
						{
							cx = Integer.parseInt(string.substring(i+1, i+3), 16);
						}
						catch (NumberFormatException|IndexOutOfBoundsException ex)
						{
							throw new StringFormatException("illegal \\x escape: " + FunctionRepr.call(savesubstr(string, i+1, i+3)), ex);
						}
						output.append((char)cx);
						i += 3; // already skipped the '/'
						break;
					case 'u':
						int cu;
						try
						{
							cu = Integer.parseInt(string.substring(i+1, i+5), 16);
						}
						catch (NumberFormatException|IndexOutOfBoundsException ex)
						{
							throw new StringFormatException("illegal \\u escape: " + FunctionRepr.call(savesubstr(string, i+1, i+5)), ex);
						}
						output.append((char)cu);
						i += 5; // already skipped the '/'
						break;
					case 'U':
						throw new RuntimeException("\\U escapes are not supported");
					default:
						output.append(c);
						output.append(c2);
						++i; // already skipped the '/'
				}
			}
		}
		return output.toString();
	}

	public static Object parseUL4Int(String string)
	{
		boolean neg = false;
		int base = 10;
		Object value;
		if (string.charAt(0) == '-')
		{
			neg = true;
			string = string.substring(1);
		}
		if (string.startsWith("0x") || string.startsWith("0X"))
		{
			string = string.substring(2);
			base = 16;
		}
		else if (string.startsWith("0o") || string.startsWith("0O"))
		{
			string = string.substring(2);
			base = 8;
		}
		else if (string.startsWith("0b") || string.startsWith("0B"))
		{
			string = string.substring(2);
			base = 2;
		}
		if (neg)
			string = "-" + string;
		try
		{
			return Integer.parseInt(string, base);
		}
		catch (NumberFormatException ex1)
		{
			try
			{
				return Long.parseLong(string, base);
			}
			catch (NumberFormatException ex2)
			{
				return new BigInteger(string, base);
			}
		}
	}

	public static void formatVarname(StringBuilder buffer, Object varname)
	{
		if (varname instanceof String)
			buffer.append((String)varname);
		else
		{
			List varnames = (List)varname;
			buffer.append("(");
			int count = 0;
			for (Object subvarname : varnames)
			{
				++count;
				formatVarname(buffer, subvarname);
				if (count == 1 || count != varnames.size())
					buffer.append(", ");
			}
			buffer.append(")");
		}
	}

	public static class LValueValue
	{
		private final LValue lvalue;
		private final Object value;

		public LValueValue(LValue lvalue, Object value)
		{
			this.lvalue = lvalue;
			this.value = value;
		}

		public LValue getLValue()
		{
			return lvalue;
		}

		public Object getValue()
		{
			return value;
		}
	}

	public static List<LValueValue> unpackVariable(Object lvalue, Object item)
	{
		List<LValueValue> result = new ArrayList<LValueValue>();

		if (lvalue instanceof LValue)
		{
			result.add(new LValueValue((LValue)lvalue, item));
		}
		else
		{
			Iterator<Object> itemIter = Utils.iterator(item);
			List lvalues = (List)lvalue;
			int lvalueCount = lvalues.size();

			for (int i = 0;;++i)
			{
				if (itemIter.hasNext())
				{
					if (i < lvalueCount)
					{
						result.addAll(unpackVariable(lvalues.get(i), itemIter.next()));
					}
					else
					{
						throw new UnpackingException("mismatched for loop unpacking: " + lvalueCount + " varnames, >" + i + " items");
					}
				}
				else
				{
					if (i < lvalueCount)
					{
						throw new UnpackingException("mismatched for loop unpacking: " + lvalueCount + "+ varnames, " + i + " items");
					}
					else
					{
						break;
					}
				}
			}
		}
		return result;
	}

	public static Throwable getInnerException(Throwable ex)
	{
		Throwable[] suppressed = ex.getSuppressed();

		// Try to find a {@code LocationException}
		for (Throwable t : suppressed)
		{
			if (t instanceof LocationException)
				return t;
		}
		Throwable cause = ex.getCause();
		if (cause instanceof LocationException)
			return cause;
		// otherwise take the next best one
		if (suppressed.length > 0)
			return suppressed[0];
		return cause;
	}

	public static Throwable findInnermostException(Throwable ex)
	{
		while (true)
		{
			Throwable innerEx = getInnerException(ex);
			if (innerEx == null)
				return ex;
			ex = innerEx;
		}
	}

	/**
	Return a string that can be used for fingerprinting the exception location
	in Sentry.

	If the exception was caused by an UL4 template, use the location of the
	innermost exception as the fingerprint. Else use the stack trace itself.

	@param ex The exception for which we need a fingerprint.
	@return A string that identifies the location of the exception.
	**/
	public static String getLocationFingerprint(Throwable ex)
	{
		Throwable innerEx = getInnerException(ex);
		if (innerEx instanceof LocationException)
			return ((LocationException)innerEx).getDescription();
		else
			return getStacktraceAsText(ex, 100, "more stack frames");
	}

	public static String getLocationDescriptionAsText(Throwable ex)
	{
		Throwable innerEx = getInnerException(ex);
		if (innerEx instanceof LocationException)
			return ((LocationException)innerEx).getDescription();
		else
		{
			StackTraceElement[] frames = ex.getStackTrace();
			if (frames.length == 0)
				return ex.toString();
			return frames[0].toString();
		}
	}

	public static String getSourcePrefix(String source, int pos)
	{
		int outerStartPos = pos;
		int innerStartPos = outerStartPos;
		int maxPrefix = 40;
		boolean found = false; // Have we found a natural stopping position?
		while (maxPrefix > 0)
		{
			// We arrived at the start of the source code
			if (outerStartPos == 0)
			{
				found = true;
				break;
			}
			// We arrived at the start of the line
			if (source.charAt(outerStartPos-1) == '\n')
			{
				found = true;
				break;
			}
			--maxPrefix;
			--outerStartPos;
		}
		String result = source.substring(outerStartPos, innerStartPos);
		if (!found)
			result = "\u2026" + result;
		return result;
	}

	public static String getSourceSuffix(String source, int pos)
	{
		int outerStopPos = pos;
		int innerStopPos = outerStopPos;
		int maxSuffix = 40;
		boolean found = false; // Have we found a natural stopping position?
		while (maxSuffix > 0)
		{
			// We arrived at the end of the source code
			if (outerStopPos >= source.length())
			{
				found = true;
				break;
			}
			// We arrived at the end of the line
			if (source.charAt(outerStopPos) == '\n')
			{
				found = true;
				break;
			}
			--maxSuffix;
			++outerStopPos;
		}
		String result = source.substring(innerStopPos, outerStopPos);
		if (!found)
			result += "\u2026";
		return result;
	}

	public static Map<String, Object> exceptionAsMap(Throwable t)
	{
		Map<String, Object> result = makeMap(
			"type", t.getClass().getName(),
			"message", t.getLocalizedMessage()
		);
		if (t instanceof LocationException)
		{
			AST ast = ((LocationException)t).getLocation();
			result.put("pos", asList(ast.getPos().getStart(), ast.getPos().getStop()));
			result.put("line", ast.getStartLine());
			result.put("col", ast.getStartCol());
			result.put("source", ast.getStartSource());
			result.put("sourceprefix", ast.getStartSourcePrefix());
			result.put("sourcesuffix", ast.getStartSourceSuffix());
			List<String> names = new ArrayList<String>();
			Template template = ast.getTemplate();
			while (template != null)
			{
				names.add(template.getFullNameUL4());
				template = template.getParentTemplate();
			}
			result.put("names", names);
		}
		return result;
	}

	private static void addChainedExceptionToList(Throwable t, ArrayList<Map<String, Object>> chain)
	{
		Throwable inner = getInnerException(t);
		if (inner != null)
			addChainedExceptionToList(inner, chain);
		chain.add(exceptionAsMap(t));
	}

	public static List<Map<String, Object>> getExceptionChainAsList(Throwable t)
	{
		ArrayList<Map<String, Object>> chain = new ArrayList<Map<String, Object>>();
		addChainedExceptionToList(t, chain);
		return chain;
	}

	public static String getExceptionChainAsJSON(Throwable t)
	{
		List<Map<String, Object>> chain = getExceptionChainAsList(t);
		try (EvaluationContext context = new EvaluationContext())
		{
			return FunctionAsJSON.call(context, chain);
		}
	}

	private static void addExceptionText2Buffer(StringBuilder buffer, Throwable t)
	{
		Throwable inner = getInnerException(t);
		if (inner != null)
		{
			addExceptionText2Buffer(buffer, inner);
			buffer.append("\n\n");
		}
		buffer.append(t.toString());
	}

	public static String getExceptionChainAsText(Throwable t)
	{
		StringBuilder buffer = new StringBuilder();
		addExceptionText2Buffer(buffer, t);
		return buffer.toString();
	}

	private static void addExceptionMarkdown2Buffer(StringBuilder buffer, Throwable t)
	{
		Throwable inner = getInnerException(t);
		if (inner != null)
		{
			addExceptionMarkdown2Buffer(buffer, inner);
			buffer.append("\n");
		}

		buffer.append("`");
		buffer.append(t.getClass().getName());
		buffer.append("`: ");

		if (t instanceof LocationException)
		{
			AST location = ((LocationException)t).getLocation();
			buffer.append(location.getTemplateDescriptionMarkdown());
			buffer.append(": ");
			buffer.append(location.getLocationDescriptionMarkdown());
			buffer.append("\n");
			buffer.append(location.getSourceSnippetMarkdown());
		}
		else
		{
			buffer.append(t.getMessage());
		}
	}

	public static String getExceptionChainAsMarkdown(Throwable t)
	{
		StringBuilder buffer = new StringBuilder();
		addExceptionMarkdown2Buffer(buffer, t);
		return buffer.toString();
	}

	private static void addExceptionHTML2Buffer(StringBuilder buffer, Throwable t)
	{
		Throwable inner = getInnerException(t);
		if (inner != null)
		{
			addExceptionHTML2Buffer(buffer, inner);
		}
		if (t instanceof LocationException)
		{
			AST ast = ((LocationException)t).getLocation();
			buffer.append("<p class='ul4-location'>");
			buffer.append(ast.getTemplateDescriptionHTML());
			buffer.append(": ");
			buffer.append(ast.getLocationDescriptionHTML());
			buffer.append("</p>");
			buffer.append("<pre>");
			buffer.append(ast.getSourceSnippetHTML());
			buffer.append("</pre>");
		}
		else
		{
			buffer.append("<p class='ul4-exc'>");
			buffer.append("<b>");
			buffer.append(FunctionXMLEscape.call(t.getClass().getName()));
			buffer.append("</b>: ");
			buffer.append(FunctionXMLEscape.call(t.getLocalizedMessage()));
			buffer.append("</p>");
		}
	}

	public static String getExceptionChainAsHTML(Throwable t)
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("<div class='ul4-error'>");
		addExceptionHTML2Buffer(buffer, t);
		buffer.append("</div>");
		return buffer.toString();
	}

	public static String getStacktraceAsText(Throwable t, int maxLines, String overflowMessage)
	{
		try (StringWriter stringWriter = new StringWriter())
		{
			try (PrintWriter printWriter = new PrintWriter(stringWriter))
			{
				t.printStackTrace(printWriter);
				String stackTrace = stringWriter.toString();
				if (maxLines != 0)
				{
					int pos = StringUtils.ordinalIndexOf(stackTrace, "\n", maxLines);
					if (pos >= 0)
						stackTrace = stackTrace.substring(0, pos+1) + "...\n" + overflowMessage;
				}
				return stackTrace;
			}
		}
		catch (IOException exc)
		{
			throw new RuntimeException(exc);
		}
	}

	public static String getStacktraceAsText(Throwable t)
	{
		return getStacktraceAsText(t, 0, null);
	}

	public static String getStacktraceAsMarkdown(Throwable t, int maxLines, String overflowMessage)
	{
		try (StringWriter stringWriter = new StringWriter())
		{
			try (PrintWriter printWriter = new PrintWriter(stringWriter))
			{
				t.printStackTrace(printWriter);
				String stackTrace = stringWriter.toString();

				int pos = -1;
				if (maxLines != 0)
				{
					pos = StringUtils.ordinalIndexOf(stackTrace, "\n", maxLines);
					if (pos >= 0)
						stackTrace = stackTrace.substring(0, pos+1);
				}

				StringBuilder buffer = new StringBuilder(8 + stackTrace.length());
				buffer.append("```\n");
				// This is not ideal, because it tampers with the stacktrace,
				// but better a (slightly) wrong stacktrace than a broken
				// Markdown markup (Also there are probably never any backticks
				// in the stacktrace anyway).
				buffer.append(stackTrace.replace("```", "'''"));
				if (!stackTrace.endsWith("\n"))
					buffer.append("\n");
				if (pos >= 0)
					buffer.append("...\n");
				buffer.append("```\n");
				if (pos >= 0)
					buffer.append(overflowMessage);
				return buffer.toString();
			}
		}
		catch (IOException exc)
		{
			throw new RuntimeException(exc);
		}
	}

	public static String getStacktraceAsMarkdown(Throwable t)
	{
		return getStacktraceAsMarkdown(t, 0, null);
	}
}
