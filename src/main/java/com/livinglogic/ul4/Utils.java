/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;
import java.util.Set;
import java.util.Date;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.MathContext;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URLDecoder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;


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
		return (obj != null) ? obj.getClass().toString().substring(6) : "null";
	}

	public static Object neg(Object arg)
	{
		if (arg instanceof Integer)
		{
			int value = ((Integer)arg).intValue();
			if (value == -0x80000000) // Prevent overflow by switching to long
				return 0x80000000L;
			else
				return -value;
		}
		else if (arg instanceof Long)
		{
			long value = ((Long)arg).longValue();
			if (value == -0x8000000000000000L) // Prevent overflow by switching to BigInteger
				return new BigInteger("8000000000000000", 16);
			else
				return -value;
		}
		else if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? -1 : 0;
		else if (arg instanceof Short || arg instanceof Byte || arg instanceof Boolean)
			return -((Number)arg).intValue();
		else if (arg instanceof Float)
			return -((Float)arg).floatValue();
		else if (arg instanceof Double)
			return -((Double)arg).doubleValue();
		else if (arg instanceof BigInteger)
			return ((BigInteger)arg).negate();
		else if (arg instanceof BigDecimal)
			return ((BigDecimal)arg).negate();
		throw new UnsupportedOperationException("-" + objectType(arg) + " not supported!");
	}

	private static BigInteger _toBigInteger(int arg)
	{
		return new BigInteger(Integer.toString(arg));
	}

	private static BigInteger _toBigInteger(long arg)
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

	public static Object add(int arg1, int arg2)
	{
		int result = arg1 + arg2;
		if ((arg1 >= 0) != (arg2 >= 0)) // arguments have different sign, so there can be no overflow
			return result;
		else if ((arg1 >= 0) == (result >= 0)) // result didn't change sign, so there was no overflow
			return result;
		else // we had an overflow => promote to BigInteger
			return _toBigInteger(arg1).add(_toBigInteger(arg2));
	}

	public static Object add(long arg1, long arg2)
	{
		long result = arg1 + arg2;
		if ((arg1 >= 0) != (arg2 >= 0)) // arguments have different sign, so there can be no overflow
			return result;
		else if ((arg1 >= 0) == (result >= 0)) // result didn't change sign, so there was no overflow
			return result;
		else // we had an overflow => promote to BigInteger
			return _toBigInteger(arg1).add(_toBigInteger(arg2));
	}

	public static Object add(float arg1, float arg2)
	{
		return arg1 + arg2;
	}

	public static Object add(double arg1, double arg2)
	{
		return arg1 + arg2;
	}

	public static Object add(String arg1, String arg2)
	{
		return arg1 + arg2;
	}

	public static Object add(Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return add(toInt(arg1), toInt(arg2));
			else if (arg2 instanceof Long)
				return add(toLong(arg1), toLong(arg2));
			else if (arg2 instanceof Float)
				return add(toFloat(arg1), toFloat(arg2));
			else if (arg2 instanceof Double)
				return add(toDouble(arg1), toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg2).add(_toBigInteger(toInt(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).add(new BigDecimal(toDouble(arg1)));
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return add(toLong(arg1), toLong(arg2));
			else if (arg2 instanceof Float)
				return add(toFloat(arg1), toFloat(arg2));
			else if (arg2 instanceof Double)
				return add(toDouble(arg1), toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg2).add(_toBigInteger(toLong(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).add(new BigDecimal(toDouble(arg1)));
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return add(toFloat(arg1), toFloat(arg2));
			else if (arg2 instanceof Double)
				return add(toDouble(arg1), (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg2).add(new BigDecimal(toDouble(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).add(new BigDecimal(toDouble(arg1)));
		}
		else if (arg1 instanceof Double)
		{
			double value1 = (((Double)arg1).doubleValue());
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return add(value1, toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg2).add(new BigDecimal(value1));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).add(new BigDecimal(value1));
		}
		else if (arg1 instanceof BigInteger)
		{
			BigInteger value1 = (BigInteger)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return value1.add(_toBigInteger(toInt(arg2)));
			else if (arg2 instanceof Long)
				return value1.add(_toBigInteger(toLong(arg2)));
			else if (arg2 instanceof Float)
				return new BigDecimal(value1).add(new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return new BigDecimal(value1).add(new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return value1.add((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).add(new BigDecimal(value1));
		}
		else if (arg1 instanceof BigDecimal)
		{
			BigDecimal value1 = (BigDecimal)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return value1.add(new BigDecimal(Integer.toString(toInt(arg2))));
			else if (arg2 instanceof Long)
				return value1.add(new BigDecimal(Long.toString(toLong(arg2))));
			else if (arg2 instanceof Float)
				return value1.add(new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return value1.add(new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return value1.add(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return value1.add((BigDecimal)arg2);
		}
		else if (arg1 instanceof String && arg2 instanceof String)
			return add((String)arg1, (String)arg2);
		throw new UnsupportedOperationException(objectType(arg1) + " + " + objectType(arg2) + " not supported!");
	}

	public static Object sub(int arg1, int arg2)
	{
		int result = arg1 - arg2;
		if ((arg1 >= 0) == (arg2 >= 0)) // arguments have same sign, so there can be no overflow
			return result;
		else if ((arg1 >= 0) == (result >= 0)) // result didn't change sign, so there was no overflow
			return result;
		else // we had an overflow => promote to BigInteger
			return _toBigInteger(arg1).add(_toBigInteger(arg2).negate());
	}

	public static Object sub(long arg1, long arg2)
	{
		long result = arg1 - arg2;
		if ((arg1 >= 0) == (arg2 >= 0)) // arguments have same sign, so there can be no overflow
			return result;
		else if ((arg1 >= 0) == (result >= 0)) // result didn't change sign, so there was no overflow
			return result;
		else // we had an overflow => promote to BigInteger
			return _toBigInteger(arg1).add(_toBigInteger(arg2).negate());
	}

	public static Object sub(float arg1, float arg2)
	{
		return arg1 - arg2;
	}

	public static Object sub(double arg1, double arg2)
	{
		return arg1 - arg2;
	}

	public static Object sub(Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return sub(toInt(arg1), toInt(arg2));
			else if (arg2 instanceof Long)
				return sub(toLong(arg1), ((Long)arg2).longValue());
			else if (arg2 instanceof Float)
				return sub(toFloat(arg1), ((Float)arg2).floatValue());
			else if (arg2 instanceof Double)
				return sub(toDouble(arg1), ((Double)arg2).doubleValue());
			else if (arg2 instanceof BigInteger)
				return _toBigInteger(toInt(arg1)).subtract((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(toDouble(arg1)).subtract((BigDecimal)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return sub(toLong(arg1), toLong(arg2));
			else if (arg2 instanceof Float)
				return sub(toFloat(arg1), ((Float)arg2).floatValue());
			else if (arg2 instanceof Double)
				return sub(toDouble(arg1), ((Double)arg2).doubleValue());
			else if (arg2 instanceof BigInteger)
				return _toBigInteger(toLong(arg1)).subtract((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(toDouble(arg1)).subtract((BigDecimal)arg2);
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return sub(toFloat(arg1), toFloat(arg2));
			else if (arg2 instanceof Double)
				return sub(toDouble(arg1), (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(toDouble(arg1)).subtract(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(toDouble(arg1)).subtract((BigDecimal)arg2);
		}
		else if (arg1 instanceof Double)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return sub(toDouble(arg1), toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(toDouble(arg1)).subtract(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(toDouble(arg1)).subtract((BigDecimal)arg2);
		}
		else if (arg1 instanceof BigInteger)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return ((BigInteger)arg1).subtract(_toBigInteger(toInt(arg2)));
			else if (arg2 instanceof Long)
				return ((BigInteger)arg1).subtract(_toBigInteger(toLong(arg2)));
			else if (arg2 instanceof Float)
				return new BigDecimal((BigInteger)arg1).subtract(new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return new BigDecimal((BigInteger)arg1).subtract(new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg1).subtract((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal((BigInteger)arg1).subtract((BigDecimal)arg2);
		}
		else if (arg1 instanceof BigDecimal)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return ((BigDecimal)arg1).subtract(new BigDecimal(toDouble(arg2)));
			else if (arg2 instanceof BigInteger)
				return ((BigDecimal)arg1).subtract(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg1).subtract((BigDecimal)arg2);
		}
		throw new UnsupportedOperationException(objectType(arg1) + " - " + objectType(arg2) + " not supported!");
	}

	public static Object mul(int arg1, String arg2)
	{
		return StringUtils.repeat(arg2, arg1);
	}

	public static Object mul(long arg1, String arg2)
	{
		if (((int)arg1) != arg1)
			throw new UnsupportedOperationException(objectType(arg1) + " * " + objectType(arg2) + " not supported!");
		return StringUtils.repeat(arg2, (int)arg1);
	}

	public static Object mul(int arg1, List arg2)
	{
		ArrayList result = new ArrayList();

		for (;arg1>0;--arg1)
			result.addAll(arg2);
		return result;
	}

	public static Object mul(long arg1, List arg2)
	{
		ArrayList result = new ArrayList();

		for (;arg1>0;--arg1)
			result.addAll(arg2);
		return result;
	}

	public static Object mul(int arg1, int arg2)
	{
		if (arg1 == 0 || arg2 == 0)
			return 0;
		int result = arg1 * arg2;
		if (result/arg1 == arg2) // result doesn't seem to have overflowed
			return result;
		else // we had an overflow => promote to BigInteger
			return _toBigInteger(arg1).multiply(_toBigInteger(arg2));
	}

	public static Object mul(long arg1, long arg2)
	{
		if (arg1 == 0 || arg2 == 0)
			return 0;
		long result = arg1 * arg2;
		if (result/arg1 == arg2) // result doesn't seem to have overflowed
			return result;
		else // we had an overflow => promote to BigInteger
			return _toBigInteger(arg1).multiply(_toBigInteger(arg2));
	}

	public static Object mul(float arg1, float arg2)
	{
		// FIXME: Overflow check
		return arg1 * arg2;
	}

	public static Object mul(double arg1, double arg2)
	{
		// FIXME: Overflow check
		return arg1 * arg2;
	}

	public static Object mul(Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return mul(toInt(arg1), toInt(arg2));
			else if (arg2 instanceof Long)
				return mul(toLong(arg1), ((Long)arg2).longValue());
			else if (arg2 instanceof Float)
				return mul(toFloat(arg1), ((Float)arg2).floatValue());
			else if (arg2 instanceof Double)
				return mul(toDouble(arg1), ((Double)arg2).doubleValue());
			else if (arg2 instanceof String)
				return mul(toInt(arg1), (String)arg2);
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg2).multiply(_toBigInteger(toInt(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal(toDouble(arg1)));
			else if (arg2 instanceof List)
				return mul(toInt(arg1), (List)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return mul(toLong(arg1), toLong(arg2));
			else if (arg2 instanceof Float)
				return mul(toFloat(arg1), ((Float)arg2).floatValue());
			else if (arg2 instanceof Double)
				return mul(toDouble(arg1), ((Double)arg2).doubleValue());
			else if (arg2 instanceof String)
				return mul(toInt(arg1), (String)arg2);
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg2).multiply(_toBigInteger(toLong(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal(toDouble(arg1)));
			else if (arg2 instanceof List)
				return mul(toLong(arg1), (List)arg2);
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return mul(toFloat(arg1), toFloat(arg2));
			else if (arg2 instanceof Double)
				return mul(toDouble(arg1), (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg2).multiply(new BigDecimal(toDouble(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal(toDouble(arg1)));
		}
		else if (arg1 instanceof Double)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return mul(toDouble(arg1), toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg2).multiply(new BigDecimal(toDouble(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal(toDouble(arg1)));
		}
		else if (arg1 instanceof BigInteger)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return ((BigInteger)arg1).multiply(_toBigInteger(toInt(arg2)));
			else if (arg2 instanceof Long)
				return ((BigInteger)arg1).multiply(_toBigInteger(toLong(arg2)));
			else if (arg2 instanceof Float)
				return new BigDecimal((BigInteger)arg1).multiply(new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return new BigDecimal((BigInteger)arg1).multiply(new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg1).multiply(((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal((BigInteger)arg1));
		}
		else if (arg1 instanceof BigDecimal)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return ((BigDecimal)arg1).multiply(new BigDecimal(toDouble(arg2)));
			else if (arg2 instanceof BigInteger)
				return ((BigDecimal)arg1).multiply(new BigDecimal(((BigInteger)arg2)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg1).multiply(((BigDecimal)arg2));
		}
		else if (arg1 instanceof String)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return mul(toInt(arg2), (String)arg1);
			else if (arg2 instanceof Long)
				return mul(toLong(arg2), (String)arg1);
		}
		else if (arg1 instanceof List)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return mul(toInt(arg2), (List)arg1);
			else if (arg2 instanceof Long)
				return mul(toLong(arg2), (List)arg1);
		}
		throw new UnsupportedOperationException(objectType(arg1) + " * " + objectType(arg2) + " not supported!");
	}

	public static Object truediv(Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Long || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean || arg1 instanceof Float || arg1 instanceof Double)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return toDouble(arg1) / toDouble(arg2);
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(toDouble(arg1)).divide(new BigDecimal((BigInteger)arg2), MathContext.DECIMAL128);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(toDouble(arg1)).divide((BigDecimal)arg2, MathContext.DECIMAL128);
		}
		else if (arg1 instanceof BigInteger)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return new BigDecimal((BigInteger)arg1).divide(new BigDecimal(toDouble(arg2)), MathContext.DECIMAL128);
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg1).divide(new BigDecimal((BigInteger)arg2), MathContext.DECIMAL128);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal((BigInteger)arg1).divide((BigDecimal)arg2, MathContext.DECIMAL128);
		}
		else if (arg1 instanceof BigDecimal)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return ((BigDecimal)arg1).divide(new BigDecimal(toDouble(arg2)), MathContext.DECIMAL128);
			else if (arg2 instanceof BigInteger)
				return ((BigDecimal)arg1).divide(new BigDecimal((BigInteger)arg2), MathContext.DECIMAL128);
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg1).divide((BigDecimal)arg2, MathContext.DECIMAL128);
		}
		throw new UnsupportedOperationException(objectType(arg1) + " / " + objectType(arg2) + " not supported!");
	}

	public static Object floordiv(Object arg1, Object arg2)
	{
		// integer division in UL4 is defined is rounding towards -infinity (as Python does)
		// since Java rounds towards 0, the following code compensates for that
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
			{
				int int1 = toInt(arg1);
				int int2 = toInt(arg2);
				if (int1 < 0)
				{
					if (int2 < 0)
						return  int1 / int2;
					else
						return  (int1 - int2 + 1) / int2;
				}
				else
				{
					if (int2 < 0)
						return  (int1 - int2 - 1) / int2;
					else
						return  int1 / int2;
				}
			}
			else if (arg2 instanceof Long)
			{
				int int1 = toInt(arg1);
				long long2 = toLong(arg2);
				if (int1 < 0)
				{
					if (long2 < 0)
						return  int1 / long2;
					else
						return  (int1 - long2 + 1) / long2;
				}
				else
				{
					if (long2 < 0)
						return  (int1 - long2 - 1) / long2;
					else
						return  int1 / long2;
				}
			}
			else if (arg2 instanceof Float)
				return Math.floor(toInt(arg1) / toFloat(arg2));
			else if (arg2 instanceof Double)
				return Math.floor(toInt(arg1) / toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return _toBigInteger(toInt(arg1)).divide((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(toDouble(arg1)).divideToIntegralValue((BigDecimal)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return toLong(arg1) / toLong(arg2);
			else if (arg2 instanceof Float)
				return Math.floor(toLong(arg1) / toFloat(arg2));
			else if (arg2 instanceof Double)
				return Math.floor(toLong(arg1) / toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return _toBigInteger(toLong(arg1)).divide((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(toDouble(arg1)).divideToIntegralValue((BigDecimal)arg2);
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return Math.floor(toFloat(arg1) / toFloat(arg2));
			else if (arg2 instanceof Double)
				return Math.floor(toDouble(arg1) / (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(toDouble(arg1)).divideToIntegralValue(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(toDouble(arg1)).divideToIntegralValue((BigDecimal)arg2);
		}
		else if (arg1 instanceof Double)
		{
			double value1 = (((Double)arg1).doubleValue());
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return Math.floor(value1 / toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(value1).divideToIntegralValue(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(value1).divideToIntegralValue((BigDecimal)arg2);
		}
		else if (arg1 instanceof BigInteger)
		{
			BigInteger value1 = (BigInteger)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return value1.divide(_toBigInteger(toInt(arg2)));
			else if (arg2 instanceof Long)
				return value1.divide(_toBigInteger(toLong(arg2)));
			else if (arg2 instanceof Float)
				return new BigDecimal(value1).divideToIntegralValue(new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return new BigDecimal(value1).divideToIntegralValue(new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return value1.divide((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(value1).divideToIntegralValue((BigDecimal)arg2);
		}
		else if (arg1 instanceof BigDecimal)
		{
			BigDecimal value1 = (BigDecimal)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return value1.divideToIntegralValue(new BigDecimal(Integer.toString(toInt(arg2))));
			else if (arg2 instanceof Long)
				return value1.divideToIntegralValue(new BigDecimal(Long.toString(toLong(arg2))));
			else if (arg2 instanceof Float)
				return value1.divideToIntegralValue(new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return value1.divideToIntegralValue(new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return value1.divideToIntegralValue(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return value1.divideToIntegralValue((BigDecimal)arg2);
		}
		throw new UnsupportedOperationException(objectType(arg1) + " // " + objectType(arg2) + " not supported!");
	}

	public static int mod(int arg1, int arg2)
	{
		int div = arg1 / arg2;
		int mod = arg1 - div * arg2;

		if (mod != 0 && ((arg2 < 0 && mod > 0) || (arg2 > 0 && mod < 0)))
		{
			mod += arg2;
			--div;
		}
		return arg1 - div * arg2;
	}

	public static long mod(long arg1, long arg2)
	{
		long div = arg1 / arg2;
		long mod = arg1 - div * arg2;

		if (mod != 0 && ((arg2 < 0 && mod > 0) || (arg2 > 0 && mod < 0)))
		{
			mod += arg2;
			--div;
		}
		return arg1 - div * arg2;
	}

	// No version public static float mod(float arg1, float arg2)

	public static double mod(double arg1, double arg2)
	{
		double div = Math.floor(arg1 / arg2);
		double mod = arg1 - div * arg2;

		if (mod != 0 && ((arg2 < 0 && mod > 0) || (arg2 > 0 && mod < 0)))
		{
			mod += arg2;
			--div;
		}
		return arg1 - div * arg2;
	}

	public static BigInteger mod(BigInteger arg1, BigInteger arg2)
	{
		return arg1.mod(arg2); // FIXME: negative numbers?
	}

	public static BigDecimal mod(BigDecimal arg1, BigDecimal arg2)
	{
		return arg1.remainder(arg2); // FIXME: negative numbers?
	}

	public static Object mod(Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return mod(toInt(arg1), toInt(arg2));
			else if (arg2 instanceof Long)
				return mod(toLong(arg1), toLong(arg2));
			else if (arg2 instanceof Float)
				return mod(toDouble(arg1), toDouble(arg2));
			else if (arg2 instanceof Double)
				return mod(toDouble(arg1), toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return mod(_toBigInteger(toInt(arg1)), (BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return mod(new BigDecimal(toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return mod(toLong(arg1), toLong(arg2));
			else if (arg2 instanceof Float)
				return mod(toDouble(arg1), toDouble(arg2));
			else if (arg2 instanceof Double)
				return mod(toDouble(arg1), toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return mod(_toBigInteger(toLong(arg1)), (BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return mod(new BigDecimal(toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return mod(toDouble(arg1), toDouble(arg2));
			else if (arg2 instanceof Double)
				return mod(toDouble(arg1), (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return mod(new BigDecimal(toDouble(arg1)), new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return mod(new BigDecimal(toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Double)
		{
			double value1 = (((Double)arg1).doubleValue());
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return mod(value1, toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return mod(new BigDecimal(value1), new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return mod(new BigDecimal(value1), ((BigDecimal)arg2));
		}
		else if (arg1 instanceof BigInteger)
		{
			BigInteger value1 = (BigInteger)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return mod(value1, _toBigInteger(toInt(arg2)));
			else if (arg2 instanceof Long)
				return mod(value1, _toBigInteger(toLong(arg2)));
			else if (arg2 instanceof Float)
				return mod(new BigDecimal(value1), new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return mod(new BigDecimal(value1), new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return mod(value1, (BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return mod(new BigDecimal(value1), (BigDecimal)arg2);
		}
		else if (arg1 instanceof BigDecimal)
		{
			BigDecimal value1 = (BigDecimal)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return mod(value1, new BigDecimal(Integer.toString(toInt(arg2))));
			else if (arg2 instanceof Long)
				return mod(value1, new BigDecimal(Long.toString(toLong(arg2))));
			else if (arg2 instanceof Float)
				return mod(value1, new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return mod(value1, new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return mod(value1, new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return mod(value1, (BigDecimal)arg2);
		}
		else if (arg1 instanceof Color && arg2 instanceof Color)
			return ((Color)arg1).blend((Color)arg2);
		throw new UnsupportedOperationException(objectType(arg1) + " % " + objectType(arg2) + " not supported!");
	}

	public static Object getItem(String arg1, Integer arg2)
	{
		int index = arg2.intValue();
		if (0 > index)
		{
			index += arg1.length();
		}
		return arg1.substring(index, index + 1);
	}

	public static Object getItem(List arg1, Integer arg2)
	{
		int index = arg2.intValue();
		if (0 > index)
		{
			index += arg1.size();
		}
		return arg1.get(index);
	}

	public static Object getItem(Color arg1, Integer arg2)
	{
		int index = arg2.intValue();
		switch (index)
		{
			case 0:
				return arg1.getR();
			case 1:
				return arg1.getG();
			case 2:
				return arg1.getB();
			case 3:
				return arg1.getA();
			default:
				throw new ArrayIndexOutOfBoundsException();
		}
	}

	public static Object getItem(Map arg1, Object arg2)
	{
		Object result = arg1.get(arg2);

		if ((result == null) && !arg1.containsKey(arg2))
			throw new KeyException(arg2);
		return result;
	}

	public static Object getItem(Object arg1, Object arg2)
	{
		if (arg1 instanceof Map)
			return getItem((Map)arg1, arg2);
		else if (arg2 instanceof Integer)
		{
			if (arg1 instanceof String)
				return getItem((String)arg1, (Integer)arg2);
			else if (arg1 instanceof List)
				return getItem((List)arg1, (Integer)arg2);
			else if (arg1 instanceof Color)
				return getItem((Color)arg1, (Integer)arg2);
		}
		throw new UnsupportedOperationException(objectType(arg1) + "[" + objectType(arg2) + "] not supported!");
	}

	private static int getSliceStartPos(int sequenceSize, int virtualPos)
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

	private static int getSliceEndPos(int sequenceSize, int virtualPos)
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

	public static Object getSlice(List arg1, int arg2, int arg3)
	{
		int size = arg1.size();
		int start = getSliceStartPos(size, arg2);
		int end = getSliceEndPos(size, arg3);
		if (end < start)
			end = start;
		return arg1.subList(start, end);
	}

	public static Object getSlice(String arg1, int arg2, int arg3)
	{
		int size = arg1.length();
		int start = getSliceStartPos(size, arg2);
		int end = getSliceEndPos(size, arg3);
		if (end < start)
			end = start;
		return StringUtils.substring(arg1, start, end);
	}

	public static Object getSlice(Object arg1, Object arg2, Object arg3)
	{
		if (arg1 instanceof List)
		{
			int start = arg2 != null ? toInt(arg2) : 0;
			int end = arg3 != null ? toInt(arg3) : ((List)arg1).size();
			return getSlice((List)arg1, start, end);
		}
		else if (arg1 instanceof String)
		{
			int start = arg2 != null ? toInt(arg2) : 0;
			int end = arg3 != null ? toInt(arg3) : ((String)arg1).length();
			return getSlice((String)arg1, start, end);
		}
		throw new UnsupportedOperationException(objectType(arg1) + "[" + objectType(arg2) + ":" + objectType(arg3) + "] not supported!");
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
				return cmp(_toBigInteger(toInt(arg1)), (BigInteger)arg2);
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
				return cmp(_toBigInteger(toLong(arg1)), (BigInteger)arg2);
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
				return cmp(value1, _toBigInteger(toInt(arg2)));
			else if (arg2 instanceof Long)
				return cmp(value1, _toBigInteger(toLong(arg2)));
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
		else if (arg1 instanceof String && arg2 instanceof String)
			return cmp((String)arg1, (String)arg2);
		throw new UnsupportedOperationException(objectType(arg1) + " " + op + " " + objectType(arg2) + " not supported!");
	}

	public static boolean eq(Object obj1, Object obj2)
	{
		if (null != obj1 && null != obj2)
			return cmp(obj1, obj2, "==") == 0;
		return (null == obj1) == (null == obj2);
	}

	public static boolean ne(Object obj1, Object obj2)
	{
		if (null != obj1 && null != obj2)
			return cmp(obj1, obj2, "!=") != 0;
		return (null == obj1) != (null == obj2);
	}

	public static boolean lt(Object obj1, Object obj2)
	{
		if (null != obj1 && null != obj2)
			return cmp(obj1, obj2, "<") < 0;
		if ((null == obj1) != (null == obj2))
			throw new UnsupportedOperationException(objectType(obj1) + " < " + objectType(obj2) + " not supported!");
		return false;
	}

	public static boolean le(Object obj1, Object obj2)
	{
		if (null != obj1 && null != obj2)
			return cmp(obj1, obj2, "<=") <= 0;
		if ((null == obj1) != (null == obj2))
			throw new UnsupportedOperationException(objectType(obj1) + " <= " + objectType(obj2) + " not supported!");
		return true;
	}

	public static boolean gt(Object obj1, Object obj2)
	{
		if (null != obj1 && null != obj2)
			return cmp(obj1, obj2, ">") > 0;
		if ((null == obj1) != (null == obj2))
			throw new UnsupportedOperationException(objectType(obj1) + " > " + objectType(obj2) + " not supported!");
		return false;
	}

	public static boolean ge(Object obj1, Object obj2)
	{
		if (null != obj1 && null != obj2)
			return cmp(obj1, obj2, ">=") >= 0;
		if ((null == obj1) != (null == obj2))
			throw new UnsupportedOperationException(objectType(obj1) + " >= " + objectType(obj2) + " not supported!");
		return true;
	}

	public static boolean contains(String obj, String container)
	{
		return container.indexOf(obj) >= 0;
	}

	public static boolean contains(Object obj, Collection container)
	{
		return container.contains(obj);
	}

	public static boolean contains(Object obj, Map container)
	{
		return container.containsKey(obj);
	}

	public static boolean contains(Object obj, Object container)
	{
		if (container instanceof String)
		{
			if (obj instanceof String)
				return contains((String)obj, (String)container);
		}
		else if (container instanceof Collection)
			return contains(obj, (Collection)container);
		else if (container instanceof Map)
			return contains(obj, (Map)container);
		throw new RuntimeException(objectType(obj) + " in " + objectType(container) + " not supported!");
	}

	public static boolean notcontains(Object obj, Object container)
	{
		return !contains(obj, container);
	}

	public static Iterator iterator(Object obj)
	{
		if (obj instanceof String)
			return new StringIterator((String)obj);
		else if (obj instanceof Iterable)
			return ((Iterable)obj).iterator();
		else if (obj instanceof Map)
			return ((Map)obj).keySet().iterator();
		else if (obj instanceof Iterator)
			return (Iterator)obj;
		throw new UnsupportedOperationException("iter(" + objectType(obj) + ") not supported!");
	}

	public static Date makeDate(int year, int month, int day, int hour, int minute, int second, int microsecond)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.set(year, month-1, day, hour, minute, second);
		calendar.set(Calendar.MILLISECOND, microsecond/1000);
		return calendar.getTime();
	}

	public static Date makeDate(int year, int month, int day, int hour, int minute, int second)
	{
		return makeDate(year, month, day, hour, minute, second, 0);
	}

	public static Date makeDate(int year, int month, int day)
	{
		return makeDate(year, month, day, 0, 0, 0, 0);
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

	public static Object renders(Object obj)
	{
		if (obj instanceof Template)
			return ((Template)obj).renders(null);
		throw new UnsupportedOperationException(objectType(obj) + ".renders() not supported!");
	}

	public static Object renders(Object obj, Object variables)
	{
		if (obj instanceof Template && variables instanceof Map)
			return ((Template)obj).renders((Map<String, Object>)variables);
		throw new UnsupportedOperationException(objectType(obj) + ".renders(" + objectType(obj) + ") not supported!");
	}

	public static String unescapeUL4String(String string)
	{
		if (string == null)
			return null;
		StringBuffer output = new StringBuffer(string.length());
		for (int i = 0; i < string.length(); ++i)
		{
			char c = string.charAt(i);
			if (c != '\\' || i == string.length()-1)
				output.append(c);
			else
			{
				char c2 = string.charAt(++i);
				switch (c2)
				{
					case '\\':
						output.append('\\');
						break;
					case 'n':
						output.append('\n');
						break;
					case 'r':
						output.append('\r');
						break;
					case 't':
						output.append('\t');
						break;
					case 'f':
						output.append('\f');
						break;
					case 'b':
						output.append('\b');
						break;
					case 'a':
						output.append('\u0007');
						break;
					case 'e':
						output.append('\u001b');
						break;
					case '"':
						output.append('"');
						break;
					case '\'':
						output.append('\'');
						break;
					case 'x':
						output.append((char)Integer.parseInt(string.substring(i+1, i+3), 16));
						i += 3;
						break;
					case 'u':
						output.append((char)Integer.parseInt(string.substring(i+1, i+5), 16));
						i += 5;
						break;
					case 'U':
						throw new RuntimeException("\\U escapes are not supported");
					default:
						output.append(c);
						output.append(c2);
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

	/**
	 * Compile Java source code of a Java class and return the class object
	 * @param source The source of the body of the class
	 * @param extendsSpec Name of base class (or null)
	 * @param implementsSpec Comma separated list of implemented interfaces (or null)
	 * @return The Class object that contains the compiled Java code
	 */
	public static Class compileToJava(String source, String extendsSpec, String implementsSpec) throws java.io.IOException
	{
		String dirname = System.getProperty("user.dir");
		File file = File.createTempFile("jav", ".java", new File(dirname));
		file.deleteOnExit(); // Set the file to delete on exit
		// Get the file name and extract a class name from it
		String filename = file.getName();
		String classname = filename.substring(0, filename.length()-5);

		PrintWriter out = new PrintWriter(new FileOutputStream(file));

		out.println("/* Created on " + new Date() + " */");
		out.println();
		out.print("public class " + classname);
		if (extendsSpec != null)
		{
			out.print(" extends ");
			out.print(extendsSpec);
		}
		if (implementsSpec != null)
		{
			out.print(" implements ");
			out.print(implementsSpec);
		}
		out.println();
		out.println("{");
		out.println(source);
		out.println("}");
		// Flush and close the stream
		out.flush();
		out.close();

		// This requires $JAVA_HOME/lib/tools.jar in the CLASSPATH
		com.sun.tools.javac.Main javac = new com.sun.tools.javac.Main();

		String[] args = new String[] {
			"-d", System.getProperty("user.dir"),
			filename
		};

		OutputStream nulloutput = new OutputStream() {
			public void write(int i) throws IOException {
				//do nothing
			}
		};

		// TODO add ul4.jar to java.class.path in a more generic way
		System.setProperty("java.class.path", "/Users/walter/.m2/repository/com/livinglogic/ul4/0.48/ul4-0.48.jar:/home/andreas/LivingLogic/cms/install/xist4c/WEB-INF/lib/ul4.jar:" + System.getProperty("user.dir") + ":" + System.getProperty("java.class.path") + ":/Users/walter/apache-tomcat-6.0.18/lib/naming-resources.jar:/Users/walter/apache-tomcat-6.0.18/lib/servlet-api.jar:/Users/walter/apache-tomcat-6.0.18/lib/jsp-api.jar:/Users/walter/.m2/repository/commons-lang/commons-lang/2.6/commons-lang-2.6.jar:/Users/walter/checkouts/LivingLogic.Java.ul4/ul4jython.jar:.");
		int status = javac.compile(args, new PrintWriter(System.err));
		System.err.flush();
		switch (status)
		{
			case 0:  // OK
				// Make the class file temporary as well
				new File(file.getParent(), classname + ".class").deleteOnExit();

				// Create an instance and return it
				try
				{
					return Class.forName(classname);
				}
				catch (ClassNotFoundException ex)
				{
					// Can't happen
					throw new RuntimeException(ex);
				}
			case 1:
				throw new RuntimeException("Compile status: ERROR");
			case 2:
				throw new RuntimeException("Compile status: CMDERR");
			case 3:
				throw new RuntimeException("Compile status: SYSERR");
			case 4:
				throw new RuntimeException("Compile status: ABNORMAL");
			default:
				throw new RuntimeException("Compile status: Unknown exit status");
		}
	}
}
