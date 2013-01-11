/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionAsUL4ON extends NormalFunction
{
	public String getName()
	{
		return "asul4on";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("obj");
	}

	public Object evaluate(EvaluationContext context, Object[] args)
	{
		return call(args[0]);
	}

	public static String call(Object obj)
	{
		return com.livinglogic.ul4on.Utils.dumps(obj);
	}
}
