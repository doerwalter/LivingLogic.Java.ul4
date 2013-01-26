/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Random;
import java.util.List;
import java.util.Map;

public class FunctionRandRange implements UL4Callable
{
	public String getName()
	{
		return "randrange";
	}

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		if (kwargs.size() != 0)
			throw new KeywordArgumentsNotSupportedException(this.getName());
		switch (args.length)
		{
			case 1:
				return call(args[0]);
			case 2:
				return call(args[0], args[1]);
			case 3:
				return call(args[0], args[1], args[2]);
			default:
				throw new ArgumentCountMismatchException("function", "randrange", args.length, 1, 3);
		}
	}

	private static Random rng = new Random();

	public static long call(Object stopObj)
	{
		long stop = Utils.toLong(stopObj);
		double value = rng.nextDouble();
		return (long)(value*stop);
	}

	public static long call(Object startObj, Object stopObj)
	{
		long start = Utils.toLong(startObj);
		long stop = Utils.toLong(stopObj);
		long width = stop-start;
		double value = rng.nextDouble();
		return start + ((long)(value*width));
	}

	public static long call(Object startObj, Object stopObj, Object stepObj)
	{
		long start = Utils.toLong(startObj);
		long stop = Utils.toLong(stopObj);
		long step = Utils.toLong(stepObj);
		long width = stop-start;
		double value = rng.nextDouble();

		long n;
		if (step > 0)
			n = (width + step - 1) / step;
		else if (step < 0)
			n = (width + step + 1) / step;
		else
			throw new UnsupportedOperationException("step can't be 0 in randrange()");
		return start + step*((long)(value * n));
	}
}
