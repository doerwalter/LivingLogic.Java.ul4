/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
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
	public String nameUL4()
	{
		return "insert";
	}

	private static final Signature signature = new Signature("pos", Signature.required, "items", Signature.remainingParameters);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static void call(List object, int pos, List<Object> items)
	{
		if (pos < 0)
			pos += object.size();
		object.addAll(pos, items);
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		call(object, Utils.toInt(args.get(0)), (List<Object>)args.get(1));
		return null;
	}
}
