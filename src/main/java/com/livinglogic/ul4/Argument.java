/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class Argument
{
	protected AST arg;

	public Argument(AST arg)
	{
		this.arg = arg;
	}

	public String getName()
	{
		return null;
	}

	public AST getArg()
	{
		return arg;
	}

	public void addToCallArguments(EvaluationContext context, Object object, List<Object> arguments, Map<String, Object> keywordArguments)
	{
		arguments.add(arg.decoratedEvaluate(context));
	}
}
