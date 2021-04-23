/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionAsUL4ON extends Function
{
	public String getNameUL4()
	{
		return "asul4on";
	}

	private static final Signature signature = new Signature().addPositionalOnly("obj").addBoth("indent", null);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(BoundArguments arguments)
	{
		Object obj = arguments.get(0);
		Object indent = arguments.get(1);

		if (indent != null && !(indent instanceof String))
			throw new ArgumentTypeMismatchException("dumps({!t}, {!t}) not supported", obj, indent);

		return call(obj, (String)indent);
	}

	public static String call(Object obj)
	{
		return com.livinglogic.ul4on.Utils.dumps(obj);
	}

	public static String call(Object obj, String indent)
	{
		return com.livinglogic.ul4on.Utils.dumps(obj, indent);
	}

	public static final Function function = new FunctionAsUL4ON();
}
