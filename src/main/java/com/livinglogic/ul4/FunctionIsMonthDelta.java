/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionIsMonthDelta extends NormalFunction
{
	public String getName()
	{
		return "ismonthdelta";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("obj");
	}

	public Object evaluate(EvaluationContext context, Object[] args)
	{
		return call(args[0]);
	}

	public static boolean call(Object obj)
	{
		return (null != obj) && (obj instanceof MonthDelta);
	}
}
