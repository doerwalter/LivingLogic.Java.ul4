/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.List;

public class MethodInsert extends NormalMethod
{
	public String nameUL4()
	{
		return "insert";
	}

	protected Signature makeSignature()
	{
		return new Signature(
			nameUL4(),
			"pos", Signature.required,
			"items", Signature.remainingArguments
		);
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args)
	{
		call(obj, args[0], (List<Object>)args[1]);
		return null;
	}

	public static void call(List obj, int pos, List<Object> items)
	{
		if (pos < 0)
			pos += obj.size();
		obj.addAll(pos, items);
	}

	public static void call(Object obj, Object pos, List<Object> items)
	{
		if (obj instanceof List)
			call((List)obj, Utils.toInt(pos), items);
		else
			throw new ArgumentTypeMismatchException("{}.insert({}, *{})", obj, pos, items);
	}
}
