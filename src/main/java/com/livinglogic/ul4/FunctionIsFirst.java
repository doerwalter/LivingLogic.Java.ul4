/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;
import java.util.Vector;

public class FunctionIsFirst implements Function
{
	public String getName()
	{
		return "isfirst";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "isfirst", args.length, 1);
	}

	public static Object call(Object obj)
	{
		return new SequenceIsFirst(Utils.iterator(obj));
	}

	private static class SequenceIsFirst implements Iterator<Vector>
	{
		Iterator sequenceIterator;

		boolean first = true;

		public SequenceIsFirst(Iterator sequenceIterator)
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
			retVal.add(first);
			retVal.add(sequenceIterator.next());
			first = false;
			return retVal;
		}

		public void remove()
		{
			sequenceIterator.remove();
		}
	}
}
