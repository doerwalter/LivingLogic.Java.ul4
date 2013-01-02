/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.AbstractList;

public class FunctionRange implements Function
{
	public String getName()
	{
		return "range";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		switch (args.length)
		{
			case 1:
				return call(args[0]);
			case 2:
				return call(args[0], args[1]);
			case 3:
				return call(args[0], args[1], args[2]);
			default:
				throw new ArgumentCountMismatchException("function", "range", args.length, 1, 3);
		}
	}

	public static Object call(Object obj)
	{
		return new Range(0, Utils.toInt(obj), 1);
	}

	public static Object call(Object obj1, Object obj2)
	{
		return new Range(Utils.toInt(obj1), Utils.toInt(obj2), 1);
	}

	public static Object call(Object obj1, Object obj2, Object obj3)
	{
		return new Range(Utils.toInt(obj1), Utils.toInt(obj2), Utils.toInt(obj3));
	}

	private static class Range extends AbstractList
	{
		int start;

		int stop;

		int step;

		int length;

		public Range(int start, int stop, int step)
		{
			if (0 == step)
			{
				throw new IllegalArgumentException("Step argument must be non-zero!");
			}
			else if (0 < step)
			{
				this.length = rangeLength(start, stop, step);
			}
			else
			{
				this.length = rangeLength(stop, start, -step);
			}
			this.start = start;
			this.stop = stop;
			this.step = step;
		}

		public Object get(int index)
		{
			if ((index < 0) || (index >= length))
			{
				throw new IndexOutOfBoundsException("Invalid index: " + index);
			}
			return start + index * step;
		}

		protected int rangeLength(int lowerEnd, int higherEnd, int positiveStep)
		{
			int retVal = 0;
			if (lowerEnd < higherEnd)
			{
				int diff = higherEnd - lowerEnd - 1;
				retVal = diff/positiveStep + 1;
			}
			return retVal;
		}

		public int size()
		{
			return length;
		}
	}
}
