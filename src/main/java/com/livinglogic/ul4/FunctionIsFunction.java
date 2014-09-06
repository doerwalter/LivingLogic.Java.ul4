/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


public class FunctionIsFunction extends Function
{
	public String nameUL4()
	{
		return "isfunction";
	}

	private Signature signature = new Signature("isfunction", "obj", Signature.required);

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
		return (null != obj) && ((obj instanceof UL4Call) || (obj instanceof UL4CallWithContext));
	}
}
