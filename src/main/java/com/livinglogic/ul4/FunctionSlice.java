/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;
import java.util.Map;

public class FunctionSlice implements UL4Call
{
	public String getName()
	{
		return "slice";
	}

	public Object callUL4(List<Object> args, Map<String, Object> kwargs)
	{
		if (kwargs.size() != 0)
			throw new KeywordArgumentsNotSupportedException(this.getName());
		switch (args.size())
		{
			case 2:
				return call(args.get(0), args.get(1));
			case 3:
				return call(args.get(0), args.get(1), args.get(2));
			case 4:
				return call(args.get(0), args.get(1), args.get(2), args.get(3));
			default:
				throw new ArgumentCountMismatchException("function", "slice", args.size(), 2, 4);
		}
	}

	private static int _start(Object obj)
	{
		int start = 0;
		if (obj != null)
		{
			start = Utils.toInt(obj);
			if (start < 0)
				throw new IllegalArgumentException("Start argument must >= 0!");
		}
		return start;
	}

	private static int _stop(Object obj)
	{
		int stop = -1;
		if (obj != null)
		{
			stop = Utils.toInt(obj);
			if (stop < 0)
				throw new IllegalArgumentException("Stop argument must >= 0!");
		}
		return stop;
	}

	private static int _step(Object obj)
	{
		int step = 1;
		if (obj != null)
		{
			step = Utils.toInt(obj);
			if (step <= 0)
				throw new IllegalArgumentException("Step argument must > 0!");
		}
		return step;
	}

	public static Object call(Object obj1, Object obj2)
	{
		return new Slice(obj1, 0, _start(obj2), 1);
	}

	public static Object call(Object obj1, Object obj2, Object obj3)
	{
		return new Slice(obj1, _start(obj2), _stop(obj3), 1);
	}

	public static Object call(Object obj1, Object obj2, Object obj3, Object obj4)
	{
		return new Slice(obj1, _start(obj2), _stop(obj3), _step(obj4));
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
