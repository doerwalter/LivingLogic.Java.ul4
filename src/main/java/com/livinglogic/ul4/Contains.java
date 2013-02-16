/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class Contains extends Binary
{
	public Contains(Location location, int start, int end, AST obj1, AST obj2)
	{
		super(location, start, end, obj1, obj2);
	}

	public String getType()
	{
		return "contains";
	}

	public static AST make(Location location, int start, int end, AST obj1, AST obj2)
	{
		if (obj1 instanceof Const && obj2 instanceof Const)
		{
			Object result = call(((Const)obj1).value, ((Const)obj2).value);
			if (!(result instanceof Undefined))
				return new Const(location, start, end, result);
		}
		return new Contains(location, start, end, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static boolean call(String obj, String container)
	{
		return container.indexOf(obj) >= 0;
	}

	public static boolean call(Object obj, Collection container)
	{
		return container.contains(obj);
	}

	public static boolean call(Object obj, Map container)
	{
		return container.containsKey(obj);
	}

	public static boolean call(Object obj, Object container)
	{
		if (container instanceof String)
		{
			if (obj instanceof String)
				return call((String)obj, (String)container);
		}
		else if (container instanceof Collection)
			return call(obj, (Collection)container);
		else if (container instanceof Map)
			return call(obj, (Map)container);
		throw new ArgumentTypeMismatchException("{} in {}", obj, container);
	}
}
