/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;
import java.util.Vector;

public class FunctionEnumerate extends Function
{
	public String nameUL4()
	{
		return "enumerate";
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

	public static Object call(Object obj, Object start)
	{
		return new SequenceEnumerator(Utils.iterator(obj), Utils.toInt(start));
	}

	public static Object call(Object obj)
	{
		return new SequenceEnumerator(Utils.iterator(obj), 0);
	}

	private static class SequenceEnumerator implements Iterator<Vector>
	{
		Iterator sequenceIterator;

		int index;

		public SequenceEnumerator(Iterator sequenceIterator, int start)
		{
			this.sequenceIterator = sequenceIterator;
			this.index = start;
		}

		public boolean hasNext()
		{
			return sequenceIterator.hasNext();
		}

		public Vector next()
		{
			Vector retVal = new Vector(2);
			retVal.add(new Integer(index++));
			retVal.add(sequenceIterator.next());
			return retVal;
		}

		public void remove()
		{
			sequenceIterator.remove();
		}
	}
}
