/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;

public class FunctionIsFloat extends Function
{
	public String nameUL4()
	{
		return "isfloat";
	}

	private Signature signature = new Signature("isfloat", "obj", Signature.required);

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
		return (null != obj) && (obj instanceof BigDecimal || obj instanceof Float || obj instanceof Double);
	}
}
