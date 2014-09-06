/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

public class FunctionFromJSON extends Function
{
	public String nameUL4()
	{
		return "fromjson";
	}

	private static final Signature signature = new Signature("string", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0]);
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
