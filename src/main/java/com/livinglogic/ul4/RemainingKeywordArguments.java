/*
** Copyright 2009-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class RemainingKeywordArguments extends Argument
{
	public RemainingKeywordArguments(AST arg)
	{
		super(arg);
	}

	public String getName()
	{
		return "**";
	}

	public void addToCallArguments(EvaluationContext context, Object object, List<Object> arguments, Map<String, Object> keywordArguments)
	{
		Object argObject = arg.decoratedEvaluate(context);

		if (!(argObject instanceof Map))
			throw new RemainingKeywordArgumentsException(object);

		for (Map.Entry<Object, Object> entry : ((Map<Object, Object>)argObject).entrySet())
		{
			Object argumentName = entry.getKey();
			if (!(argumentName instanceof String))
				throw new RemainingKeywordArgumentsException(object);
			if (keywordArguments.containsKey(argumentName))
				throw new DuplicateArgumentException(object, (String)argumentName);
			keywordArguments.put((String)argumentName, entry.getValue());
		}
	}
}
