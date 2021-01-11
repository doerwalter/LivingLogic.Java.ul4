/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundListMethodAppend extends BoundMethod<List>
{
	public BoundListMethodAppend(List object)
	{
		super(object);
	}

	@Override
	public String nameUL4()
	{
		return "append";
	}

	private static final Signature signature = new Signature("items", Signature.remainingParameters);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static void call(List object, List<Object> items)
	{
		object.addAll(items);
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		call(object, (List<Object>)args.get(0));
		return null;
	}
}
