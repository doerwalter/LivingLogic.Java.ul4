/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class FunctionURLUnquote extends Function
{
	public String nameUL4()
	{
		return "urlunquote";
	}

	protected Signature makeSignature()
	{
		return new Signature(
			nameUL4(),
			"string", Signature.required
		);
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0]);
	}

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
		throw new ArgumentTypeMismatchException("urlunquote({})", obj);
	}
}
