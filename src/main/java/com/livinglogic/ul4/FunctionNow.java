/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;

public class FunctionNow extends Function
{
	public String nameUL4()
	{
		return "now";
	}

	private static Signature signature = new Signature("now");

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(Object[] args)
	{
		return call();
	}

	public static Date call()
	{
		return new Date();
	}
}
