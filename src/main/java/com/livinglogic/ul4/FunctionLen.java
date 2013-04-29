/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Collection;

public class FunctionLen extends Function
{
	public String nameUL4()
	{
		return "len";
	}

	protected Signature makeSignature()
	{
		return new Signature(
			nameUL4(),
			"sequence", Signature.required
		);
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0]);
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

	public static int call(Map obj)
	{
		return obj.size();
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		else if (obj instanceof UL4Len)
			return call((Map)obj);
		else if (obj instanceof Collection)
			return call((Collection)obj);
		else if (obj instanceof Map)
			return call((Map)obj);
		throw new ArgumentTypeMismatchException("len({})", obj);
	}
}
