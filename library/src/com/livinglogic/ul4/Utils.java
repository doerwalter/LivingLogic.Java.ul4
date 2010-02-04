package com.livinglogic.ul4;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.Set;
import java.util.Date;
import java.text.SimpleDateFormat;
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
		if (obj == null)
			return "null";
		else
			return "instance of " + obj.getClass();
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
		throw new UnsupportedOperationException("Can't negate " + objectType(arg) + "!");
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
		throw new UnsupportedOperationException("Can't add " + objectType(arg1) + " and " + objectType(arg2) + "!");
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
		throw new UnsupportedOperationException("Can't subtract " + objectType(arg1) + " and " + objectType(arg2) + "!");
	}

	public static Object mul(int arg1, String arg2)
	{
		return StringUtils.repeat(arg2, arg1);
	}

	public static Object mul(long arg1, String arg2)
	{
		if (((int)arg1) != arg1)
			throw new UnsupportedOperationException("Can't multiply " + objectType(arg1) + " and " + objectType(arg2) + "!");
		return StringUtils.repeat(arg2, (int)arg1);
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
		throw new UnsupportedOperationException("Can't multiply " + objectType(arg1) + " and " + objectType(arg2) + "!");
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
		throw new UnsupportedOperationException("Can't divide " + objectType(arg1) + " and " + objectType(arg2) + "!");
	}

	public static Object floordiv(Boolean arg1, Boolean arg2)
	{
		return (arg1.booleanValue() ? 1 : 0) / (arg2.booleanValue() ? 1 : 0);
	}

	public static Object floordiv(Boolean arg1, Integer arg2)
	{
		return (arg1.booleanValue() ? 1 : 0) / arg2.intValue();
	}

	public static Object floordiv(Boolean arg1, Number arg2)
	{
		return (arg1.booleanValue() ? 1 : 0) / arg2.doubleValue();
	}

	public static Object floordiv(Integer arg1, Boolean arg2)
	{
		return arg1.intValue() / (arg2.booleanValue() ? 1 : 0);
	}

	public static Object floordiv(Integer arg1, Integer arg2)
	{
		return arg1.intValue() / arg2.intValue();
	}

	public static Object floordiv(Integer arg1, Number arg2)
	{
		return arg1.intValue() / arg2.doubleValue();
	}

	public static Object floordiv(Number arg1, Boolean arg2)
	{
		return arg1.doubleValue() / (arg2.booleanValue() ? 1 : 0);
	}

	public static Object floordiv(Number arg1, Integer arg2)
	{
		return arg1.doubleValue() / arg2.intValue();
	}

	public static Object floordiv(Number arg1, Number arg2)
	{
		return (double)((int)(arg1.doubleValue() / arg2.doubleValue()));
	}

	public static Object floordiv(Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return _toInt(arg1) / _toInt(arg2);
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
		throw new UnsupportedOperationException("Can't divide " + objectType(arg1) + " and " + objectType(arg2) + "!");
	}

	public static Object mod(Integer arg1, Integer arg2)
	{
		return arg1.intValue() % arg2.intValue();
	}

	public static Object mod(Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer && arg2 instanceof Integer)
			return mod((Integer)arg1, (Integer)arg2);
		else if (arg1 instanceof Color && arg2 instanceof Color)
			return ((Color)arg1).blend((Color)arg2);
		throw new UnsupportedOperationException("Can't apply the modulo operator to " + objectType(arg1) + " and " + objectType(arg2) + "!");
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
		throw new UnsupportedOperationException(objectType(arg1) + " don't not support getitem with " + objectType(arg2) + " as index!");
	}

	private static int getSliceStartPos(int sequenceSize, Integer virtualPos)
	{
		int retVal;
		if (null == virtualPos)
		{
			retVal = 0;
		}
		else
		{
			retVal = virtualPos.intValue();
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
		}
		return retVal;
	}

	private static int getSliceEndPos(int sequenceSize, Integer virtualPos)
	{
		int retVal;
		if (null == virtualPos)
		{
			retVal = sequenceSize;
		}
		else
		{
			retVal = virtualPos;
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
		}
		return retVal;
	}

	public static Object getSlice(List arg1, Integer arg2, Integer arg3)
	{
		int size = arg1.size();
		int start = getSliceStartPos(size, arg2);
		int end = getSliceEndPos(size, arg3);
		if (end < start)
			end = start;
		return arg1.subList(start, end);
	}

	public static Object getSlice(String arg1, Integer arg2, Integer arg3)
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
			return getSlice((List)arg1, (Integer)arg2, (Integer)arg3);
		else if (arg1 instanceof String)
			return getSlice((String)arg1, (Integer)arg2, (Integer)arg3);
		throw new UnsupportedOperationException(objectType(arg1) + " don't support getslice with " + objectType(arg2) + " and " + objectType(arg3) + " as indices!");
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

	public static boolean lt(Object obj1, Object obj2)
	{
		if (null != obj1)
		{
			if (null != obj2)
				return ((Comparable)obj1).compareTo(obj2) < 0;
		}
		else if (null == obj2)
			return false;
		throw new RuntimeException("Can't compare object to null!");
	}

	public static boolean le(Object obj1, Object obj2)
	{
		if (null != obj1)
		{
			if (null != obj2)
				return ((Comparable)obj1).compareTo(obj2) <= 0;
		}
		else if (null == obj2)
			return true;
		throw new RuntimeException("Can't compare object to null!");
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
			return contains(obj, (String)container);
		else if (container instanceof Collection)
			return contains(obj, (Collection)container);
		else if (container instanceof Map)
			return contains(obj, (Map)container);
		throw new RuntimeException("Can't determine presence for " + objectType(obj) + " in " + objectType(container) + " container!");
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
		throw new UnsupportedOperationException("Can't take absolute value of " + objectType(arg) + "!");
	}

	public static String xmlescape(Object obj)
	{
		if (obj == null)
			return "";

		String str = ObjectUtils.toString(obj);
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

	public static String csv(Object obj)
	{
		if (obj == null)
			return "";
		if (!(obj instanceof String))
			obj = repr(obj);
		return StringEscapeUtils.escapeCsv((String)obj);
	}

	public static Object toInteger(String obj)
	{
		return Integer.valueOf(obj);
	}

	public static Object toInteger(Integer obj)
	{
		return obj;
	}

	public static Object toInteger(Long obj)
	{
		return obj;
	}

	public static Object toInteger(Number obj)
	{
		return new Integer(obj.intValue());
	}

	public static Object toInteger(Boolean obj)
	{
		return obj.booleanValue() ? INTEGER_TRUE : INTEGER_FALSE;
	}

	public static Object toInteger(Object obj)
	{
		if (obj instanceof String)
			return toInteger((String)obj);
		else if (obj instanceof Integer)
			return toInteger((Integer)obj);
		else if (obj instanceof Long)
			return toInteger((Long)obj);
		else if (obj instanceof Number)
			return toInteger((Number)obj);
		else if (obj instanceof Boolean)
			return toInteger((Boolean)obj);
		throw new UnsupportedOperationException("Can't convert " + objectType(obj) + " to an integer!");
	}

	public static Object toInteger(String obj1, Integer obj2)
	{
		return Integer.valueOf(obj1, obj2.intValue());
	}

	public static Object toInteger(Object obj1, Object obj2)
	{
		if (obj1 instanceof String && obj2 instanceof Integer)
		{
			return toInteger((String)obj1, (Integer)obj2);
		}
		throw new UnsupportedOperationException("Can't convert " + objectType(obj1) + " to an integer using " + objectType(obj2) + " as base!");
	}

	public static Object toFloat(String obj)
	{
		return Float.valueOf(obj);
	}

	public static Object toFloat(Integer obj)
	{
		return (double)(obj.intValue());
	}

	public static Object toFloat(Long obj)
	{
		return (double)(obj.longValue());
	}

	public static Object toFloat(Float obj)
	{
		return (double)obj;
	}

	public static Object toFloat(Boolean obj)
	{
		return obj.booleanValue() ? 1.0d : 0.0d;
	}

	public static Object toFloat(Object obj)
	{
		if (obj instanceof String)
			return toFloat((String)obj);
		else if (obj instanceof Integer)
			return toFloat((Integer)obj);
		else if (obj instanceof Long)
			return toFloat((Long)obj);
		else if (obj instanceof Number)
			return toFloat((Number)obj);
		else if (obj instanceof Boolean)
			return toFloat((Boolean)obj);
		throw new UnsupportedOperationException("Can't convert " + objectType(obj) + " to a float!");
	}

	public static String repr(Object obj)
	{
		if (obj == null)
			return "None";
		else if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue() ? "True" : "False";
		else if (obj instanceof Integer)
			return String.valueOf(((Integer)obj).intValue());
		else if (obj instanceof Long)
			return String.valueOf(((Long)obj).longValue());
		else if (obj instanceof Double)
			return String.valueOf(((Double)obj).doubleValue());
		else if (obj instanceof String)
			return new StringBuffer()
				.append("\"")
				.append(StringEscapeUtils.escapeJava(((String)obj)))
				.append("\"")
				.toString();
		else if (obj instanceof Date)
			return isoformat((Date)obj);
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
	
	public static String json(Object obj)
	{
		if (obj == null)
			return "null";
		else if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue() ? "true" : "false";
		else if (obj instanceof Integer)
			return String.valueOf(((Integer)obj).intValue());
		else if (obj instanceof Long)
			return String.valueOf(((Long)obj).longValue());
		else if (obj instanceof Double)
			return String.valueOf(((Double)obj).doubleValue());
		else if (obj instanceof String)
			return new StringBuffer()
				.append("\"")
				.append(StringEscapeUtils.escapeJavaScript(((String)obj)))
				.append("\"")
				.toString();
		else if (obj instanceof Date)
			return json(isoformat((Date)obj));
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
		return null;
	}
	
	public static Iterator reversed(Object obj)
	{
		if (obj instanceof String)
			return new StringReversedIterator((String)obj);
		else if (obj instanceof List)
			return new ListReversedIterator((List)obj);
		throw new UnsupportedOperationException("Can't created reversed iterator for " + objectType(obj) + "!");
	}
	
	public static Object length(String obj)
	{
		return obj.length();
	}

	public static Object length(Collection obj)
	{
		return obj.size();
	}

	public static Object length(Map obj)
	{
		return obj.size();
	}

	public static Object length(Object obj)
	{
		if (obj instanceof String)
			return length((String)obj);
		else if (obj instanceof Collection)
			return length((Collection)obj);
		else if (obj instanceof Map)
			return length((Map)obj);
		throw new UnsupportedOperationException("Can't determine length for " + objectType(obj) + "!");
	}

	public static Iterator iterator(String obj)
	{
		return new StringIterator(obj);
	}

	public static Iterator iterator(Iterable obj)
	{
		return obj.iterator();
	}

	public static Iterator iterator(Map obj)
	{
		return obj.keySet().iterator();
	}

	public static Iterator iterator(Object obj)
	{
		if (obj instanceof String)
			return iterator((String)obj);
		else if (obj instanceof Iterable)
			return iterator((Iterable)obj);
		else if (obj instanceof Map)
			return iterator((Map)obj);
		else if (obj instanceof Iterator)
			return (Iterator)obj;
		throw new UnsupportedOperationException("Can't iterate over " + objectType(obj) + "!");
	}

	public static Object enumerate(Object obj)
	{
		return new SequenceEnumerator(iterator(obj));
	}

	public static Object chr(Integer obj)
	{
		int intValue = obj.intValue();
		char charValue = (char)intValue;
		if (intValue != (int)charValue)
		{
			throw new IndexOutOfBoundsException("Code point " + intValue + " is invalid!");
		}
		return String.valueOf(charValue);
	}

	public static Object chr(Object obj)
	{
		if (obj instanceof Integer)
			return chr((Integer)obj);
		throw new UnsupportedOperationException(objectType(obj) + " is not a valid unicode codepoint!");
	}

	public static Object ord(String obj)
	{
		if (1 != obj.length())
		{
			throw new IllegalArgumentException("String " + obj + " contains more than one unicode character!");
		}
		return (int)obj.charAt(0);
	}

	public static Object ord(Object obj)
	{
		if (obj instanceof String)
			return chr((String)obj);
		throw new UnsupportedOperationException("Can't determine unicode code point for " + objectType(obj) + "!");
	}

	public static Object hex(Integer obj)
	{
		return "0x" + Integer.toHexString(obj);
	}

	public static Object hex(Object obj)
	{
		if (obj instanceof Integer)
			return hex((Integer)obj);
		throw new UnsupportedOperationException(objectType(obj) + " can't be represented as a hexadecimal string!");
	}

	public static Object oct(Integer obj)
	{
		return "0o" + Integer.toOctalString(obj);
	}

	public static Object oct(Object obj)
	{
		if (obj instanceof Integer)
			return oct((Integer)obj);
		throw new UnsupportedOperationException(objectType(obj) + " can't be represented as an octal string!");
	}

	public static Object bin(Integer obj)
	{
		return "0b" + Integer.toBinaryString(obj);
	}

	public static Object bin(Object obj)
	{
		if (obj instanceof Integer)
			return chr((Integer)obj);
		throw new UnsupportedOperationException(objectType(obj) + " can't be represented as a binary string!");
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

	public static Object sorted(Object obj)
	{
		if (obj instanceof String)
			return sorted((String)obj);
		else if (obj instanceof Collection)
			return sorted((Collection)obj);
		else if (obj instanceof Map)
			return sorted((Map)obj);
		throw new RuntimeException("Can't sort " + objectType(obj) + "!");
	}

	public static Object range(Integer obj)
	{
		return new Range(0, obj, 1);
	}

	public static Object range(Object obj)
	{
		if (obj instanceof Integer)
			return range((Integer)obj);
		throw new UnsupportedOperationException("Can't build a range from " + objectType(obj) + "!");
	}

	public static Object range(Integer obj1, Integer obj2)
	{
		return new Range(obj1, obj2, 1);
	}

	public static Object range(Object obj1, Object obj2)
	{
		if (obj1 instanceof Integer && obj2 instanceof Integer)
			return range((Integer)obj1, (Integer)obj2);
		throw new UnsupportedOperationException("Can't build a range from " + objectType(obj1) + " and " + objectType(obj2) + "!");
	}

	public static Object range(Integer obj1, Integer obj2, Integer obj3)
	{
		return new Range(obj1, obj2, obj3);
	}

	public static Object range(Object obj1, Object obj2, Object obj3)
	{
		if (obj1 instanceof Integer && obj2 instanceof Integer && obj3 instanceof Integer)
			return range((Integer)obj1, (Integer)obj2, (Integer)obj3);
		throw new UnsupportedOperationException("Can't build a range from " + objectType(obj1) + " and " + objectType(obj2) + " and " + objectType(obj3) + "!");
	}

	public static Object zip(Object obj1, Object obj2)
	{
		return new ZipIterator(iterator(obj1), iterator(obj2));
	}

	public static Object zip(Object obj1, Object obj2, Object obj3)
	{
		return new ZipIterator(iterator(obj1), iterator(obj2), iterator(obj3));
	}

	public static Object split(String obj1, String obj2)
	{
		LinkedList<String> retVal = new LinkedList<String>();
		int length = obj1.length();
		int delimLength = obj2.length();
		int pos1 = 0;
		int pos2;
		while (pos1 < length)
		{
			while ((pos1 < length) && obj1.startsWith(obj2, pos1))
			{
				if (0 == pos1)
				{
					retVal.add("");
				}
				pos1 += delimLength;
				retVal.add("");
			}
			if (pos1 < length)
			{
				pos2 = pos1 + 1;
				if (!retVal.isEmpty())
				{
					retVal.removeLast();
				}
				while ((pos2 < length) && !obj1.startsWith(obj2, pos2))
				{
					pos2++;
				}
				retVal.add(obj1.substring(pos1, pos2));
				pos1 = pos2;
			}
		}
		return retVal;
	}

	public static Object split(Object obj)
	{
		if (obj instanceof String)
			return StringUtils.split((String)obj);
		throw new UnsupportedOperationException("Can't split " + objectType(obj) + "!");
	}

	public static Object split(Object obj1, Object obj2)
	{
		if ((obj1 instanceof String) && (obj2 instanceof String))
			return split((String)obj1, (String)obj2);
		throw new UnsupportedOperationException("Can't split " + objectType(obj1) + " with delimiter " + objectType(obj2) + "!");
	}

	public static Object strip(Object obj)
	{
		if (obj instanceof String)
			return StringUtils.strip((String)obj);
		throw new UnsupportedOperationException("Can't strip " + objectType(obj) + "!");
	}

	public static Object lstrip(Object obj)
	{
		if (obj instanceof String)
			return StringUtils.stripStart((String)obj, null);
		throw new UnsupportedOperationException("Can't lstrip " + objectType(obj) + "!");
	}

	public static Object rstrip(Object obj)
	{
		if (obj instanceof String)
			return StringUtils.stripEnd((String)obj, null);
		throw new UnsupportedOperationException("Can't rstrip " + objectType(obj) + "!");
	}

	public static Object upper(String obj)
	{
		return obj.toUpperCase();
	}

	public static Object upper(Object obj)
	{
		if (obj instanceof String)
			return upper((String)obj);
		throw new UnsupportedOperationException("Can't convert " + objectType(obj) + " to upper case!");
	}

	public static Object lower(String obj)
	{
		return obj.toLowerCase();
	}

	public static Object lower(Object obj)
	{
		if (obj instanceof String)
			return lower((String)obj);
		throw new UnsupportedOperationException("Can't convert " + objectType(obj) + " to lower case!");
	}

	public static Object capitalize(String obj)
	{
		return String.valueOf(Character.toTitleCase(obj.charAt(0))) + obj.substring(1).toLowerCase();
	}

	public static Object capitalize(Object obj)
	{
		if (obj instanceof String)
			return capitalize((String)obj);
		throw new UnsupportedOperationException("Can't convert " + objectType(obj) + " to capital case!");
	}

	public static SimpleDateFormat isoDateFormatter = new SimpleDateFormat("yyyy.MM.dd'T'HH:mm:ss.SSS'000'");

	public static String isoformat(Date obj)
	{
		return isoDateFormatter.format(obj);
	}

	public static String isoformat(Object obj)
	{
		if (obj instanceof Date)
			return isoformat((Date)obj);
		throw new UnsupportedOperationException("Can't call isoformat on " + objectType(obj) + "!");
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
						throw new UnsupportedOperationException("Unimplemented escape sequence %c");
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
						throw new UnsupportedOperationException("Unimplemented escape sequence %w");
					case 'W':
						javaFormatSequence = "ww";
						break;
					case 'x':
						throw new UnsupportedOperationException("Unimplemented escape sequence %x");
					case 'X':
						throw new UnsupportedOperationException("Unimplemented escape sequence %X");
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
		throw new UnsupportedOperationException("Can't call format on " + objectType(obj) + " with " + objectType(formatString) + " as format string!");
	}

	public static Object replace(Object obj, Object arg1, Object arg2)
	{
		if (obj instanceof String && arg1 instanceof String && arg2 instanceof String)
			return StringUtils.replace((String)obj, (String)arg1, (String)arg2);
		throw new UnsupportedOperationException("Can't call replace on " + objectType(obj) + "!");
	}

	public static Object find(Object obj, Object arg1)
	{
		if (obj instanceof String && arg1 instanceof String)
			return ((String)obj).indexOf((String)arg1);
		throw new UnsupportedOperationException("Can't call find on " + objectType(obj) + "!");
	}

	public static Object rfind(Object obj, Object arg1)
	{
		if (obj instanceof String && arg1 instanceof String)
			return ((String)obj).lastIndexOf((String)arg1);
		throw new UnsupportedOperationException("Can't call rfind on " + objectType(obj) + "!");
	}

	public static Object items(Map obj)
	{
		return new MapItemIterator(obj);
	}

	public static Object items(Object obj)
	{
		if (obj instanceof Map)
			return items((Map)obj);
		throw new UnsupportedOperationException(objectType(obj) + " can't be iterated as a map!");
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

	public static String join(Object arg1, Object arg2)
	{
		if (arg1 instanceof String)
		{
			StringBuffer buffer = new StringBuffer();

			for (Iterator iter = iterator(arg2); iter.hasNext();)
			{
				Object item = iter.next();
				if (buffer.length() != 0)
					buffer.append(arg1);
				if (item != null)
					buffer.append(item);
			}
			return buffer.toString();
		}
		else
			throw new UnsupportedOperationException("can't call join on " + objectType(arg1) + "!");
	}
}
