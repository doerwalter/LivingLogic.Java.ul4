/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionTimeDelta implements Function
{
	public String getName()
	{
		return "timedelta";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		switch (args.length)
		{
			case 0:
				return call();
			case 1:
				return call(args[0]);
			case 2:
				return call(args[0], args[1]);
			case 3:
				return call(args[0], args[1], args[2]);
			default:
				throw new ArgumentCountMismatchException("function", "timedelta", args.length, 0, 3);
		}
	}

	public static TimeDelta call()
	{
		return new TimeDelta();
	}

	public static TimeDelta call(int days)
	{
		return new TimeDelta(days, 0, 0);
	}

	public static TimeDelta call(double days)
	{
		return new TimeDelta(days, 0.0, 0.0);
	}

	public static TimeDelta call(int days, long seconds)
	{
		return new TimeDelta(days, seconds, 0);
	}

	public static TimeDelta call(double days, double seconds)
	{
		return new TimeDelta(days, seconds, 0.0);
	}

	public static TimeDelta call(int days, long seconds, long microseconds)
	{
		return new TimeDelta(days, seconds, microseconds);
	}

	public static TimeDelta call(double days, double seconds, double microseconds)
	{
		return new TimeDelta(days, seconds, microseconds);
	}

	public static TimeDelta call(Object days)
	{
		if (days instanceof Float || days instanceof Double)
			return call(Utils.toDouble(days));
		else
			return call(Utils.toInt(days));
	}

	public static TimeDelta call(Object days, Object seconds)
	{
		if (days instanceof Float || days instanceof Double || seconds instanceof Float || seconds instanceof Double)
			return call(Utils.toDouble(days), Utils.toDouble(seconds));
		else
			return call(Utils.toInt(days), Utils.toLong(seconds));
	}

	public static TimeDelta call(Object days, Object seconds, Object microseconds)
	{
		if (days instanceof Float || days instanceof Double || seconds instanceof Float || seconds instanceof Double || microseconds instanceof Float || microseconds instanceof Double)
			return call(Utils.toDouble(days), Utils.toDouble(seconds), Utils.toDouble(microseconds));
		else
			return call(Utils.toInt(days), Utils.toLong(seconds), Utils.toLong(microseconds));
	}
}
