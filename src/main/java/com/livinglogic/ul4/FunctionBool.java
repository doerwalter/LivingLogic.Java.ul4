/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionBool extends Function
{
	@Override
	public String getNameUL4()
	{
		return "bool";
	}

	private static final Signature signature = new Signature("obj", false);

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

	public static boolean call()
	{
		return false;
	}

	public static boolean call(Object obj)
	{
		return UL4Type.getType(obj).boolInstance(obj);
	}
}
