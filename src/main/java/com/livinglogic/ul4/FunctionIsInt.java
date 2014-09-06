/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigInteger;

public class FunctionIsInt extends Function
{
	public String nameUL4()
	{
		return "isint";
	}

	private Signature signature = new Signature("isint", "obj", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0]);
	}

	public static boolean call(Object obj)
	{
		return (null != obj) && (obj instanceof BigInteger || obj instanceof Byte || obj instanceof Integer || obj instanceof Long || obj instanceof Short);
	}
}
