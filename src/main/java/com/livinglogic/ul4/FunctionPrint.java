/*
** Copyright 2013-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionPrint extends FunctionWithContext
{
	public String nameUL4()
	{
		return "print";
	}

	private static final Signature signature = new Signature("values", Signature.remainingArguments);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(EvaluationContext context, Object[] args)
	{
		return call(context, (List<Object>)args[0]);
	}

	public static Object call(EvaluationContext context, List<Object> values)
	{
		for (int i = 0; i < values.size(); ++i)
		{
			if (i != 0)
				context.write(" ");
			context.write(FunctionStr.call(values.get(i)));
		}
		return null;
	}
}
