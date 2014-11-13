/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class FunctionBool extends Function
{
	public String nameUL4()
	{
		return "bool";
	}

	private static final Signature signature = new Signature("obj", false);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
	}

	public static boolean call()
	{
		return false;
	}

	public static boolean call(UL4Bool obj)
	{
		return obj.boolUL4();
	}

	public static boolean call(Boolean obj)
	{
		return obj.booleanValue();
	}

	public static boolean call(String obj)
	{
		return (obj.length() > 0);
	}

	public static boolean call(Integer obj)
	{
		return (obj.intValue() != 0);
	}

	public static boolean call(Long obj)
	{
		return (obj.longValue() != 0);
	}

	public static boolean call(BigInteger obj)
	{
		return (!obj.equals(BigInteger.ZERO));
	}

	public static boolean call(Double obj)
	{
		return (obj.doubleValue() != 0.);
	}

	public static boolean call(BigDecimal obj)
	{
		return (obj.signum() != 0);
	}

	public static boolean call(Date obj)
	{
		return true;
	}

	public static boolean call(Collection obj)
	{
		return !obj.isEmpty();
	}

	public static boolean call(Object[] obj)
	{
		return obj.length != 0;
	}

	public static boolean call(Map obj)
	{
		return !obj.isEmpty();
	}

	public static boolean call(Object obj)
	{
		if (null == obj)
			return false;
		else if (obj instanceof UL4Bool)
			return call((UL4Bool)obj);
		else if (obj instanceof Boolean)
			return call((Boolean)obj);
		else if (obj instanceof String)
			return call((String)obj);
		else if (obj instanceof Integer)
			return call((Integer)obj);
		else if (obj instanceof Long)
			return call((Long)obj);
		else if (obj instanceof BigInteger)
			return call((BigInteger)obj);
		else if (obj instanceof Double)
			return call((Double)obj);
		else if (obj instanceof BigDecimal)
			return call((BigDecimal)obj);
		else if (obj instanceof Date)
			return call((Date)obj);
		else if (obj instanceof Collection)
			return call((Collection)obj);
		else if (obj instanceof Object[])
			return call((Object[])obj);
		else if (obj instanceof Map)
			return call((Map)obj);
		return true;
	}
}
