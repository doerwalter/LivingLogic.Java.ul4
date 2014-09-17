/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionIsFunction extends Function
{
	public String nameUL4()
	{
		return "isfunction";
	}

	private static final Signature signature = new Signature("obj", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(List<Object> args)
	{
		return call(args.get(0));
	}

	public static boolean call(Object obj)
	{
		return (null != obj) && ((obj instanceof UL4Call) || (obj instanceof UL4CallWithContext));
	}
}
