/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundListMethodInsert extends BoundMethod<List>
{
	public BoundListMethodInsert(List object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "insert";
	}

	private static final Signature signature = new Signature().addPositionalOnly("pos").addVarPositional("items");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static void call(EvaluationContext context, List object, int pos, List<Object> items)
	{
		if (pos < 0)
			pos += object.size();
		object.addAll(pos, items);
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		call(context, object, Utils.toInt(args.get(0)), (List<Object>)args.get(1));
		return null;
	}
}
