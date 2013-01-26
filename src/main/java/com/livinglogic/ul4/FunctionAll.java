/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Collection;

public class FunctionAll extends Function
{
	public String nameUL4()
	{
		return "all";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("iterable");
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0]);
	}

	public static boolean call(String obj)
	{
		for (int i = 0; i < obj.length(); ++i)
		{
			if (obj.charAt(i) == '\0')
				return false;
		}
		return true;
	}

	public static boolean call(List obj)
	{
		for (int i = 0; i < obj.size(); ++i)
		{
			if (!FunctionBool.call(obj.get(i)))
				return false;
		}
		return true;
	}

	public static boolean call(Collection obj)
	{
		return call(obj.iterator());
	}

	public static boolean call(Iterator obj)
	{
		while (obj.hasNext())
		{
			if (!FunctionBool.call(obj.next()))
				return false;
		}
		return true;
	}

	public static boolean call(Map obj)
	{

		return call(obj.keySet().iterator());
	}

	public static boolean call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		else if (obj instanceof List)
			return call((List)obj);
		else if (obj instanceof Collection)
			return call((Collection)obj);
		else if (obj instanceof Iterator)
			return call((Iterator)obj);
		else if (obj instanceof Map)
			return call((Map)obj);
		throw new ArgumentTypeMismatchException("all({})", obj);
	}
}
