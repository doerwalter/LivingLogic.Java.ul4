/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public class BoundStringMethodCapitalize extends BoundMethod<String>
{
	private static final Signature signature = new Signature("capitalize");

	public BoundStringMethodCapitalize(String object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static String call(String object)
	{
		return String.valueOf(Character.toTitleCase(object.charAt(0))) + object.substring(1).toLowerCase();
	}

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);
		return call(object);
	}
}
