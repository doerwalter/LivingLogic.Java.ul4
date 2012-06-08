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
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
		{
			if (!(args[0] instanceof String))
				throw new UnsupportedOperationException("urlunquote(" + Utils.objectType(args[0]) + ") not supported!");
			try
			{
				return URLDecoder.decode((String)args[0], "utf-8");
			}
			catch (java.io.UnsupportedEncodingException ex)
			{
				// Can't happen
				throw new RuntimeException(ex);
			}
		}
		throw new ArgumentCountMismatchException("function", "urlunquote", args.length, 1);
	}

	public String getName()
	{
		return "urlunquote";
	}
}
