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

	private static final Signature signature = new Signature().addPositionalOnly("obj");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
	}

	public static String call(Object obj)
	{
		return com.livinglogic.ul4on.Utils.dumps(obj);
	}

	public static final Function function = new FunctionAsUL4ON();
}
