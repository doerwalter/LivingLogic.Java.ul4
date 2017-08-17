/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;


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

	public static int toInt(Object arg)
	{
		if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1 : 0;
		else if (arg instanceof Number)
			return ((Number)arg).intValue();
		throw new UnsupportedOperationException("can't convert " + objectType(arg) + " to int!");
	}

	public static long toLong(Object arg)
	{
		if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1L : 0L;
		else if (arg instanceof Number)
			return ((Number)arg).longValue();
		throw new UnsupportedOperationException("can't convert " + objectType(arg) + " to long!");
	}

	public static float toFloat(Object arg)
	{
		if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1.0f : 0.0f;
		else if (arg instanceof Number)
			return ((Number)arg).floatValue();
		throw new UnsupportedOperationException("can't convert " + objectType(arg) + " to float!");
	}

	public static double toDouble(Object arg)
	{
		if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1.0d : 0.0d;
		else if (arg instanceof Number)
			return ((Number)arg).doubleValue();
		throw new UnsupportedOperationException("can't convert " + objectType(arg) + " to double!");
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
				return cmp(value1, new BigDecimal(Integer.toString(toInt(arg2))));
			else if (arg2 instanceof Long)
				return cmp(value1, new BigDecimal(Long.toString(toLong(arg2))));
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
				return new BigDecimal(toDouble(arg1)).compareTo((BigDecimal)arg2) == 0; // We don't want {@code equals}, as this would required the same scale on both numbers, for them to be equal
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

	public static SimpleDateFormat isoDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat isoDateTime1Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
	public static SimpleDateFormat isoDateTime2Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public static SimpleDateFormat isoTimestampFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	public static SimpleDateFormat isoTimestampMicroFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'000'");

	public static Date isoparse(String format)
	{
		try
		{
			int length = format.length();
			if (length == 10)
				return isoDateFormatter.parse(format);
			else if (length == 11)
				return isoDateFormatter.parse(format.substring(0, 10)); // ignore the trailing 'T'
			else if (length == 16)
				return isoDateTime1Formatter.parse(format);
			else if (length == 19)
				return isoDateTime2Formatter.parse(format);
			else
			{
				if (length > 23)
					format = format.substring(0, 23); // ignore last three digits
				return isoTimestampFormatter.parse(format);
			}
		}
		catch (java.text.ParseException ex) // can not happen when reading from the binary format
		{
			throw new RuntimeException("can't parse " + FunctionRepr.call(format));
		}
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
	 * Can be used to convert an array into a real list.
	 */
	public static List array2List(Object[] array)
	{
		return new ArrayList(Arrays.asList(array));
	}

	public static String formatMessage(String template, Object... args)
	{
		StringBuilder buffer = new StringBuilder();
		int argIndex = 0;

		for (int fromIndex = 0;;)
		{
			int index = indexOf(template, fromIndex, "{}", "{!r}", "{!t}");

			if (index == -1)
			{
				buffer.append(template.substring(fromIndex));
				break;
			}
			if (index != fromIndex)
				buffer.append(template.substring(fromIndex, index));
			if (template.startsWith("{}", index))
			{
				Object arg = args[argIndex++];
				buffer.append(arg != null ? arg.toString() : "null");
				fromIndex = index + 2;
			}
			else if (template.startsWith("{!r}", index))
			{
				buffer.append(FunctionRepr.call(args[argIndex++]));
				fromIndex = index + 4;
			}
			else /* {!t} */
			{
				buffer.append(objectType(args[argIndex++]));
				fromIndex = index + 4;
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
}
