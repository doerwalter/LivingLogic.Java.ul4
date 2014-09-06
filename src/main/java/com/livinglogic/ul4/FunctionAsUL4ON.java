/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


public class FunctionAsUL4ON extends Function
{
	public String nameUL4()
	{
		return "asul4on";
	}

	private static final Signature signature = new Signature("obj", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0]);
	}

	public static String call(Object obj)
	{
		return com.livinglogic.ul4on.Utils.dumps(obj);
	}
}
