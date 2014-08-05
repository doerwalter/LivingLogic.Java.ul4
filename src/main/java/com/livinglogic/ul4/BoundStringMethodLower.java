/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public class BoundStringMethodLower extends BoundMethod<String>
{
	private static final Signature signature = new Signature("lower");

	public BoundStringMethodLower(String object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static String call(String object)
	{
		return object.toLowerCase();
	}

	public Object callUL4(Object[] args)
	{
		return call(object);
	}
}
