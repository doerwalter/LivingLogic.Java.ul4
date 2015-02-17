/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
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

	public Object evaluate(BoundArguments args)
	{
		Object arg = args.get(0);

		if (arg instanceof String)
			return call(object, (String)arg);
		throw new ArgumentTypeMismatchException("{}.startswith({})", object, arg);
	}
}
