/*
** Copyright 2009-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;
import java.util.Vector;

public class FunctionEnumFL extends Function
{
	@Override
	public String getNameUL4()
	{
		return "enumfl";
	}

	private static final Signature signature = new Signature().addPositionalOnly("iterable").addBoth("start", 0);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(args.getIterator(0), args.getInt(1));
	}

	public static Object call(Iterator iterator)
	{
		return new SequenceEnumFL(iterator, 0);
	}

	public static Object call(Iterator iterator, int start)
	{
		return new SequenceEnumFL(iterator, start);
	}

	public static Object call(Object obj)
	{
		return new SequenceEnumFL(Utils.iterator(obj), 0);
	}

	public static Object call(Object obj, Object start)
	{
		return new SequenceEnumFL(Utils.iterator(obj), Utils.toInt(start));
	}

	private static class SequenceEnumFL implements Iterator<Vector>
	{
		Iterator sequenceIterator;

		int index;
		int start;

		public SequenceEnumFL(Iterator sequenceIterator, int start)
		{
			this.sequenceIterator = sequenceIterator;
			this.index = this.start = start;
		}

		public boolean hasNext()
		{
			return sequenceIterator.hasNext();
		}

		public Vector next()
		{
			Object next = sequenceIterator.next();
			Vector retVal = new Vector(4);
			retVal.add(index++);
			retVal.add(index == start+1);
			retVal.add(!sequenceIterator.hasNext());
			retVal.add(next);
			return retVal;
		}

		public void remove()
		{
			sequenceIterator.remove();
		}
	}

	public static final Function function = new FunctionEnumFL();
}
