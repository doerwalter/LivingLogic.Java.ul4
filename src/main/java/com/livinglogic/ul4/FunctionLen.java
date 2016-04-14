/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Collection;
import java.util.Map;

public class FunctionLen extends Function
{
	public String nameUL4()
	{
		return "len";
	}

	private static final Signature signature = new Signature("sequence", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
	}

	public static int call(String obj)
	{
		return obj.length();
	}

	public static int call(UL4Len obj)
	{
		return obj.lenUL4();
	}

	public static int call(Collection obj)
	{
		return obj.size();
	}

	public static int call(Object[] obj)
	{
		return ((Object[])obj).length;
	}

	public static int call(Map obj)
	{
		return obj.size();
	}

	public static int call(UL4Attributes obj)
	{
		return obj.getAttributeNamesUL4().size();
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		else if (obj instanceof UL4Len)
			return call((Map)obj);
		else if (obj instanceof Collection)
			return call((Collection)obj);
		else if (obj instanceof Object[])
			return call((Object[])obj);
		else if (obj instanceof UL4Attributes)
			return call((UL4Attributes)obj);
		else if (obj instanceof Map)
			return call((Map)obj);
		throw new ArgumentTypeMismatchException("len({!t}) not supported", obj);
	}
}
