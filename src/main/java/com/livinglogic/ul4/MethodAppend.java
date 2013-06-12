/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class MethodAppend extends NormalMethod
{
	public String nameUL4()
	{
		return "append";
	}

	protected Signature makeSignature()
	{
		return new Signature(
			nameUL4(),
			"items", Signature.remainingArguments
		);
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args)
	{
		call(obj, (List<Object>)args[0]);
		return null;
	}

	public static void call(List obj, List<Object> items)
	{
		obj.addAll(items);
	}

	public static void call(Object obj, List<Object> items)
	{
		if (obj instanceof List)
			call((List)obj, items);
		else
			throw new ArgumentTypeMismatchException("{}.append(*{})", obj, items);
	}
}
