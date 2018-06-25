/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class RemainingArguments extends Argument
{
	public RemainingArguments(AST arg)
	{
		super(arg);
	}

	public String getName()
	{
		return "*";
	}

	public void addToCallArguments(EvaluationContext context, Object object, List<Object> arguments, Map<String, Object> keywordArguments)
	{
		Object argObject = arg.decoratedEvaluate(context);

		if (argObject instanceof Collection)
			arguments.addAll((Collection)argObject);
		else if (argObject instanceof Iterable)
		{
			for (Object item : (Iterable)argObject)
				arguments.add(item);
		}
		else if (argObject instanceof Iterator)
		{
			for (Iterator argIterator = (Iterator)argObject; argIterator.hasNext();)
				arguments.add(argIterator.next());
		}
		else
			throw new RemainingArgumentsException(object);
	}
}
