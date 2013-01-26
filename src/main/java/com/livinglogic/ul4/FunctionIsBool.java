/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionIsBool extends Function
{
	public String nameUL4()
	{
		return "isbool";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("obj");
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0]);
	}

	public static boolean call(Object obj)
	{
		return (obj != null) && (obj instanceof Boolean);
	}
}
