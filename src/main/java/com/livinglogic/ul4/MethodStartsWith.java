/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class MethodStartsWith extends NormalMethod
{
	public String nameUL4()
	{
		return "startswith";
	}

	protected Signature makeSignature()
	{
		return new Signature(
			nameUL4(),
			"prefix", Signature.required
		);
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args)
	{
		return call(obj, args[0]);
	}

	public static Object call(String obj, String prefix)
	{
		return obj.startsWith(prefix);
	}

	public static Object call(Object obj, Object prefix)
	{
		if (obj instanceof String && prefix instanceof String)
			return call((String)obj, (String)prefix);
		throw new ArgumentTypeMismatchException("{}.startswith({})", obj, prefix);
	}
}
