package com.livinglogic.sxtl;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

class StringIterator implements Iterator
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

	public Object next()
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

class SequenceEnumerator implements Iterator
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

	public Object next()
	{
		Vector<Object> retVal = new Vector<Object>(2);
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

	public static boolean equals(Object obj1, Object obj2)
	{
		if (null != obj1)
			return obj1.equals(obj2);
		else
			return (null == obj2);
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
		throw new RuntimeException("Can't determine presence for instance of " + obj.getClass() + " in container instance of class " + container.getClass() + "!");
	}

	public static Object xmlescape(String obj)
	{
		int length = obj.length();
		StringBuffer sb = new StringBuffer((int)(1.2 * length));
		for (int offset = 0; offset < length; offset++)
		{
			char c = obj.charAt(offset);
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

	public static Object xmlescape(Object obj)
	{
		throw new UnsupportedOperationException("Can't xmlescape instance of " + obj.getClass() + "!");
	}

	public static Object toString(Object obj)
	{
		return obj != null ? obj.toString() : ""; 
	}

	public static Object toInteger(String obj)
	{
		return Integer.valueOf(obj);
	}

	public static Object toInteger(Integer obj)
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
		throw new UnsupportedOperationException("Can't convert instance of " + obj.getClass() + " to an integer!");
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
		throw new UnsupportedOperationException("Can't determine length for instance of " + obj.getClass() + "!");
	}

	public static Iterator iterator(String obj)
	{
		return new StringIterator(obj);
	}

	public static Iterator iterator(Collection obj)
	{
		return obj.iterator();
	}

	public static Iterator iterator(Map obj)
	{
		return obj.keySet().iterator();
	}

	public static Iterator iterator(Object obj)
	{
		throw new UnsupportedOperationException("Can't iterate instance of " + obj.getClass() + "!");
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
		throw new UnsupportedOperationException("Instance of " + obj.getClass() + " is no valid unicode codepoint!");
	}

	public static Object ord(String obj)
	{
		if (1 != obj.length())
		{
			throw new IllegalArgumentException("String " + obj + " contains more than one unicode character!"); 
		}
		return new Integer((int)obj.charAt(0));
	}

	public static Object ord(Object obj)
	{
		throw new UnsupportedOperationException("Can't determine unicode code point for instance of " + obj.getClass() + "!");
	}

	public static Object hex(Integer obj)
	{
		return "0x" + Integer.toHexString(obj);
	}

	public static Object hex(Object obj)
	{
		throw new UnsupportedOperationException("Instance of " + obj.getClass() + " can't be represented as a hexadecimal string!");
	}

	public static Object oct(Integer obj)
	{
		return "0o" + Integer.toOctalString(obj);
	}

	public static Object oct(Object obj)
	{
		throw new UnsupportedOperationException("Instance of " + obj.getClass() + " can't be represented as an octal string!");
	}

	public static Object bin(Integer obj)
	{
		return "0b" + Integer.toBinaryString(obj);
	}

	public static Object bin(Object obj)
	{
		throw new UnsupportedOperationException("Instance of " + obj.getClass() + " can't be represented as a binary string!");
	}

	public static Object split(String obj)
	{
		return obj.trim().split("\\s+");
	}

	public static Object split(Object obj)
	{
		throw new UnsupportedOperationException("Can't split instance of " + obj.getClass() + "!");
	}

	public static Object items(Map obj)
	{
		return new MapItemIterator(obj); 
	}

	public static Object items(Object obj)
	{
		throw new UnsupportedOperationException("Instance of " + obj.getClass() + " can't be iterated as a map!");
	}
	
	public static void main(String[] args)
	{
		System.out.println(java.util.Arrays.asList((String[])split("    gurk       hurz  schwumpl  ")));
	}
}
