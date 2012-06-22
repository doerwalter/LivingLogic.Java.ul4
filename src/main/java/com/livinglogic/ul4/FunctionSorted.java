/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Vector;
import java.util.Collection;
import java.util.Collections;

public class FunctionSorted implements Function
{
	public String getName()
	{
		return "sorted";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "sorted", args.length, 1);
	}

	public static Vector call(String obj)
	{
		Vector retVal;
		int length = obj.length();
		retVal = new Vector(obj.length());
		for (int i = 0; i < length; i++)
		{
			retVal.add(String.valueOf(obj.charAt(i)));
		}
		Collections.sort(retVal);
		return retVal;
	}

	public static Vector call(Collection obj)
	{
		Vector retVal = new Vector(obj);
		Collections.sort(retVal);
		return retVal;
	}

	public static Vector call(Map obj)
	{
		Vector retVal = new Vector(obj.keySet());
		Collections.sort(retVal);
		return retVal;
	}

	public static Vector call(Set obj)
	{
		Vector retVal = new Vector(obj);
		Collections.sort(retVal);
		return retVal;
	}

	public static Vector call(Iterator obj)
	{
		Vector retVal = new Vector();
		while (obj.hasNext())
			retVal.add(obj.next());
		Collections.sort(retVal);
		return retVal;
	}

	public static Vector call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		else if (obj instanceof Collection)
			return call((Collection)obj);
		else if (obj instanceof Map)
			return call((Map)obj);
		else if (obj instanceof Set)
			return call((Set)obj);
		else if (obj instanceof Iterator)
			return call((Iterator)obj);
		throw new ArgumentTypeMismatchException("sorted({})", obj);
	}
}
