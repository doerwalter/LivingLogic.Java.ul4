/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class FunctionRandRange extends Function
{
	@Override
	public String getNameUL4()
	{
		return "randrange";
	}

	private static final Signature signature = new Signature().addPositionalOnly("start").addPositionalOnly("stop", Signature.noValue).addPositionalOnly("step", Signature.noValue);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
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

	private static Random rng = new Random();

	public static long call(long stop)
	{
		double value = rng.nextDouble();
		return (long)(value*stop);
	}

	public static long call(long start, long stop)
	{
		long width = stop-start;
		double value = rng.nextDouble();
		return start + ((long)(value*width));
	}

	public static long call(long start, long stop, long step)
	{
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

	public static long call(Object stop)
	{
		return call(Utils.toLong(stop));
	}

	public static long call(Object start, Object stop)
	{
		return call(Utils.toLong(start), Utils.toLong(stop));
	}

	public static long call(Object start, Object stop, Object step)
	{
		return call(Utils.toLong(start), Utils.toLong(stop), Utils.toLong(step));
	}

	public static FunctionRandRange function = new FunctionRandRange();
}
