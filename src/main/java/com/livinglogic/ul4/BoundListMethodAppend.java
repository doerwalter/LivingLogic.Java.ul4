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
	public String getNameUL4()
	{
		return "append";
	}

	private static final Signature signature = new Signature().addVarPositional("items");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static void call(EvaluationContext context, List object, List<Object> items)
	{
		object.addAll(items);
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		call(context, object, (List<Object>)args.get(0));
		return null;
	}
}
