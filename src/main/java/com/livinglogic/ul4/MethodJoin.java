/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;

public class MethodJoin implements Method
{
	public String getName()
	{
		return "join";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 1:
				return call(obj, args[0]);
			default:
				throw new ArgumentCountMismatchException("method", "join", args.length, 1);
		}
	}

	public static String call(String obj, Collection iterable)
	{
		return StringUtils.join(iterable, obj);
	}

	public static String call(String obj, Object iterable)
	{
		return StringUtils.join(Utils.iterator(iterable), obj);
	}

	public static String call(Object obj, Object iterable)
	{
		if (obj instanceof String)
		{
			if (iterable instanceof Collection)
				return call((String)obj, (Collection)iterable);
			return call((String)obj, iterable);
		}
		else
			throw new ArgumentTypeMismatchException("{}.join({})", obj, iterable);
	}
}
