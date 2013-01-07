/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class FunctionVars extends NormalFunction
{
	public String getName()
	{
		return "vars";
	}

	public Object evaluate(EvaluationContext context, Object[] args)
	{
		return call(context.getVariables());
	}

	public static Object call(Map<String, Object> variables)
	{
		return variables;
	}
}
