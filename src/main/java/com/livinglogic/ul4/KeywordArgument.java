/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class KeywordArgument extends Argument
{
	protected String name;

	public KeywordArgument(String name, AST arg)
	{
		super(arg);
		this.name = name;
	}

	public String toString()
	{
		return name + "=" + arg.toString();
	}

	public String getName()
	{
		return name;
	}

	public void addToCallArguments(EvaluationContext context, Object object, List<Object> arguments, Map<String, Object> keywordArguments)
	{
		if (keywordArguments.containsKey(name))
			throw new DuplicateArgumentException(object, name);
		keywordArguments.put(name, arg.decoratedEvaluate(context));
	}
}
