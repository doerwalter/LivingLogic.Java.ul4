/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

public class FunctionURLQuote implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
		{
			if (!(args[0] instanceof String))
				throw new UnsupportedOperationException("urlquote(" + Utils.objectType(args[0]) + ") not supported!");
			try
			{
				return URLEncoder.encode((String)args[0], "utf-8");
			}
			catch (java.io.UnsupportedEncodingException ex)
			{
				// Can't happen
				throw new RuntimeException(ex);
			}
		}
		throw new ArgumentCountMismatchException("function", "urlquote", args.length, 1);
	}

	public String getName()
	{
		return "urlquote";
	}
}
