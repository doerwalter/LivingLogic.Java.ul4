/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;

public class FunctionAny extends Function
{
	@Override
	public String getNameUL4()
	{
		return "any";
	}

	private static final Signature signature = new Signature().addPositionalOnly("iterable");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
	}

	public static boolean call(String obj)
	{
		for (int i = 0; i < obj.length(); ++i)
		{
			if (obj.charAt(i) != '\0')
				return true;
		}
		return false;
	}

	public static boolean call(List obj)
	{
		for (int i = 0; i < obj.size(); ++i)
		{
			if (FunctionBool.call(obj.get(i)))
				return true;
		}
		return false;
	}

	public static boolean call(Collection obj)
	{
		return call(obj.iterator());
	}

	public static boolean call(Iterator obj)
	{
		while (obj.hasNext())
		{
			if (FunctionBool.call(obj.next()))
				return true;
		}
		return false;
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
		throw new ArgumentTypeMismatchException("any({!t}) not supported", obj);
	}

	public static final Function function = new FunctionAny();
}
