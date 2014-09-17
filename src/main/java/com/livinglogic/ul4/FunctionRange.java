/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.AbstractList;
import java.util.Map;

public class FunctionRange implements UL4Call
{
	public String getName()
	{
		return "range";
	}

	public Object callUL4(List<Object> args, Map<String, Object> kwargs)
	{
		if (kwargs.size() != 0)
			throw new KeywordArgumentsNotSupportedException(this.getName());
		switch (args.size())
		{
			case 1:
				return call(args.get(0));
			case 2:
				return call(args.get(0), args.get(1));
			case 3:
				return call(args.get(0), args.get(1), args.get(2));
			default:
				throw new ArgumentCountMismatchException("function", "range", args.size(), 1, 3);
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
