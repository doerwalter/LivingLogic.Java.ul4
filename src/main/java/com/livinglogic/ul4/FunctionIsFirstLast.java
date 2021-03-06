/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;
import java.util.Vector;

public class FunctionIsFirstLast extends Function
{
	@Override
	public String getNameUL4()
	{
		return "isfirstlast";
	}

	private static final Signature signature = new Signature().addPositionalOnly("iterable");

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(args.get(0));
	}

	public static Object call(Object obj)
	{
		return new SequenceIsFirstLast(Utils.iterator(obj));
	}

	private static class SequenceIsFirstLast implements Iterator<Vector>
	{
		Iterator sequenceIterator;

		boolean first = true;

		public SequenceIsFirstLast(Iterator sequenceIterator)
		{
			this.sequenceIterator = sequenceIterator;
		}

		public boolean hasNext()
		{
			return sequenceIterator.hasNext();
		}

		public Vector next()
		{
			Object next = sequenceIterator.next();
			Vector retVal = new Vector(3);
			retVal.add(first);
			retVal.add(!sequenceIterator.hasNext());
			retVal.add(next);
			first = false;
			return retVal;
		}

		public void remove()
		{
			sequenceIterator.remove();
		}
	}

	public static final Function function = new FunctionIsFirstLast();
}
