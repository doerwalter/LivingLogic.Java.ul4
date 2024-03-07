/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;
import java.util.Map;

public class FunctionSlice extends Function
{
	@Override
	public String getNameUL4()
	{
		return "slice";
	}

	private static final Signature signature = new Signature().addPositionalOnly("iterable").addPositionalOnly("start").addPositionalOnly("stop", Signature.noValue).addPositionalOnly("step", Signature.noValue);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		Object iterable = args.get(0);
		Object start = args.get(1);
		Object stop = args.get(2);
		Object step = args.get(3);
		if (step == Signature.noValue)
		{
			if (stop == Signature.noValue)
				return call(iterable, start);
			else
				return call(iterable, start, stop);
		}
		else
			return call(iterable, start, stop, step);
	}

	private static int convert(Object obj, int defaultValue)
	{
		return (obj != null) ? Utils.toInt(obj) : defaultValue;
	}

	public static Object call(Object iterable, int stop)
	{
		return new Slice(iterable, 0, stop, 1);
	}

	public static Object call(Object iterable, int start, int stop)
	{
		return new Slice(iterable, start, stop, 1);
	}

	public static Object call(Object iterable, int start, int stop, int step)
	{
		return new Slice(iterable, start, stop, step);
	}

	public static Object call(Object iterable, Object stop)
	{
		return call(iterable, convert(stop, -1));
	}

	public static Object call(Object iterable, Object start, Object stop)
	{
		return call(iterable, convert(start, 0), convert(stop, -1));
	}

	public static Object call(Object iterable, Object start, Object stop, Object step)
	{
		return call(iterable, convert(start, 0), convert(stop, -1), convert(step, 1));
	}

	private static class Slice extends FilteredIterator
	{
		Iterator iterator;
		int next;
		int start;
		int stop;
		int step;
		int count;

		public Slice(Object iterable, int start, int stop, int step)
		{
			if (start < 0)
				throw new IllegalArgumentException("Start argument must >= 0!");
			if (stop < -1)
				throw new IllegalArgumentException("Stop argument must >= 0!");
			if (step <= 0)
				throw new IllegalArgumentException("Step argument must > 0!");

			iterator = Utils.iterator(iterable);
			this.next = start;
			this.start = start;
			this.stop = stop;
			this.step = step;
			this.count = 0;

			fetchNext();
		}

		public void fetchNext()
		{
			Object nextItem;
			while (count < next)
			{
				if (iterator.hasNext())
				{
					iterator.next();
					++count;
				}
				else
				{
					noNextItem();
					return;
				}
			}
			if (stop > 0 && count >= stop)
			{
				noNextItem();
				return;
			}
			if (iterator.hasNext())
			{
				haveNextItem(iterator.next());
				++count;
			}
			else
			{
				noNextItem();
				return;
			}

			next += step;
			if (stop > 0 && next > stop)
				next = stop;
		}
	}

	public static UL4Call function = new FunctionSlice();
}
