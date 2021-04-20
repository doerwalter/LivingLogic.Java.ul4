/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.AbstractList;
import java.util.Map;

public class FunctionRange extends Function
{
	@Override
	public String getNameUL4()
	{
		return "range";
	}

	private static final Signature signature = new Signature().addPositionalOnly("start").addPositionalOnly("stop", Signature.noValue).addPositionalOnly("step", Signature.noValue);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		Object start = args.get(0);
		Object stop = args.get(1);
		Object step = args.get(2);
		if (step == Signature.noValue)
		{
			if (stop == Signature.noValue)
				return call(start);
			else
				return call(start, stop);
		}
		else
			return call(start, stop, step);
	}

	public static Object call(int stop)
	{
		return new Range(0, stop, 1);
	}

	public static Object call(int start, int stop)
	{
		return new Range(start, stop, 1);
	}

	public static Object call(int start, int stop, int step)
	{
		return new Range(start, stop, step);
	}

	public static Object call(Object stop)
	{
		return call(Utils.toInt(stop));
	}

	public static Object call(Object start, Object stop)
	{
		return call(Utils.toInt(start), Utils.toInt(stop));
	}

	public static Object call(Object start, Object stop, Object step)
	{
		return call(Utils.toInt(start), Utils.toInt(stop), Utils.toInt(step));
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

	public static UL4Call function = new FunctionRange();
}
