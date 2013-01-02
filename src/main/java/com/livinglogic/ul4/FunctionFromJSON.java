/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

public class FunctionFromJSON implements Function
{
	public String getName()
	{
		return "fromjson";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "fromjson", args.length, 1);
	}

	public static Object call(String obj)
	{
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(obj, Object.class);
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		throw new ArgumentTypeMismatchException("fromjson({})", obj);
	}
}
