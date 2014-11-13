/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class BoundDictMethodItems extends BoundMethod<Map>
{
	public BoundDictMethodItems(Map object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "dict.items";
	}

	public static Object call(Map object)
	{
		return new MapItemIterator(object);
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
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
