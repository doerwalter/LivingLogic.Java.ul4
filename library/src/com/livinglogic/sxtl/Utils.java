package com.livinglogic.sxtl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

class MapItemIterator implements Iterator
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

	public Object next()
	{
		Vector<Object> retVal = new Vector<Object>(2);
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

public class Utils
{
	protected static final Integer INTEGER_TRUE = new Integer(1);
	
	protected static final Integer INTEGER_FALSE = new Integer(0);
	
	public static Object add(Integer arg1, Integer arg2)
	{
		return new Integer(arg1.intValue() + arg2.intValue());
	}
	
	public static Object add(Number arg1, Number arg2)
	{
		return new Double(arg1.doubleValue() + arg2.doubleValue());
	}
	
	public static Object add(String arg1, String arg2)
	{
		return arg1 + arg2;
	}

	public static Object add(Object arg1, Object arg2)
	{
		throw new UnsupportedOperationException("Can't add instances of " + arg1.getClass() + " and " + arg2.getClass() + "!");
	}

	public static Object sub(Integer arg1, Integer arg2)
	{
		return new Integer(arg1.intValue() - arg2.intValue());
	}
	
	public static Object sub(Number arg1, Number arg2)
	{
		return new Double(arg1.doubleValue() - arg2.doubleValue());
	}

	public static Object sub(Object arg1, Object arg2)
	{
		throw new UnsupportedOperationException("Can't subtract instances of " + arg1.getClass() + " and " + arg2.getClass() + "!");
	}

	public static Object mul(String arg1, Integer arg2)
	{
		return mul(arg2, arg1);
	}

	public static Object mul(Integer arg1, String arg2)
	{
		int count = arg1.intValue();
		StringBuffer retValBuffer = new StringBuffer(count * arg2.length());
		for (int i = 0; i < count; i++)
		{
			retValBuffer.append(arg2);
		}
		return retValBuffer.toString();
	}

	public static Object mul(Integer arg1, Integer arg2)
	{
		return new Integer(arg1.intValue() * arg2.intValue());
	}

	public static Object mul(Number arg1, Number arg2)
	{
		return new Double(arg1.doubleValue() * arg2.doubleValue());
	}

	public static Object mul(Object arg1, Object arg2)
	{
		throw new UnsupportedOperationException("Can't multiply instances of " + arg1.getClass() + " and " + arg2.getClass() + "!");
	}

	public static Object truediv(Number arg1, Number arg2)
	{
		return new Double(arg1.doubleValue() / arg2.doubleValue());
	}

	public static Object truediv(Object arg1, Object arg2)
	{
		throw new UnsupportedOperationException("Can't divide instances of " + arg1.getClass() + " and " + arg2.getClass() + "!");
	}

	public static Object floordiv(Integer arg1, Integer arg2)
	{
		return new Integer(arg1.intValue() / arg2.intValue());
	}

	public static Object floordiv(Number arg1, Number arg2)
	{
		return new Double((int)(arg1.doubleValue() / arg2.doubleValue()));
	}

	public static Object floordiv(Object arg1, Object arg2)
	{
		throw new UnsupportedOperationException("Can't divide instances of " + arg1.getClass() + " and " + arg2.getClass() + "!");
	}

	public static Object mod(Integer arg1, Integer arg2)
	{
		return new Integer(arg1.intValue() % arg2.intValue());
	}

	public static Object mod(Object arg1, Object arg2)
	{
		throw new UnsupportedOperationException("Can't apply the modulo operator to instances of " + arg1.getClass() + " and " + arg2.getClass() + "!");
	}

	public static Object getItem(Map arg1, Object arg2)
	{
		return arg1.get(arg2);
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

	public static Object getItem(String arg1, Integer arg2)
	{
		int index = arg2.intValue();
		if (0 > index)
		{
			index += arg1.length();
		}
		return arg1.substring(index, index + 1);
	}

	public static Object getItem(Object arg1, Object arg2)
	{
		throw new UnsupportedOperationException("Instance of " + arg1.getClass() + " does not support getitem with argument of type " + arg2.getClass() + "!");
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
			retVal = virtualPos.intValue();
			if (0 > retVal)
			{
				retVal += sequenceSize;
			}
			if (sequenceSize < retVal)
			{
				retVal = sequenceSize;
			}
		}
		return retVal;
	}

	public static Object getSlice(List arg1, Integer arg2, Integer arg3)
	{
		int size = arg1.size();
		return arg1.subList(getSliceStartPos(size, arg2), getSliceEndPos(size, arg3));
	}

	public static Object getSlice(String arg1, Integer arg2, Integer arg3)
	{
		int size = arg1.length();
		return arg1.substring(getSliceStartPos(size, arg2), getSliceEndPos(size, arg3));
	}

	public static Object getSlice(Object arg1, Object arg2, Object arg3)
	{
		throw new UnsupportedOperationException("Instance of " + arg1.getClass() + " does not support getsclice with arguments of type " + arg2.getClass() + " and " + arg3.getClass() + "!");
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

	public static boolean getBool(Double obj)
	{
		return (obj.doubleValue() != 0.);
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
		return false;
	}

	public static Iterator getForIterator(Collection obj)
	{
		return obj.iterator();
	}

	public static Iterator getForIterator(Map obj)
	{
		return obj.keySet().iterator();
	}

	public static Iterator getForIterator(Object obj)
	{
		throw new UnsupportedOperationException("Instance of " + obj.getClass() + " does not support iteration!");
	}

	public static Iterator getForItemsIterator(Object obj)
	{
		return new MapItemIterator((Map)obj); 
	}

	public static Object xmlescape(String obj)
	{
		int length = obj.length();
		StringBuffer sb = new StringBuffer((int)(1.2 * length));
		for (int offset = 0; offset < length; offset++)
		{
			char c = text.charAt(offset);
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
					sb.append("&apos;");
					break;
				case '"':
					sb.append("&quot;");
					break;
				case '\t':
					sb.append(c);
					break;
				case '\n'
					sb.append(c);
					break;
				case '\r'
					sb.append(c);
					break;
				case '\u0085'
					sb.append(c);
					break;
				default:
					if ((('\u0020' <= c) && (c <= '\u007e')) || ('00A0' <= c))
						sb.append(c);
					else
						sb.append("&#").append((int)character).append(';');	
					break;
			}
		}
		return sb.toString();
	}

	public static Object xmlescape(Object obj)
	{
		throw new UnsupportedOperationException("Can't xmlescape instance of " + obj.getClass() + "!");
	}

	public static Object toInteger(Object obj)
	{
		if (obj instanceof String)
			return Integer.valueOf((String)obj);
		else if (obj instanceof Integer)
			return obj;
		else if (obj instanceof Double)
			return new Integer(((Double)obj).intValue());
		else if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue() ? INTEGER_TRUE : INTEGER_FALSE; 
		throw new RuntimeException("type of " + obj + " not convertable to integer");
	}

	public static Object toString(Object obj)
	{
		return obj != null ? obj.toString() : ""; 
	}

	public static boolean contains(Object obj, Object container)
	{
		if (container instanceof Collection)
			return ((Collection)container).contains(obj);
		else if (container instanceof Map)
			return ((Map)container).containsKey(obj);
		else if (container instanceof String)
			return ((String)container).indexOf((String)obj) >= 0;
		throw new RuntimeException("type of " + container + " not supported");
	}

	public static boolean equals(Object obj1, Object obj2)
	{
		if (null != obj1)
			return obj1.equals(obj2);
		else
			return (null == obj2);
	}
}
