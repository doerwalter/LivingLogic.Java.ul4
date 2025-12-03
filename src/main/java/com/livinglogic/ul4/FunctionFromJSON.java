/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import org.json.simple.JSONValue;

public class FunctionFromJSON extends Function
{
	@Override
	public String getNameUL4()
	{
		return "fromjson";
	}

	private static final Signature signature = new Signature().addPositionalOnly("string");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(args.getString(0));
	}

	public static Object call(String obj)
	{
		return JSONValue.parse(obj);
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		throw new ArgumentTypeMismatchException("fromjson({!t}) not supported", obj);
	}

	public static final Function function = new FunctionFromJSON();
}
