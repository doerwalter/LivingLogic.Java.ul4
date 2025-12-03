/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
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
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, args.get(0));
	}

	public static boolean call(EvaluationContext context, String obj)
	{
		for (int i = 0; i < obj.length(); ++i)
		{
			if (obj.charAt(i) != '\0')
				return true;
		}
		return false;
	}

	public static boolean call(EvaluationContext context, List obj)
	{
		for (int i = 0; i < obj.size(); ++i)
		{
			if (Bool.call(context, obj.get(i)))
				return true;
		}
		return false;
	}

	public static boolean call(EvaluationContext context, Collection obj)
	{
		return call(context, obj.iterator());
	}

	public static boolean call(EvaluationContext context, Iterator obj)
	{
		while (obj.hasNext())
		{
			if (Bool.call(context, obj.next()))
				return true;
		}
		return false;
	}

	public static boolean call(EvaluationContext context, Map obj)
	{

		return call(context, obj.keySet().iterator());
	}

	public static boolean call(EvaluationContext context, Object obj)
	{
		if (obj instanceof String)
			return call(context, (String)obj);
		else if (obj instanceof List)
			return call(context, (List)obj);
		else if (obj instanceof Collection)
			return call(context, (Collection)obj);
		else if (obj instanceof Iterator)
			return call(context, (Iterator)obj);
		else if (obj instanceof Map)
			return call(context, (Map)obj);
		throw new ArgumentTypeMismatchException("any({!t}) not supported", obj);
	}

	public static final Function function = new FunctionAny();
}
