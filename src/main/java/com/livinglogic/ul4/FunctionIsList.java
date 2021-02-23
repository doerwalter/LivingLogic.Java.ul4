/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionIsList extends Function
{
	@Override
	public String getNameUL4()
	{
		return "islist";
	}

	private static final Signature signature = new Signature("obj", Signature.required);

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

	public static boolean call(Object obj)
	{
		return (null != obj) && (obj instanceof List || obj instanceof Object[]) && !(obj instanceof Color);
	}

	public static Function function = new FunctionIsList();
}
