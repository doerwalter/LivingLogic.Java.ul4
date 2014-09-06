/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public class FunctionIsDict extends Function
{
	public String nameUL4()
	{
		return "isdict";
	}

	private Signature signature = new Signature("isdict", "obj", Signature.required);

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
		return (null != obj) && (obj instanceof Map) && !FunctionIsTemplate.call(obj);
	}
}
