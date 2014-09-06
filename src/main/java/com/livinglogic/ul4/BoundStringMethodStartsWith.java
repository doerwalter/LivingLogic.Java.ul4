/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public class BoundStringMethodStartsWith extends BoundMethod<String>
{
	public BoundStringMethodStartsWith(String object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "str.startswith";
	}

	private static final Signature signature = new Signature("prefix", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public static boolean call(String object, String suffix)
	{
		return object.startsWith(suffix);
	}

	public Object evaluate(Object[] args)
	{
		if (args[0] instanceof String)
			return call(object, (String)args[0]);
		throw new ArgumentTypeMismatchException("{}.startswith({})", object, args[0]);
	}
}
