/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public class BoundStringMethodUpper extends BoundMethod<String>
{
	private static final Signature signature = new Signature("upper");

	public BoundStringMethodUpper(String object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static String call(String object)
	{
		return object.toUpperCase();
	}

	public Object callUL4(Object[] args)
	{
		return call(object);
	}
}
