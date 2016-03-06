/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;

public class BoundSetMethodAdd extends BoundMethod<Set>
{
	public BoundSetMethodAdd(Set object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "set.add";
	}

	private static final Signature signature = new Signature("object", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public static void call(Set set, Object object)
	{
		set.add(object);
	}

	public Object evaluate(BoundArguments args)
	{
		call(object, args.get(0));
		return null;
	}
}
