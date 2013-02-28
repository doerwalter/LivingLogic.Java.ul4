/*
** Copyright 2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class FunctionPrint extends FunctionWithContext
{
	public String nameUL4()
	{
		return "print";
	}

	protected void makeSignature(Signature signature)
	{
		signature.setRemainingArguments("values");
	}

	public Object evaluate(EvaluationContext context, Object[] args)
	{
		return call(context, (Object[])args[0]);
	}

	public static Object call(EvaluationContext context, Object[] values)
	{
		try
		{
			for (int i = 0; i < values.length; ++i)
			{
				if (i != 0)
					context.write(" ");
				context.write(FunctionStr.call(values[i]));
			}
			return null;
		}
		catch (IOException exc)
		{
			throw new RuntimeException(exc);
		}
	}
}
