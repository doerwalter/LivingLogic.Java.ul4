/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class MethodItems extends NormalMethod
{
	public String nameUL4()
	{
		return "items";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args) throws IOException
	{
		return call(obj);
	}

	public static Object call(Map obj)
	{
		return new MapItemIterator(obj);
	}

	public static Object call(Object obj)
	{
		if (obj instanceof Map)
			return call((Map)obj);
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
}
