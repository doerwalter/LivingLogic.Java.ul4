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
import java.util.Vector;
import java.util.Set;
import java.util.Date;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.MathContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.ObjectUtils;

class Range extends AbstractList
{
	int start;

	int stop;

	int step;

	int length;

	public Range(int start, int stop, int step)
	{
		if (0 == step)
		{
			throw new IllegalArgumentException("Step argument must be non-zero!");
		}
		else if (0 < step)
		{
			this.length = rangeLength(start, stop, step);
		}
		else
		{
			this.length = rangeLength(stop, start, -step);
		}
		this.start = start;
		this.stop = stop;
		this.step = step;
	}

	public Object get(int index)
	{
		if ((index < 0) || (index >= length))
		{
			throw new IndexOutOfBoundsException("Invalid index: " + index);
		}
		return start + index * step;
	}

	protected int rangeLength(int lowerEnd, int higherEnd, int positiveStep)
	{
		int retVal = 0;
		if (lowerEnd < higherEnd)
		{
			int diff = higherEnd - lowerEnd - 1;
			retVal = diff/positiveStep + 1;
		}
		return retVal;
	}

	public int size()
	{
		return length;
	}
}

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

class StringReversedIterator implements Iterator<String>
{
	String string;

	int stringSize;

	int index;

	public StringReversedIterator(String string)
	{
		this.string = string;
		stringSize = string.length();
		index = stringSize - 1;
	}

	public boolean hasNext()
	{
		return index >= 0;
	}

	public String next()
	{
		if (index < 0)
		{
			throw new NoSuchElementException("No more characters available!");
		}
		return String.valueOf(string.charAt(index--));
	}

	public void remove()
	{
		throw new UnsupportedOperationException("Strings don't support character removal!");
	}
}

class ListReversedIterator implements Iterator
{
	List list;

	int listSize;

	int index;

	public ListReversedIterator(List list)
	{
		this.list = list;
		listSize = list.size();
		index = listSize - 1;
	}

	public boolean hasNext()
	{
		return index >= 0;
	}

	public Object next()
	{
		if (index < 0)
		{
			throw new NoSuchElementException("No more items available!");
		}
		return list.get(index--);
	}

	public void remove()
	{
		list.remove(index);
	}
}

class MapItemIterator implements Iterator<Vector>
{
	Iterator iterator;

	public MapItemIterator(Map map)
	{
		iterator = map.entrySet().iterator();
	}

	public boolean hasNext()
	{
		return iterator.hasNext();
	}

	public Vector next()
	{
		Vector retVal = new Vector(2);
		Map.Entry entry = (Map.Entry)iterator.next();
		retVal.add(entry.getKey());
		retVal.add(entry.getValue());
		return retVal;
	}

	public void remove()
	{
		iterator.remove();
	}
}

class ZipIterator implements Iterator<Vector>
{
	Iterator iterator1;
	Iterator iterator2;
	Iterator iterator3;

	public ZipIterator(Iterator iterator1, Iterator iterator2)
	{
		this.iterator1 = iterator1;
		this.iterator2 = iterator2;
		this.iterator3 = null;
	}

	public ZipIterator(Iterator iterator1, Iterator iterator2, Iterator iterator3)
	{
		this.iterator1 = iterator1;
		this.iterator2 = iterator2;
		this.iterator3 = iterator3;
	}

	public boolean hasNext()
	{
		return iterator1.hasNext() && iterator2.hasNext() && (iterator3 == null || iterator3.hasNext());
	}

	public Vector next()
	{
		Vector retVal = new Vector(iterator3 != null ? 3 : 2);
		retVal.add(iterator1.next());
		retVal.add(iterator2.next());
		if (iterator3 != null)
			retVal.add(iterator3.next());
		return retVal;
	}

	public void remove()
	{
		iterator1.remove();
		iterator2.remove();
		if (iterator3 != null)
			iterator3.remove();
	}
}

class SequenceEnumerator implements Iterator<Vector>
{
	Iterator sequenceIterator;

	int index = 0;

	public SequenceEnumerator(Iterator sequenceIterator)
	{
		this.sequenceIterator = sequenceIterator;
	}

	public boolean hasNext()
	{
		return sequenceIterator.hasNext();
	}

	public Vector next()
	{
		Vector retVal = new Vector(2);
		retVal.add(new Integer(index++));
		retVal.add(sequenceIterator.next());
		return retVal;
	}

	public void remove()
	{
		sequenceIterator.remove();
	}
}

public class Utils
{
	protected static final Integer INTEGER_TRUE = new Integer(1);

	protected static final Integer INTEGER_FALSE = new Integer(0);

	private static String objectType(Object obj)
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

	private static int _toInt(Object arg)
	{
		if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1 : 0;
		else if (arg instanceof Number)
			return ((Number)arg).intValue();
		throw new UnsupportedOperationException("can't convert " + objectType(arg) + " to int!");
	}

	private static long _toLong(Object arg)
	{
		if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1L : 0L;
		else if (arg instanceof Number)
			return ((Number)arg).longValue();
		throw new UnsupportedOperationException("can't convert " + objectType(arg) + " to long!");
	}

	private static float _toFloat(Object arg)
	{
		if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1.0f : 0.0f;
		else if (arg instanceof Number)
			return ((Number)arg).floatValue();
		throw new UnsupportedOperationException("can't convert " + objectType(arg) + " to float!");
	}

