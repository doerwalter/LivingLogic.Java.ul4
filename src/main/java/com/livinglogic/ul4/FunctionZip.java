/*
** Copyright 2009-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;
import java.util.Vector;

public class FunctionZip extends Function
{
	@Override
	public String getNameUL4()
	{
		return "zip";
	}

	private static final Signature signature = new Signature().addVarPositional("iterables");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call((List<Object>)args.get(0));
	}

	public static Iterator call(List<Object> iterables)
	{
		return new ZipIterator(iterables);
	}

	private static class ZipIterator implements Iterator<Vector>
	{
		Iterator[] iterators;

		public ZipIterator(List<Object> iterables)
		{
			iterators = new Iterator[iterables.size()];
		
			for (int i = 0; i < iterators.length; ++i)
				this.iterators[i] = Utils.iterator(iterables.get(i));
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

	public static final Function function = new FunctionZip();
}
