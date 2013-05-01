/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class MethodItems extends NormalMethod
{
	public String nameUL4()
	{
		return "items";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args)
	{
		return call(obj);
	}

	public static Object call(Map obj)
	{
		return new MapItemIterator(obj);
	}

	public static Object call(UL4Attributes obj)
	{
		return new UL4AttributeItemsIterator(obj);
	}

	public static Object call(Object obj)
	{
		if (obj instanceof Map)
			return call((Map)obj);
		else if (obj instanceof UL4Attributes)
			return call((UL4Attributes)obj);
		throw new ArgumentTypeMismatchException("{}.items()", obj);
	}

	private static class MapItemIterator implements Iterator<Vector>
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

	private static class UL4AttributeItemsIterator implements Iterator<Vector>
	{
		UL4Attributes obj;
		Iterator<String> iterator;

		public UL4AttributeItemsIterator(UL4Attributes obj)
		{
			this.obj = obj;
			this.iterator = obj.getAttributeNamesUL4().iterator();
		}

		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		public Vector next()
		{
			Vector retVal = new Vector(2);
			String attributeName = iterator.next();
			retVal.add(attributeName);
			retVal.add(obj.getItemStringUL4(attributeName));
			return retVal;
		}

		public void remove()
		{
			iterator.remove();
		}
	}
}
