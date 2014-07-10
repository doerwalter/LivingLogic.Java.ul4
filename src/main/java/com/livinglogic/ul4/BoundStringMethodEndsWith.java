/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public class BoundStringMethodEndsWith extends BoundMethod<String>
{
	private static final Signature signature = new Signature("endswith", "suffix", Signature.required);

	public BoundStringMethodEndsWith(String object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static boolean call(String object, String suffix)
	{
		return object.endsWith(suffix);
	}

	public Object callUL4(Object[] args)
	{
		if (args[0] instanceof String)
			return call(object, (String)args[0]);
		throw new ArgumentTypeMismatchException("{}.endswith({})", object, args[0]);
	}
}
