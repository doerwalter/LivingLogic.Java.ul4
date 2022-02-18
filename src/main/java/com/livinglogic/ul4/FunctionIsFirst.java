/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;
import java.util.Vector;

public class FunctionIsFirst extends Function
{
	@Override
	public String getNameUL4()
	{
		return "isfirst";
	}

	private static final Signature signature = new Signature().addPositionalOnly("iterable");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(args.getIterator(0));
	}

	public static Object call(Iterator iterator)
	{
		return new SequenceIsFirst(iterator);
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

	public static final Function function = new FunctionIsFirst();
}
