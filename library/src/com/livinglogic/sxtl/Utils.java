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
		List retVal = new Vector(2);
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
	protected static final Integer INTEGER_TRUE = 1;
	
	protected static final Integer INTEGER_FALSE = 0;
	
	public static Object add(Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer)
		{
			if (arg2 instanceof Integer)
			{
				return new Integer(((Integer)arg1).intValue() + ((Integer)arg2).intValue());
			}
			else if (arg2 instanceof Double)
			{
				return new Double(((Integer)arg1).intValue() + ((Double)arg2).doubleValue());
			}
		}
		else if (arg1 instanceof Double)
		{
			if (arg2 instanceof Integer)
			{
				return new Double(((Double)arg1).doubleValue() + ((Integer)arg2).intValue());
			}
			else if (arg2 instanceof Double)
			{
				return new Double(((Double)arg1).doubleValue() + ((Double)arg2).doubleValue());
			}
		}
		throw new RuntimeException("type of " + arg1 + " and " + arg2 + " do not support addition");
	}

	public static Object getItem(Object arg1, Object arg2)
	{
		if (arg1 instanceof Map)
		{
			return ((Map)arg1).get(arg2);
		}
		else if (arg1 instanceof List)
		{
			int index = ((Integer)arg2).intValue();
			if (index < 0)
				index += ((List)arg1).size();
			return ((List)arg1).get(index);
		}
		else if (arg1 instanceof String)
		{
			int index = ((Integer)arg2).intValue();
			if (index < 0)
				index += ((String)arg1).length();
			return ((String)arg1).substring(index, index + 1);
		}
		throw new RuntimeException("type of " + arg1 + " does not support getitem()");
	}

	public static Object getSlice(Object arg1, Object arg2, Object arg3)
	{
		int size;

		if (arg1 instanceof List)
			size = ((List)arg1).size();
		else if (arg1 instanceof String)
			size = ((String)arg1).length();
		else
			throw new RuntimeException("type of " + arg1 + " does not support getslice()");

		int index1;
		if (arg2 == null)
			index1 = 0;
		else
		{
			index1 = ((Integer)arg2).intValue();
			if (index1 < 0)
				index1 += size;
			if (index1 < 0)
				index1 = 0;
		}
		int index2;
		if (arg3 == null)
			index2 = size;
		else
		{
			index2 = ((Integer)arg3).intValue();
			if (index2 < 0)
				index2 += size;
			if (index2 > size)
				index2 = size;
		}

		if (arg1 instanceof List)
			return ((List)arg1).subList(index1, index2);
		else
			return ((String)arg1).substring(index1, index2);
	}

	public static boolean getBool(Object obj)
	{
		if (obj == null)
			return false;
		else if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue();
		else if (obj instanceof String)
			return (((String)obj).length() > 0);
		else if (obj instanceof Integer)
			return (((Integer)obj).intValue() != 0);
		else if (obj instanceof Double)
			return (((Double)obj).doubleValue() != 0.);
		else if (obj instanceof Collection)
			return !((Collection)obj).isEmpty();
		else if (obj instanceof Map)
			return !((Map)obj).isEmpty();
		else
			return false;
	}

	public static Iterator getForIterator(Object obj)
	{
		if (obj instanceof Collection)
			return ((Collection)obj).iterator();
		else if (obj instanceof Map)
			return ((Map)obj).keySet().iterator();

		throw new RuntimeException("type of " + obj + " not iterable");
	}

	public static Iterator getForItemsIterator(Object obj)
	{
		return new MapItemIterator((Map)obj); 
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
