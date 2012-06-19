/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Map;
import java.util.Iterator;
import java.util.Vector;

public class MethodItems implements Method
{
	public String getName()
	{
		return "items";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return call(obj);
			default:
				throw new ArgumentCountMismatchException("method", "items", args.length, 0);
		}
	}

	public static Object call(Map obj)
	{
		return new MapItemIterator(obj);
	}

	public static Object call(Object obj)
	{
		if (obj instanceof Map)
			return call((Map)obj);
		throw new UnsupportedOperationException(Utils.objectType(obj) + ".items() not supported!");
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