	private static double _toDouble(Object arg)
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
				return add(_toInt(arg1), _toInt(arg2));
			else if (arg2 instanceof Long)
				return add(_toLong(arg1), _toLong(arg2));
			else if (arg2 instanceof Float)
				return add(_toFloat(arg1), _toFloat(arg2));
			else if (arg2 instanceof Double)
				return add(_toDouble(arg1), _toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg2).add(_toBigInteger(_toInt(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).add(new BigDecimal(_toDouble(arg1)));
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return add(_toLong(arg1), _toLong(arg2));
			else if (arg2 instanceof Float)
				return add(_toFloat(arg1), _toFloat(arg2));
			else if (arg2 instanceof Double)
				return add(_toDouble(arg1), _toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg2).add(_toBigInteger(_toLong(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).add(new BigDecimal(_toDouble(arg1)));
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return add(_toFloat(arg1), _toFloat(arg2));
			else if (arg2 instanceof Double)
				return add(_toDouble(arg1), (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg2).add(new BigDecimal(_toDouble(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).add(new BigDecimal(_toDouble(arg1)));
		}
		else if (arg1 instanceof Double)
		{
			double value1 = (((Double)arg1).doubleValue());
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return add(value1, _toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg2).add(new BigDecimal(value1));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).add(new BigDecimal(value1));
		}
		else if (arg1 instanceof BigInteger)
		{
			BigInteger value1 = (BigInteger)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return value1.add(_toBigInteger(_toInt(arg2)));
			else if (arg2 instanceof Long)
				return value1.add(_toBigInteger(_toLong(arg2)));
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
				return value1.add(new BigDecimal(Integer.toString(_toInt(arg2))));
			else if (arg2 instanceof Long)
				return value1.add(new BigDecimal(Long.toString(_toLong(arg2))));
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
				return sub(_toInt(arg1), _toInt(arg2));
			else if (arg2 instanceof Long)
				return sub(_toLong(arg1), ((Long)arg2).longValue());
			else if (arg2 instanceof Float)
				return sub(_toFloat(arg1), ((Float)arg2).floatValue());
			else if (arg2 instanceof Double)
				return sub(_toDouble(arg1), ((Double)arg2).doubleValue());
			else if (arg2 instanceof BigInteger)
				return _toBigInteger(_toInt(arg1)).subtract((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(_toDouble(arg1)).subtract((BigDecimal)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return sub(_toLong(arg1), _toLong(arg2));
			else if (arg2 instanceof Float)
				return sub(_toFloat(arg1), ((Float)arg2).floatValue());
			else if (arg2 instanceof Double)
				return sub(_toDouble(arg1), ((Double)arg2).doubleValue());
			else if (arg2 instanceof BigInteger)
				return _toBigInteger(_toLong(arg1)).subtract((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(_toDouble(arg1)).subtract((BigDecimal)arg2);
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return sub(_toFloat(arg1), _toFloat(arg2));
			else if (arg2 instanceof Double)
				return sub(_toDouble(arg1), (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(_toDouble(arg1)).subtract(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(_toDouble(arg1)).subtract((BigDecimal)arg2);
		}
		else if (arg1 instanceof Double)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return sub(_toDouble(arg1), _toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(_toDouble(arg1)).subtract(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(_toDouble(arg1)).subtract((BigDecimal)arg2);
		}
		else if (arg1 instanceof BigInteger)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return ((BigInteger)arg1).subtract(_toBigInteger(_toInt(arg2)));
			else if (arg2 instanceof Long)
				return ((BigInteger)arg1).subtract(_toBigInteger(_toLong(arg2)));
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
				return ((BigDecimal)arg1).subtract(new BigDecimal(_toDouble(arg2)));
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
		int result = arg1 * arg2;
		if (result/arg1 == arg2) // result doesn't seem to have overflowed
			return result;
		else // we had an overflow => promote to BigInteger
			return _toBigInteger(arg1).multiply(_toBigInteger(arg2));
	}

	public static Object mul(long arg1, long arg2)
	{
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
				return mul(_toInt(arg1), _toInt(arg2));
			else if (arg2 instanceof Long)
				return mul(_toLong(arg1), ((Long)arg2).longValue());
			else if (arg2 instanceof Float)
				return mul(_toFloat(arg1), ((Float)arg2).floatValue());
			else if (arg2 instanceof Double)
				return mul(_toDouble(arg1), ((Double)arg2).doubleValue());
			else if (arg2 instanceof String)
				return mul(_toInt(arg1), (String)arg2);
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg2).multiply(_toBigInteger(_toInt(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal(_toDouble(arg1)));
			else if (arg2 instanceof List)
				return mul(_toInt(arg1), (List)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return mul(_toLong(arg1), _toLong(arg2));
			else if (arg2 instanceof Float)
				return mul(_toFloat(arg1), ((Float)arg2).floatValue());
			else if (arg2 instanceof Double)
				return mul(_toDouble(arg1), ((Double)arg2).doubleValue());
			else if (arg2 instanceof String)
				return mul(_toInt(arg1), (String)arg2);
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg2).multiply(_toBigInteger(_toLong(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal(_toDouble(arg1)));
			else if (arg2 instanceof List)
				return mul(_toLong(arg1), (List)arg2);
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return mul(_toFloat(arg1), _toFloat(arg2));
			else if (arg2 instanceof Double)
				return mul(_toDouble(arg1), (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg2).multiply(new BigDecimal(_toDouble(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal(_toDouble(arg1)));
		}
		else if (arg1 instanceof Double)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return mul(_toDouble(arg1), _toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg2).multiply(new BigDecimal(_toDouble(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal(_toDouble(arg1)));
		}
		else if (arg1 instanceof BigInteger)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return ((BigInteger)arg1).multiply(_toBigInteger(_toInt(arg2)));
			else if (arg2 instanceof Long)
				return ((BigInteger)arg1).multiply(_toBigInteger(_toLong(arg2)));
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
				return ((BigDecimal)arg1).multiply(new BigDecimal(_toDouble(arg2)));
			else if (arg2 instanceof BigInteger)
				return ((BigDecimal)arg1).multiply(new BigDecimal(((BigInteger)arg2)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg1).multiply(((BigDecimal)arg2));
		}
		else if (arg1 instanceof String)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return mul(_toInt(arg2), (String)arg1);
			else if (arg2 instanceof Long)
				return mul(_toLong(arg2), (String)arg1);
		}
		else if (arg1 instanceof List)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return mul(_toInt(arg2), (List)arg1);
			else if (arg2 instanceof Long)
				return mul(_toLong(arg2), (List)arg1);
		}
		throw new UnsupportedOperationException(objectType(arg1) + " * " + objectType(arg2) + " not supported!");
	}

	public static Object truediv(Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Long || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean || arg1 instanceof Float || arg1 instanceof Double)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return _toDouble(arg1) / _toDouble(arg2);
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(_toDouble(arg1)).divide(new BigDecimal((BigInteger)arg2), MathContext.DECIMAL128);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(_toDouble(arg1)).divide((BigDecimal)arg2, MathContext.DECIMAL128);
		}
		else if (arg1 instanceof BigInteger)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return new BigDecimal((BigInteger)arg1).divide(new BigDecimal(_toDouble(arg2)), MathContext.DECIMAL128);
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg1).divide(new BigDecimal((BigInteger)arg2), MathContext.DECIMAL128);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal((BigInteger)arg1).divide((BigDecimal)arg2, MathContext.DECIMAL128);
		}
		else if (arg1 instanceof BigDecimal)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return ((BigDecimal)arg1).divide(new BigDecimal(_toDouble(arg2)), MathContext.DECIMAL128);
			else if (arg2 instanceof BigInteger)
				return ((BigDecimal)arg1).divide(new BigDecimal((BigInteger)arg2), MathContext.DECIMAL128);
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg1).divide((BigDecimal)arg2, MathContext.DECIMAL128);
		}
		throw new UnsupportedOperationException(objectType(arg1) + " / " + objectType(arg2) + " not supported!");
	}

	public static Object floordiv(Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
			{
				// FIXME: Negative arguments don't work properly
				return _toInt(arg1) / _toInt(arg2);
			}
			else if (arg2 instanceof Long)
				return _toInt(arg1) / _toLong(arg2);
			else if (arg2 instanceof Float)
				return Math.floor(_toInt(arg1) / _toFloat(arg2));
			else if (arg2 instanceof Double)
				return Math.floor(_toInt(arg1) / _toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return _toBigInteger(_toInt(arg1)).divide((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(_toDouble(arg1)).divideToIntegralValue((BigDecimal)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return _toLong(arg1) / _toLong(arg2);
			else if (arg2 instanceof Float)
				return Math.floor(_toLong(arg1) / _toFloat(arg2));
			else if (arg2 instanceof Double)
				return Math.floor(_toLong(arg1) / _toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return _toBigInteger(_toLong(arg1)).divide((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(_toDouble(arg1)).divideToIntegralValue((BigDecimal)arg2);
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return Math.floor(_toFloat(arg1) / _toFloat(arg2));
			else if (arg2 instanceof Double)
				return Math.floor(_toDouble(arg1) / (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(_toDouble(arg1)).divideToIntegralValue(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(_toDouble(arg1)).divideToIntegralValue((BigDecimal)arg2);
		}
		else if (arg1 instanceof Double)
		{
			double value1 = (((Double)arg1).doubleValue());
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return Math.floor(value1 / _toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(value1).divideToIntegralValue(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(value1).divideToIntegralValue((BigDecimal)arg2);
		}
		else if (arg1 instanceof BigInteger)
		{
			BigInteger value1 = (BigInteger)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return value1.divide(_toBigInteger(_toInt(arg2)));
			else if (arg2 instanceof Long)
				return value1.divide(_toBigInteger(_toLong(arg2)));
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
				return value1.divideToIntegralValue(new BigDecimal(Integer.toString(_toInt(arg2))));
			else if (arg2 instanceof Long)
				return value1.divideToIntegralValue(new BigDecimal(Long.toString(_toLong(arg2))));
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
		return arg1 % arg2;
	}

	public static long mod(long arg1, long arg2)
	{
		return arg1 % arg2;
	}

	public static float mod(float arg1, float arg2)
	{
		return arg1 % arg2;
	}

	public static double mod(double arg1, double arg2)
	{
		return arg1 % arg2;
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
				return mod(_toInt(arg1), _toInt(arg2));
			else if (arg2 instanceof Long)
				return mod(_toLong(arg1), _toLong(arg2));
			else if (arg2 instanceof Float)
				return mod(_toFloat(arg1), _toFloat(arg2));
			else if (arg2 instanceof Double)
				return mod(_toDouble(arg1), _toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return mod(_toBigInteger(_toInt(arg1)), (BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return mod(new BigDecimal(_toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return mod(_toLong(arg1), _toLong(arg2));
			else if (arg2 instanceof Float)
				return mod(_toFloat(arg1), _toFloat(arg2));
			else if (arg2 instanceof Double)
				return mod(_toDouble(arg1), _toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return mod(_toBigInteger(_toLong(arg1)), (BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return mod(new BigDecimal(_toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return mod(_toFloat(arg1), _toFloat(arg2));
			else if (arg2 instanceof Double)
				return mod(_toDouble(arg1), (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return mod(new BigDecimal(_toDouble(arg1)), new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return mod(new BigDecimal(_toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Double)
		{
			double value1 = (((Double)arg1).doubleValue());
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return mod(value1, _toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return mod(new BigDecimal(value1), new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return mod(new BigDecimal(value1), ((BigDecimal)arg2));
		}
		else if (arg1 instanceof BigInteger)
		{
			BigInteger value1 = (BigInteger)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return mod(value1, _toBigInteger(_toInt(arg2)));
			else if (arg2 instanceof Long)
				return mod(value1, _toBigInteger(_toLong(arg2)));
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
				return mod(value1, new BigDecimal(Integer.toString(_toInt(arg2))));
			else if (arg2 instanceof Long)
				return mod(value1, new BigDecimal(Long.toString(_toLong(arg2))));
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
			int start = arg2 != null ? _toInt(arg2) : 0;
			int end = arg3 != null ? _toInt(arg3) : ((List)arg1).size();
			return getSlice((List)arg1, start, end);
		}
		else if (arg1 instanceof String)
		{
			int start = arg2 != null ? _toInt(arg2) : 0;
			int end = arg3 != null ? _toInt(arg3) : ((String)arg1).length();
			return getSlice((String)arg1, start, end);
		}
		throw new UnsupportedOperationException(objectType(arg1) + "[" + objectType(arg2) + ":" + objectType(arg3) + "] not supported!");
	}

	public static boolean getBool(Boolean obj)
	{
		return obj.booleanValue();
	}

	public static boolean getBool(String obj)
	{
		return (obj.length() > 0);
	}

	public static boolean getBool(Integer obj)
	{
		return (obj.intValue() != 0);
	}

	public static boolean getBool(Long obj)
	{
		return (obj.longValue() != 0);
	}

	public static boolean getBool(Double obj)
	{
		return (obj.doubleValue() != 0.);
	}

	public static boolean getBool(Date obj)
	{
		return true;
	}

	public static boolean getBool(Collection obj)
	{
		return !obj.isEmpty();
	}

	public static boolean getBool(Map obj)
	{
		return !obj.isEmpty();
	}

	public static boolean getBool(Object obj)
	{
		if (null == obj)
			return false;
		else if (obj instanceof Boolean)
			return getBool((Boolean)obj);
		else if (obj instanceof String)
			return getBool((String)obj);
		else if (obj instanceof Integer)
			return getBool((Integer)obj);
		else if (obj instanceof Long)
			return getBool((Long)obj);
		else if (obj instanceof Double)
			return getBool((Double)obj);
		else if (obj instanceof Date)
			return getBool((Date)obj);
		else if (obj instanceof Collection)
			return getBool((Collection)obj);
		else if (obj instanceof Map)
			return getBool((Map)obj);
		return true;
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
				return cmp(_toInt(arg1), _toInt(arg2));
			else if (arg2 instanceof Long)
				return cmp(_toLong(arg1), _toLong(arg2));
			else if (arg2 instanceof Float)
				return cmp(_toFloat(arg1), _toFloat(arg2));
			else if (arg2 instanceof Double)
				return cmp(_toDouble(arg1), _toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return cmp(_toBigInteger(_toInt(arg1)), (BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return cmp(new BigDecimal(_toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return cmp(_toLong(arg1), _toLong(arg2));
			else if (arg2 instanceof Float)
				return cmp(_toFloat(arg1), _toFloat(arg2));
			else if (arg2 instanceof Double)
				return cmp(_toDouble(arg1), _toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return cmp(_toBigInteger(_toLong(arg1)), (BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return cmp(new BigDecimal(_toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return cmp(_toFloat(arg1), _toFloat(arg2));
			else if (arg2 instanceof Double)
				return cmp(_toDouble(arg1), (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return cmp(new BigDecimal(_toDouble(arg1)), new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return cmp(new BigDecimal(_toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Double)
		{
			double value1 = (((Double)arg1).doubleValue());
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return cmp(value1, _toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return cmp(new BigDecimal(value1), new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return cmp(new BigDecimal(value1), (BigDecimal)arg2);
		}
		else if (arg1 instanceof BigInteger)
		{
			BigInteger value1 = (BigInteger)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return cmp(value1, _toBigInteger(_toInt(arg2)));
			else if (arg2 instanceof Long)
				return cmp(value1, _toBigInteger(_toLong(arg2)));
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
				return cmp(value1, new BigDecimal(Integer.toString(_toInt(arg2))));
			else if (arg2 instanceof Long)
				return cmp(value1, new BigDecimal(Long.toString(_toLong(arg2))));
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
		return true;
	}

	public static boolean ne(Object obj1, Object obj2)
	{
		if (null != obj1 && null != obj2)
			return cmp(obj1, obj2, "!=") != 0;
		return false;
	}

	public static boolean lt(Object obj1, Object obj2)
	{
		if (null != obj1 && null != obj2)
			return cmp(obj1, obj2, "<") < 0;
		return false;
	}

	public static boolean le(Object obj1, Object obj2)
	{
		if (null != obj1 && null != obj2)
			return cmp(obj1, obj2, "<=") <= 0;
		return true;
	}

	public static boolean gt(Object obj1, Object obj2)
	{
		if (null != obj1 && null != obj2)
			return cmp(obj1, obj2, ">") > 0;
		return false;
	}

	public static boolean ge(Object obj1, Object obj2)
	{
		if (null != obj1 && null != obj2)
			return cmp(obj1, obj2, ">=") >= 0;
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

	public static Object abs(Object arg)
	{
		if (arg instanceof Integer)
		{
			int value = ((Integer)arg).intValue();
			if (value >= 0)
				return arg;
			else if (value == -0x80000000) // Prevent overflow by switching to long
				return 0x80000000L;
			else
				return -value;
		}
		else if (arg instanceof Long)
		{
			long value = ((Long)arg).longValue();
			if (value >= 0)
				return arg;
			else if (value == -0x8000000000000000L) // Prevent overflow by switching to BigInteger
				return new BigInteger("8000000000000000", 16);
			else
				return -value;
		}
		else if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1 : 0;
		else if (arg instanceof Byte || arg instanceof Short)
		{
			int value = ((Number)arg).intValue();
			if (value >= 0)
				return arg;
			else
				return -value;
		}
		else if (arg instanceof Float)
		{
			float value = ((Float)arg).floatValue();
			if (value >= 0)
				return arg;
			else
				return -value;
		}
		else if (arg instanceof Double)
		{
			double value = ((Double)arg).doubleValue();
			if (value >= 0)
				return arg;
			else
				return -value;
		}
		else if (arg instanceof BigInteger)
			return ((BigInteger)arg).abs();
		else if (arg instanceof BigDecimal)
			return ((BigDecimal)arg).abs();
		throw new UnsupportedOperationException("abs(" + objectType(arg) + ") not supported!");
	}

	public static String xmlescape(Object obj)
	{
		if (obj == null)
			return "";

		String str = str(obj);
		int length = str.length();
		StringBuffer sb = new StringBuffer((int)(1.2 * length));
		for (int offset = 0; offset < length; offset++)
		{
			char c = str.charAt(offset);
			switch (c)
			{
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '&':
					sb.append("&amp;");
					break;
				case '\'':
					sb.append("&#39;");
					break;
				case '"':
					sb.append("&quot;");
					break;
				case '\t':
					sb.append(c);
					break;
				case '\n':
					sb.append(c);
					break;
				case '\r':
					sb.append(c);
					break;
				case '\u0085':
					sb.append(c);
					break;
				default:
					if ((('\u0020' <= c) && (c <= '\u007e')) || ('\u00A0' <= c))
						sb.append(c);
					else
						sb.append("&#").append((int)c).append(';');
					break;
			}
		}
		return sb.toString();
	}

	public static Date utcnow()
	{
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String formatted = df.format(new Date());
		df.setTimeZone(TimeZone.getDefault());
		try
		{
			return df.parse(formatted);
		}
		catch (ParseException ex)
		{
			// Can't happen
			return null;
		}
	}

	public static String csv(Object obj)
	{
		if (obj == null)
			return "";
		if (!(obj instanceof String))
			obj = repr(obj);
		return StringEscapeUtils.escapeCsv((String)obj);
	}

	public static Object toInteger(Object obj)
	{
		if (obj instanceof String)
			return Integer.valueOf((String)obj);
		else if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short || obj instanceof Long || obj instanceof BigInteger)
			return obj;
		else if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue() ? INTEGER_TRUE : INTEGER_FALSE;
		else if (obj instanceof Float || obj instanceof Double)
			return ((Number)obj).intValue();
		else if (obj instanceof BigDecimal)
			return ((BigDecimal)obj).toBigInteger();
		throw new UnsupportedOperationException("int(" + objectType(obj) + ") not supported!");
	}

	public static Object toInteger(Object obj1, Object obj2)
	{
		if (obj1 instanceof String)
		{
			if (obj2 instanceof Integer || obj2 instanceof Byte || obj2 instanceof Short || obj2 instanceof Long || obj2 instanceof BigInteger)
				return Integer.valueOf((String)obj1, ((Number)obj2).intValue());
		}
		throw new UnsupportedOperationException("int(" + objectType(obj1) + ", " + objectType(obj2) + ") not supported!");
	}

	public static Object toFloat(Object obj)
	{
		if (obj instanceof String)
			return Double.valueOf((String)obj);
		else if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short)
			return (double)((Number)obj).intValue();
		else if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue() ? 1.0d : 0.0d;
		else if (obj instanceof Long)
			return (double)((Long)obj).longValue();
		else if (obj instanceof BigInteger)
			return new BigDecimal(((BigInteger)obj).toString());
		else if (obj instanceof BigDecimal || obj instanceof Float || obj instanceof Double)
			return obj;
		throw new UnsupportedOperationException("float(" + objectType(obj) + ") not supported!");
	}

	public static SimpleDateFormat isoReprDateFormatter = new SimpleDateFormat("@yyyy-MM-dd'T'");
	public static SimpleDateFormat isoReprDateTimeFormatter = new SimpleDateFormat("@yyyy-MM-dd'T'HH:mm:ss");
	public static SimpleDateFormat isoReprTimestampMicroFormatter = new SimpleDateFormat("@yyyy-MM-dd'T'HH:mm:ss.SSS'000'");

	public static String repr(Object obj)
	{
		if (obj == null)
			return "None";
		else if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue() ? "True" : "False";
		else if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short || obj instanceof Long || obj instanceof BigInteger || obj instanceof Double || obj instanceof Float)
			return obj.toString();
		else if (obj instanceof BigDecimal)
		{
			String result = obj.toString();
			if (result.indexOf('.') < 0 || result.indexOf('E') < 0 || result.indexOf('e') < 0)
				result += ".0";
			return result;
		}
		else if (obj instanceof String)
			return new StringBuffer()
				.append("\"")
				.append(StringEscapeUtils.escapeJava(((String)obj)))
				.append("\"")
				.toString();
		else if (obj instanceof Date)
		{
			if (microsecond(obj) != 0)
				return isoReprTimestampMicroFormatter.format(obj);
			else
			{
				if (hour(obj) != 0 || minute(obj) != 0 || second(obj) != 0)
					return isoReprDateTimeFormatter.format(obj);
				else
					return isoReprDateFormatter.format(obj);
			}
			
		}
		else if (obj instanceof Color)
			return ((Color)obj).repr();
		else if (obj instanceof Collection)
		{
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			boolean first = true;
			for (Object o : ((Collection)obj))
			{
				if (first)
					first = false;
				else
					sb.append(", ");
				sb.append(repr(o));
			}
			sb.append("]");
			return sb.toString();
		}
		else if (obj instanceof Map)
		{
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			boolean first = true;

			Set<Map.Entry> entrySet = ((Map)obj).entrySet();
			for (Map.Entry entry : entrySet)
			{
				if (first)
					first = false;
				else
					sb.append(", ");
				sb.append(repr(entry.getKey()));
				sb.append(": ");
				sb.append(repr(entry.getValue()));
			}
			sb.append("}");
			return sb.toString();
		}
		return null;
	}

	public static SimpleDateFormat strDateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat strTimestampMicroFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS'000'");

	public static String str(Object obj)
	{
		if (obj == null)
			return "";
		else if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue() ? "True" : "False";
		else if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short || obj instanceof Long || obj instanceof BigInteger || obj instanceof Double || obj instanceof Float)
			return obj.toString();
		else if (obj instanceof BigDecimal)
		{
			String result = obj.toString();
			if (result.indexOf('.') < 0 || result.indexOf('E') < 0 || result.indexOf('e') < 0)
				result += ".0";
			return result;
		}
		else if (obj instanceof String)
			return (String)obj;
		else if (obj instanceof Date)
		{
			if (microsecond(obj) != 0)
				return strTimestampMicroFormatter.format(obj);
			else
			{
				if (hour(obj) != 0 || minute(obj) != 0 || second(obj) != 0)
					return strDateTimeFormatter.format(obj);
				else
					return isoDateFormatter.format(obj);
			}
		}
		else if (obj instanceof Color)
			return ((Color)obj).toString();
		else
			return repr(obj);
	}

	public static String json(Object obj)
	{
		if (obj == null)
			return "null";
		else if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue() ? "true" : "false";
		else if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short || obj instanceof Long || obj instanceof BigInteger || obj instanceof Double || obj instanceof Float)
			return obj.toString();
		else if (obj instanceof BigDecimal)
		{
			String result = obj.toString();
			if (result.indexOf('.') < 0 || result.indexOf('E') < 0 || result.indexOf('e') < 0)
				result += ".0";
			return result;
		}
		else if (obj instanceof String)
			return new StringBuffer()
				.append("\"")
				.append(StringEscapeUtils.escapeJavaScript(((String)obj)))
				.append("\"")
				.toString();
		else if (obj instanceof Date)
		{
			Calendar calendar = new GregorianCalendar();
			calendar.setTime((Date)obj);
			StringBuffer buffer = new StringBuffer();
			buffer
				.append("new Date(")
				.append(calendar.get(Calendar.YEAR))
				.append(", ")
				.append(calendar.get(Calendar.MONTH))
				.append(", ")
				.append(calendar.get(Calendar.DAY_OF_MONTH))
				.append(", ")
				.append(calendar.get(Calendar.HOUR_OF_DAY))
				.append(", ")
				.append(calendar.get(Calendar.MINUTE))
				.append(", ")
				.append(calendar.get(Calendar.SECOND));
			int milliSeconds = calendar.get(Calendar.MILLISECOND);
			if (milliSeconds != 0)
			{
				buffer.append(", ").append(milliSeconds);
			}
			buffer.append(")");
			return buffer.toString();
		}
		else if (obj instanceof Color)
		{
			Color c = (Color)obj;
			return new StringBuffer()
				.append("ul4.Color.create(")
				.append(c.getR())
				.append(", ")
				.append(c.getG())
				.append(", ")
				.append(c.getB())
				.append(", ")
				.append(c.getA())
				.append(")")
				.toString();
		}
		else if (obj instanceof Collection)
		{
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			boolean first = true;
			for (Object o : (Collection)obj)
			{
				if (first)
					first = false;
				else
					sb.append(", ");
				sb.append(json(o));
			}
			sb.append("]");
			return sb.toString();
		}
		else if (obj instanceof Map)
		{
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			boolean first = true;
			Set<Map.Entry> entrySet = ((Map)obj).entrySet();
			for (Map.Entry entry : entrySet)
			{
				if (first)
					first = false;
				else
					sb.append(", ");
				sb.append(json(entry.getKey()));
				sb.append(": ");
				sb.append(json(entry.getValue()));
			}
			sb.append("}");
			return sb.toString();
		}
		else if (obj instanceof InterpretedTemplate)
		{
			return ((InterpretedTemplate)obj).javascriptSource();
		}
		return null;
	}

	public static Iterator reversed(Object obj)
	{
		if (obj instanceof String)
			return new StringReversedIterator((String)obj);
		else if (obj instanceof List)
			return new ListReversedIterator((List)obj);
		throw new UnsupportedOperationException("reversed(" + objectType(obj) + ") not supported!");
	}

	public static Object length(Object obj)
	{
		if (obj instanceof String)
			return ((String)obj).length();
		else if (obj instanceof Collection)
			return ((Collection)obj).size();
		else if (obj instanceof Map)
			return ((Map)obj).size();
		throw new UnsupportedOperationException("len(" + objectType(obj) + ") not supported!");
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

	public static Object enumerate(Object obj)
	{
		return new SequenceEnumerator(iterator(obj));
	}

	public static Object chr(Object obj)
	{
		if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short)
		{
			int intValue = ((Number)obj).intValue();
			char charValue = (char)intValue;
			if (intValue != (int)charValue)
			{
				throw new IndexOutOfBoundsException("Code point " + intValue + " is invalid!");
			}
			return String.valueOf(charValue);
		}
		else if (obj instanceof Boolean)
		{
			return ((Boolean)obj).booleanValue() ? "\u0001" : "\u0000";
		}
		else if (obj instanceof Long)
		{
			long longValue = ((Long)obj).longValue();
			char charValue = (char)longValue;
			if (longValue != (long)charValue)
			{
				throw new IndexOutOfBoundsException("Code point " + longValue + " is invalid!");
			}
			return String.valueOf(charValue);
		}
		// FIXME: Add support for BigInteger
		throw new UnsupportedOperationException("chr(" + objectType(obj) + ") not supported!");
	}

	public static Object ord(Object obj)
	{
		if (obj instanceof String)
		{
			if (1 != ((String)obj).length())
			{
				throw new IllegalArgumentException("String " + obj + " contains more than one unicode character!");
			}
			return (int)((String)obj).charAt(0);
		}
		throw new UnsupportedOperationException("ord(" + objectType(obj) + ") not supported!");
	}

	public static Object hex(Object obj)
	{
		if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short)
		{
			int value = ((Number)obj).intValue();
			if (value < 0)
				return "-0x" + Integer.toHexString(-value);
			else
				return "0x" + Integer.toHexString(value);
		}
		else if (obj instanceof Boolean)
		{
			return ((Boolean)obj).booleanValue() ? "0x1" : "0x0";
		}
		else if (obj instanceof Long)
		{
			long value = ((Long)obj).longValue();
			if (value < 0)
				return "-0x" + Long.toHexString(-value);
			else
				return "0x" + Long.toHexString(value);
		}
		else if (obj instanceof BigInteger)
		{
			BigInteger bi = (BigInteger)obj;
			if (bi.signum() < 0)
			{
				return "-0x" + bi.toString(16).substring(1);
			}
			else
				return "0x" + bi.toString(16);
		}
		throw new UnsupportedOperationException("hex(" + objectType(obj) + ") not supported!");
	}

	public static Object oct(Object obj)
	{
		if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short)
		{
			int value = ((Number)obj).intValue();
			if (value < 0)
				return "-0o" + Integer.toOctalString(-value);
			else
				return "0o" + Integer.toOctalString(value);
		}
		else if (obj instanceof Boolean)
		{
			return ((Boolean)obj).booleanValue() ? "0o1" : "0o0";
		}
		else if (obj instanceof Long)
		{
			long value = ((Long)obj).longValue();
			if (value < 0)
				return "-0o" + Long.toOctalString(-value);
			else
				return "0o" + Long.toOctalString(value);
		}
		else if (obj instanceof BigInteger)
		{
			BigInteger bi = (BigInteger)obj;
			if (bi.signum() < 0)
			{
				return "-0o" + bi.toString(8).substring(1);
			}
			else
				return "0o" + bi.toString(8);
		}
		throw new UnsupportedOperationException("oct(" + objectType(obj) + ") not supported!");
	}

	public static Object bin(Object obj)
	{
		if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short)
		{
			int value = ((Number)obj).intValue();
			if (value < 0)
				return "-0b" + Integer.toBinaryString(-value);
			else
				return "0b" + Integer.toBinaryString(value);
		}
		else if (obj instanceof Boolean)
		{
			return ((Boolean)obj).booleanValue() ? "0b1" : "0b0";
		}
		else if (obj instanceof Long)
		{
			long value = ((Long)obj).longValue();
			if (value < 0)
				return "-0b" + Long.toBinaryString(-value);
			else
				return "0b" + Long.toBinaryString(value);
		}
		else if (obj instanceof BigInteger)
		{
			BigInteger bi = (BigInteger)obj;
			if (bi.signum() < 0)
			{
				return "-0b" + bi.toString(2).substring(1);
			}
			else
				return "0b" + bi.toString(2);
		}
		throw new UnsupportedOperationException("bin(" + objectType(obj) + ") not supported!");
	}

	public static Object sorted(String obj)
	{
		Vector retVal;
		int length = obj.length();
		retVal = new Vector(obj.length());
		for (int i = 0; i < length; i++)
		{
			retVal.add(String.valueOf(obj.charAt(i)));
		}
		Collections.sort(retVal);
		return retVal;
	}

	public static Object sorted(Collection obj)
	{
		Vector retVal = new Vector(obj);
		Collections.sort(retVal);
		return retVal;
	}

	public static Object sorted(Map obj)
	{
		Vector retVal = new Vector(obj.keySet());
		Collections.sort(retVal);
		return retVal;
	}

	public static Object sorted(Set obj)
	{
		Vector retVal = new Vector(obj);
		Collections.sort(retVal);
		return retVal;
	}

	public static Object sorted(Iterator obj)
	{
		Vector retVal = new Vector();
		while (obj.hasNext())
			retVal.add(obj.next());
		Collections.sort(retVal);
		return retVal;
	}

	public static Object sorted(Object obj)
	{
		if (obj instanceof String)
			return sorted((String)obj);
		else if (obj instanceof Collection)
			return sorted((Collection)obj);
		else if (obj instanceof Map)
			return sorted((Map)obj);
		else if (obj instanceof Set)
			return sorted((Set)obj);
		else if (obj instanceof Iterator)
			return sorted((Iterator)obj);
		throw new RuntimeException("sorted(" + objectType(obj) + ") not supported!");
	}

	public static Object range(Object obj)
	{
		return new Range(0, _toInt(obj), 1);
	}

	public static Object range(Object obj1, Object obj2)
	{
		return new Range(_toInt(obj1), _toInt(obj2), 1);
	}

	public static Object range(Object obj1, Object obj2, Object obj3)
	{
		return new Range(_toInt(obj1), _toInt(obj2), _toInt(obj3));
	}

	public static Object zip(Object obj1, Object obj2)
	{
		return new ZipIterator(iterator(obj1), iterator(obj2));
	}

	public static Object zip(Object obj1, Object obj2, Object obj3)
	{
		return new ZipIterator(iterator(obj1), iterator(obj2), iterator(obj3));
	}

	public static Object split(Object obj)
	{
		if (obj instanceof String)
			return Arrays.asList(StringUtils.split((String)obj));
		throw new UnsupportedOperationException(objectType(obj) + ".split() not supported!");
	}

	public static Object split(Object obj, Object arg)
	{
		if (obj instanceof String)
		{
			if (arg == null)
				return Arrays.asList(StringUtils.split((String)obj));
			else if (arg instanceof String)
				return Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens((String)obj, (String)arg));
		}
		throw new UnsupportedOperationException(objectType(obj) + ".split(" + objectType(arg) + ") not supported!");
	}

	public static Object split(Object obj, Object arg1, Object arg2)
	{
		if (obj instanceof String)
		{
			if (arg1 == null)
				return Arrays.asList(StringUtils.splitByWholeSeparator((String)obj, null, _toInt(arg2)+1));
			else if (arg1 instanceof String)
				return Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens((String)obj, (String)arg1, _toInt(arg2)+1));
		}
		throw new UnsupportedOperationException(objectType(obj) + ".split(" + objectType(arg1) + ", " + objectType(arg2) + ") not supported!");
	}

	public static Object rsplit(String obj, int maxsplit)
	{
		ArrayList<String> result = new ArrayList<String>();
		int start, end;
		start = end = obj.length() - 1;
		while (maxsplit-- > 0)
		{
			while (start >= 0 && Character.isWhitespace(obj.charAt(start)))
				--start;
			if (start < 0)
				break;
			end = start--;
			while (start >= 0 && !Character.isWhitespace(obj.charAt(start)))
				--start;
			if (start != end)
				result.add(0, obj.substring(start+1, end+1));
		}
		if (start >= 0)
		{
			while (start >= 0 && Character.isWhitespace(obj.charAt(start)))
				--start;
			if (start >= 0)
				result.add(0, obj.substring(0, start+1));
		}
		return result;
	}

	public static Object rsplit(String obj, String separator, int maxsplit)
	{
		if (separator.length() == 0)
			throw new UnsupportedOperationException("empty separator not supported");

		ArrayList<String> result = new ArrayList<String>();
		int start, end, seplen = separator.length();
		start = end = obj.length();
		while (maxsplit-- > 0)
		{
			start = obj.lastIndexOf(separator, end-seplen);
			if (start < 0)
				break;
			result.add(0, obj.substring(start+seplen, end));
			end = start;
		}
		result.add(0, obj.substring(0, end));
		return result;
	}

	public static Object rsplit(Object obj)
	{
		if (obj instanceof String)
			return Arrays.asList(StringUtils.split((String)obj));
		throw new UnsupportedOperationException(objectType(obj) + ".rsplit() not supported!");
	}

	public static Object rsplit(Object obj, Object arg)
	{
		if (obj instanceof String)
		{
			if (arg == null)
				return Arrays.asList(StringUtils.split((String)obj));
			else if (arg instanceof String)
				return rsplit((String)obj, (String)arg, 0xffffffff);
		}
		throw new UnsupportedOperationException(objectType(obj) + ".rsplit(" + objectType(arg) + ") not supported!");
	}

	public static Object rsplit(Object obj, Object arg1, Object arg2)
	{
		if (obj instanceof String)
		{
			if (arg1 == null)
				return rsplit((String)obj, _toInt(arg2));
			else
				return rsplit((String)obj, (String)arg1, _toInt(arg2));
		}
		throw new UnsupportedOperationException(objectType(obj) + ".rsplit(" + objectType(arg1) + ", " + objectType(arg2) + ") not supported!");
	}

	public static Object strip(Object obj)
	{
		if (obj instanceof String)
			return StringUtils.strip((String)obj);
		throw new UnsupportedOperationException(objectType(obj) + ".strip() not supported!");
	}

	public static Object strip(Object obj, Object stripChars)
	{
		if (obj instanceof String && stripChars instanceof String)
			return StringUtils.strip((String)obj, (String)stripChars);
		throw new UnsupportedOperationException(objectType(obj) + ".strip(" + objectType(stripChars) + ") not supported!");
	}

	public static Object lstrip(Object obj)
	{
		if (obj instanceof String)
			return StringUtils.stripStart((String)obj, null);
		throw new UnsupportedOperationException(objectType(obj) + ".lstrip() not supported!");
	}

	public static Object lstrip(Object obj, Object stripChars)
	{
		if (obj instanceof String && stripChars instanceof String)
			return StringUtils.stripStart((String)obj, (String)stripChars);
		throw new UnsupportedOperationException(objectType(obj) + ".lstrip(" + objectType(stripChars) + ") not supported!");
	}

	public static Object rstrip(Object obj)
	{
		if (obj instanceof String)
			return StringUtils.stripEnd((String)obj, null);
		throw new UnsupportedOperationException(objectType(obj) + ".rstrip() not supported!");
	}

	public static Object rstrip(Object obj, Object stripChars)
	{
		if (obj instanceof String && stripChars instanceof String)
			return StringUtils.stripEnd((String)obj, (String)stripChars);
		throw new UnsupportedOperationException(objectType(obj) + ".rstrip(" + objectType(stripChars) + ") not supported!");
	}

	public static Object upper(String obj)
	{
		return obj.toUpperCase();
	}

	public static Object upper(Object obj)
	{
		if (obj instanceof String)
			return upper((String)obj);
		throw new UnsupportedOperationException(objectType(obj) + ".upper() not supported!");
	}

	public static Object lower(String obj)
	{
		return obj.toLowerCase();
	}

	public static Object lower(Object obj)
	{
		if (obj instanceof String)
			return lower((String)obj);
		throw new UnsupportedOperationException(objectType(obj) + ".lower() not supported!");
	}

	public static Object capitalize(String obj)
	{
		return String.valueOf(Character.toTitleCase(obj.charAt(0))) + obj.substring(1).toLowerCase();
	}

	public static Object capitalize(Object obj)
	{
		if (obj instanceof String)
			return capitalize((String)obj);
		throw new UnsupportedOperationException(objectType(obj) + ".capitalize() not supported!");
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
	public static SimpleDateFormat isoDateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public static SimpleDateFormat isoTimestampFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	public static SimpleDateFormat isoTimestampMicroFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'000'");

	public static String isoformat(Date obj)
	{
		if (microsecond(obj) != 0)
			return isoTimestampMicroFormatter.format(obj);
		else
		{
			if (hour(obj) != 0 || minute(obj) != 0 || second(obj) != 0)
				return isoDateTimeFormatter.format(obj);
			else
				return isoDateFormatter.format(obj);
		}
	}

	public static String isoformat(Object obj)
	{
		if (obj instanceof Date)
			return isoformat((Date)obj);
		throw new UnsupportedOperationException(objectType(obj) + ".isoformat() not supported!");
	}

	public static Date isoparse(String format)
	{
		try
		{
			int length = format.length();
			if (length == 11)
				return isoDateFormatter.parse(format);
			else if (length == 19)
				return isoDateTimeFormatter.parse(format);
			else // if (len == 26)
			{
				// ignore last three digits
				return isoTimestampFormatter.parse(format.substring(0, 23));
			}
		}
		catch (java.text.ParseException ex) // can not happen when reading from the binary format
		{
			throw new RuntimeException("can't parse " + repr(format));
		}
	}

	public static SimpleDateFormat mimeDateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", new Locale("en"));

	public static String mimeformat(Date obj)
	{
		return mimeDateFormatter.format(obj);
	}

	public static String mimeformat(Object obj)
	{
		if (obj instanceof Date)
			return mimeformat((Date)obj);
		throw new UnsupportedOperationException(objectType(obj) + ".mimeformat() not supported!");
	}

	public static Object format(Date obj, String formatString, Locale locale)
	{
		StringBuffer javaFormatString = new StringBuffer();
		int formatStringLength = formatString.length();
		boolean escapeCharacterFound = false;
		boolean inLiteral = false;
		char formatChar;
		String javaFormatSequence;
		for (int i = 0; i < formatStringLength; i++)
		{
			formatChar = formatString.charAt(i);
			if (escapeCharacterFound)
			{
				switch (formatChar)
				{
					case 'a':
						javaFormatSequence = "EE";
						break;
					case 'A':
						javaFormatSequence = "EEEE";
						break;
					case 'b':
						javaFormatSequence = "MMM";
						break;
					case 'B':
						javaFormatSequence = "MMMM";
						break;
					case 'c':
						throw new UnsupportedOperationException("escape sequence %c not supported");
					case 'd':
						javaFormatSequence = "dd";
						break;
					case 'f':
						javaFormatSequence = "SSS'000";
						break;
					case 'H':
						javaFormatSequence = "HH";
						break;
					case 'I':
						javaFormatSequence = "hh";
						break;
					case 'j':
						javaFormatSequence = "DDD";
						break;
					case 'm':
						javaFormatSequence = "MM";
						break;
					case 'M':
						javaFormatSequence = "mm";
						break;
					case 'p':
						javaFormatSequence = "aa";
						break;
					case 'S':
						javaFormatSequence = "ss";
						break;
					case 'U':
						javaFormatSequence = "ww";
						break;
					case 'w':
						throw new UnsupportedOperationException("escape sequence %w not supported");
					case 'W':
						javaFormatSequence = "ww";
						break;
					case 'x':
						throw new UnsupportedOperationException("escape sequence %x not supported");
					case 'X':
						throw new UnsupportedOperationException("escape sequence %X not supported");
					case 'y':
						javaFormatSequence = "yy";
						break;
					case 'Y':
						javaFormatSequence = "yyyy";
						break;
					default:
						javaFormatSequence = null;
						break;
				}
				if (inLiteral != (null == javaFormatSequence))
				{
					javaFormatString.append('\'');
					inLiteral = !inLiteral;
				}
				if (null != javaFormatSequence)
				{
					javaFormatString.append(javaFormatSequence);
					if ('f' == formatChar)
					{
						inLiteral = true;
					}
				}
				else
				{
					javaFormatString.append(formatChar);
				}
				escapeCharacterFound = false;
			}
			else
			{
				escapeCharacterFound = ('%' == formatChar);
				if (!escapeCharacterFound)
				{
					if (inLiteral = !inLiteral)
					{
						javaFormatString.append('\'');
					}
					javaFormatString.append(formatChar);
					if ('\'' == formatChar)
					{
						javaFormatString.append(formatChar);
					}
				}
			}
		}
		if (inLiteral)
		{
			javaFormatString.append('\'');
		}
		return new SimpleDateFormat(javaFormatString.toString(), locale).format(obj);
	}

	public static Object format(Object obj, Object formatString, Locale locale)
	{
		if (formatString instanceof String)
		{
			if (obj instanceof Date)
			{
				return format((Date)obj, (String)formatString, locale);
			}
		}
		throw new UnsupportedOperationException(objectType(obj) + ".format(" + objectType(formatString) + ") not supported!");
	}

	public static Object format(Object obj, Object formatString)
	{
		return format(obj, formatString, Locale.getDefault());
	}

	public static Object replace(Object obj, Object arg1, Object arg2)
	{
		if (obj instanceof String && arg1 instanceof String && arg2 instanceof String)
			return StringUtils.replace((String)obj, (String)arg1, (String)arg2);
		throw new UnsupportedOperationException(objectType(obj) + ".replace(" + objectType(arg1) + ", " + objectType(arg2) + ") not supported!");
	}

	public static Object find(Object obj, Object arg1)
	{
		if (obj instanceof String && arg1 instanceof String)
			return ((String)obj).indexOf((String)arg1);
		throw new UnsupportedOperationException(objectType(obj) + ".find(" + objectType(arg1) + ") not supported!");
	}

	public static Object find(Object obj, Object arg1, Object arg2)
	{
		if (obj instanceof String && arg1 instanceof String)
			return ((String)obj).indexOf((String)arg1, _toInt(arg2));
		throw new UnsupportedOperationException(objectType(obj) + ".find(" + objectType(arg1) + ", " + objectType(arg2) + ") not supported!");
	}

	public static Object find(Object obj, Object arg1, Object arg2, Object arg3)
	{
		if (obj instanceof String && arg1 instanceof String)
		{
			int startIndex = _toInt(arg2);
			int endIndex = _toInt(arg3);
			int result = ((String)obj).indexOf((String)arg1, _toInt(arg2));
			if (result + ((String)arg1).length() > endIndex)
				return -1;
			return result;
		}
		throw new UnsupportedOperationException(objectType(obj) + ".find(" + objectType(arg1) + ", " + objectType(arg2) + ", " + objectType(arg3) + ") not supported!");
	}

	public static Object rfind(Object obj, Object arg1)
	{
		if (obj instanceof String && arg1 instanceof String)
			return ((String)obj).lastIndexOf((String)arg1);
		throw new UnsupportedOperationException(objectType(obj) + ".rfind(" + objectType(arg1) + ") not supported!");
	}

	public static Object rfind(Object obj, Object arg1, Object arg2)
	{
		if (obj instanceof String && arg1 instanceof String)
		{
			int startIndex = _toInt(arg2);
			int result = ((String)obj).lastIndexOf((String)arg1);
			if (result < startIndex)
				return -1;
			return result;
		}
		throw new UnsupportedOperationException(objectType(obj) + ".rfind(" + objectType(arg1) + ", " + objectType(arg2) + ") not supported!");
	}

	public static Object rfind(Object obj, Object arg1, Object arg2, Object arg3)
	{
		if (obj instanceof String && arg1 instanceof String)
		{
			int startIndex = _toInt(arg2);
			int endIndex = _toInt(arg3) - ((String)arg1).length();
			if (endIndex < 0)
				return -1;
			int result = ((String)obj).lastIndexOf((String)arg1, endIndex);
			if (result < startIndex)
				return -1;
			return result;
		}
		throw new UnsupportedOperationException(objectType(obj) + ".rfind(" + objectType(arg1) + ", " + objectType(arg2) + ", " + objectType(arg3) + ") not supported!");
	}

	public static Object items(Map obj)
	{
		return new MapItemIterator(obj);
	}

	public static Object items(Object obj)
	{
		if (obj instanceof Map)
			return items((Map)obj);
		throw new UnsupportedOperationException(objectType(obj) + ".items() not supported!");
	}

	public static String type(Object obj)
	{
		if (obj == null)
			return "none";
		else if (obj instanceof String)
			return "str";
		else if (obj instanceof Boolean)
			return "bool";
		else if (obj instanceof Integer || obj instanceof Long || obj instanceof Byte || obj instanceof Short || obj instanceof BigInteger)
			return "int";
		else if (obj instanceof Double || obj instanceof Float || obj instanceof BigDecimal)
			return "float";
		else if (obj instanceof Date)
			return "date";
		else if (obj instanceof Color)
			return "color";
		else if (obj instanceof List)
			return "list";
		else if (obj instanceof Map)
			return "dict";
		else if (obj instanceof Template)
			return "template";
		else
			return null;
	}

	public static Color rgb(Object arg1, Object arg2, Object arg3)
	{
		return Color.fromrgb(_toDouble(arg1), _toDouble(arg2), _toDouble(arg3));
	}

	public static Color rgb(Object arg1, Object arg2, Object arg3, Object arg4)
	{
		return Color.fromrgb(_toDouble(arg1), _toDouble(arg2), _toDouble(arg3), _toDouble(arg4));
	}

	public static Color hsv(Object arg1, Object arg2, Object arg3)
	{
		return Color.fromhsv(_toDouble(arg1), _toDouble(arg2), _toDouble(arg3));
	}

	public static Color hsv(Object arg1, Object arg2, Object arg3, Object arg4)
	{
		return Color.fromhsv(_toDouble(arg1), _toDouble(arg2), _toDouble(arg3), _toDouble(arg4));
	}

	public static Color hls(Object arg1, Object arg2, Object arg3)
	{
		return Color.fromhls(_toDouble(arg1), _toDouble(arg2), _toDouble(arg3));
	}

	public static Color hls(Object arg1, Object arg2, Object arg3, Object arg4)
	{
		return Color.fromhls(_toDouble(arg1), _toDouble(arg2), _toDouble(arg3), _toDouble(arg4));
	}

	public static Color withlum(Object arg1, Object arg2)
	{
		return ((Color)arg1).withlum(_toDouble(arg2));
	}

	public static Color witha(Object arg1, Object arg2)
	{
		return ((Color)arg1).witha(_toInt(arg2));
	}

	public static String join(Object obj, Object arg)
	{
		if (obj instanceof String)
		{
			if (arg instanceof Collection)
				return StringUtils.join((Collection)arg, (String)obj);
			else
				return StringUtils.join(iterator(arg), (String)obj);
		}
		else
			throw new UnsupportedOperationException(objectType(obj) + ".join(" + objectType(arg) + ") not supported!");
	}

	public static int day(Object obj)
	{
		if (obj instanceof Date)
		{
			Calendar calendar = new GregorianCalendar();
			calendar.setTime((Date)obj);
			return calendar.get(Calendar.DAY_OF_MONTH);
		}
		throw new UnsupportedOperationException(objectType(obj) + ".day() not supported!");
	}

	public static int month(Object obj)
	{
		if (obj instanceof Date)
		{
			Calendar calendar = new GregorianCalendar();
			calendar.setTime((Date)obj);
			return calendar.get(Calendar.MONTH)+1;
		}
		throw new UnsupportedOperationException(objectType(obj) + ".month() not supported!");
	}

	public static int year(Object obj)
	{
		if (obj instanceof Date)
		{
			Calendar calendar = new GregorianCalendar();
			calendar.setTime((Date)obj);
			return calendar.get(Calendar.YEAR);
		}
		throw new UnsupportedOperationException(objectType(obj) + ".year() not supported!");
	}

	public static int hour(Object obj)
	{
		if (obj instanceof Date)
		{
			Calendar calendar = new GregorianCalendar();
			calendar.setTime((Date)obj);
			return calendar.get(Calendar.HOUR_OF_DAY);
		}
		throw new UnsupportedOperationException(objectType(obj) + ".hour() not supported!");
	}

	public static int minute(Object obj)
	{
		if (obj instanceof Date)
		{
			Calendar calendar = new GregorianCalendar();
			calendar.setTime((Date)obj);
			return calendar.get(Calendar.MINUTE);
		}
		throw new UnsupportedOperationException(objectType(obj) + ".minute() not supported!");
	}

	public static int second(Object obj)
	{
		if (obj instanceof Date)
		{
			Calendar calendar = new GregorianCalendar();
			calendar.setTime((Date)obj);
			return calendar.get(Calendar.SECOND);
		}
		throw new UnsupportedOperationException(objectType(obj) + ".second() not supported!");
	}

	public static int microsecond(Object obj)
	{
		if (obj instanceof Date)
		{
			Calendar calendar = new GregorianCalendar();
			calendar.setTime((Date)obj);
			return calendar.get(Calendar.MILLISECOND)*1000;
		}
		throw new UnsupportedOperationException(objectType(obj) + ".microsecond() not supported!");
	}

	private static HashMap<Integer, Integer> weekdays;

	static
	{
		weekdays = new HashMap<Integer, Integer>();
		weekdays.put(Calendar.MONDAY, 0);
		weekdays.put(Calendar.TUESDAY, 1);
		weekdays.put(Calendar.WEDNESDAY, 2);
		weekdays.put(Calendar.THURSDAY, 3);
		weekdays.put(Calendar.FRIDAY, 4);
		weekdays.put(Calendar.SATURDAY, 5);
		weekdays.put(Calendar.SUNDAY, 6);
	}

	public static int weekday(Object obj)
	{
		if (obj instanceof Date)
		{
			Calendar calendar = new GregorianCalendar();
			calendar.setTime((Date)obj);
			return weekdays.get(calendar.get(Calendar.DAY_OF_WEEK));
		}
		throw new UnsupportedOperationException(objectType(obj) + ".weekday() not supported!");
	}

	public static int yearday(Object obj)
	{
		if (obj instanceof Date)
		{
			Calendar calendar = new GregorianCalendar();
			calendar.setTime((Date)obj);
			return calendar.get(Calendar.DAY_OF_YEAR);
		}
		throw new UnsupportedOperationException(objectType(obj) + ".yearday() not supported!");
	}

	private static Random rng = new Random();

	public static double random()
	{
		return rng.nextDouble();
	}

	public static long randrange(Object startObj, Object stopObj, Object stepObj)
	{
		long start = _toLong(startObj);
		long stop = _toLong(stopObj);
		long step = _toLong(stepObj);
		long width = stop-start;
		double value = rng.nextDouble();

		long n;
		if (step > 0)
			n = (width + step - 1) / step;
		else if (step < 0)
			n = (width + step + 1) / step;
		else
			throw new UnsupportedOperationException("step can't be 0 in randrange()");
		return start + step*((long)(value * n));
	}

	public static long randrange(Object startObj, Object stopObj)
	{
		long start = _toLong(startObj);
		long stop = _toLong(stopObj);
		long width = stop-start;
		double value = rng.nextDouble();
		return start + ((long)(value*width));
	}

	public static long randrange(Object stopObj)
	{
		long stop = _toLong(stopObj);
		double value = rng.nextDouble();
		return (long)(value*stop);
	}

	public static Object randchoice(Object obj)
	{
		if (obj instanceof String)
		{
			String str = (String)obj;
			int index = (int)(str.length() * rng.nextDouble());
			return str.substring(index, index + 1);
		}
		else if (obj instanceof List)
		{
			List lst = (List)obj;
			int index = (int)(lst.size() * rng.nextDouble());
			return lst.get(index);
		}
		else if (obj instanceof Color)
		{
			Color col = (Color)obj;
			int index = (int)(4 * rng.nextDouble());
			switch (index)
			{
				case 0:
					return col.getR();
				case 1:
					return col.getG();
				case 2:
					return col.getB();
				case 3:
					return col.getA();
			}
		}
		throw new UnsupportedOperationException("randchoice(" + objectType(obj) + ") not supported!");
	}

	public static Object startswith(Object obj, Object arg)
	{
		if (obj instanceof String && arg instanceof String)
			return ((String)obj).startsWith((String)arg);
		throw new UnsupportedOperationException(objectType(obj) + ".startswith(" + objectType(arg) + ") not supported!");
	}

	public static Object endswith(Object obj, Object arg)
	{
		if (obj instanceof String && arg instanceof String)
			return ((String)obj).endsWith((String)arg);
		throw new UnsupportedOperationException(objectType(obj) + ".endswith(" + objectType(arg) + ") not supported!");
	}

	/**
	 * Create a Map from key, value arguments.
	 * @param args An even number of objects. The objects at index 0, 2, 4, ...
	 *             are the keys, the objects at index 1, 3, 5 are the values.
	 * @return A Map containing the variables
	 */
	public static Map makeMap(Object... args)
	{
		int pos = 0;
		Object key = null;
		HashMap map = new HashMap();
		for (Object arg : args)
		{
			if ((pos & 1) != 0)
				map.put(key, arg);
			else
				key = arg;
			++pos;
		}
		return map;
	}
}
