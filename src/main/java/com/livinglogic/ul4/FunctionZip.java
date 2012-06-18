/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;
import java.util.Vector;

public class FunctionZip implements Function
{
	private static class ZipIterator implements Iterator<Vector>
	{
		Iterator[] iterators;

		public ZipIterator(Object... iterators)
		{
			this.iterators = new Iterator[iterators.length];
			for (int i = 0; i < iterators.length; ++i)
				this.iterators[i] = Utils.iterator(iterators[i]);
		}

		public boolean hasNext()
		{
			if (iterators.length == 0)
				return false;
			for (int i = 0; i < iterators.length; ++i)
			{
				if (!iterators[i].hasNext())
					return false;
			}
			return true;
		}

		public Vector next()
		{
			Vector result = new Vector(iterators.length);
			for (int i = 0; i < iterators.length; ++i)
				result.add(iterators[i].next());
			return result;
		}

		public void remove()
		{
			for (int i = 0; i < iterators.length; ++i)
				iterators[i].remove();
		}
	}

	public static Object call(Object... objs)
	{
		return new ZipIterator(objs);
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		return call(args);
	}

	public String getName()
	{
		return "zip";
	}
}
