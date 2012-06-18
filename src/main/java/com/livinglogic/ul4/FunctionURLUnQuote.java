/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

public class FunctionURLUnQuote implements Function
{
	public static Object call(String obj)
	{
		try
		{
			return URLDecoder.decode(obj, "utf-8");
		}
		catch (UnsupportedEncodingException ex)
		{
			// Can't happen
			throw new RuntimeException(ex);
		}
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		throw new UnsupportedOperationException("urlunquote(" + Utils.objectType(obj) + ") not supported!");
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "urlunquote", args.length, 1);
	}

	public String getName()
	{
		return "urlunquote";
	}
}
