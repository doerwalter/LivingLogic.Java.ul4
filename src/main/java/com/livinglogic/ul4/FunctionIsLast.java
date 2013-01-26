/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;
import java.util.Vector;

public class FunctionIsLast extends Function
{
	public String nameUL4()
	{
		return "islast";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("iterable");
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0]);
	}

	public static Object call(Object obj)
	{
		return new SequenceIsLast(Utils.iterator(obj));
	}

	private static class SequenceIsLast implements Iterator<Vector>
	{
		Iterator sequenceIterator;

		public SequenceIsLast(Iterator sequenceIterator)
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
			Vector retVal = new Vector(2);
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
