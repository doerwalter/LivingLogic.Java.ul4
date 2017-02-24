/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;
import java.util.Vector;

public class FunctionEnumFL extends Function
{
	public String nameUL4()
	{
		return "enumfl";
	}

	private static final Signature signature = new Signature("iterable", Signature.required, "start", 0);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0), args.get(1));
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
			retVal.add(new Integer(index++));
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
}
