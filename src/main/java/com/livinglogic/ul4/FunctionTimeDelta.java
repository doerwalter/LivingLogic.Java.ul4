/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionTimeDelta extends Function
{
	public String nameUL4()
	{
		return "timedelta";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("days", 0);
		signature.add("seconds", 0);
		signature.add("microseconds", 0);
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0], args[1], args[2]);
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
