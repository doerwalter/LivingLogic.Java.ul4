/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public abstract class ArgumentASTBase extends CodeAST
{
	public ArgumentASTBase(Tag tag, int start, int end)
	{
		super(tag, start, end);
	}

	public Object evaluate(EvaluationContext context)
	{
		// this will never be called
		return null;
	}

	public void decoratedEvaluateCall(EvaluationContext context, List<Object> arguments, Map<String, Object> keywordArguments)
	{
		try
		{
			context.tick();
			evaluateCall(context, arguments, keywordArguments);
		}
		catch (BreakException|ContinueException|ReturnException|SourceException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new SourceException(ex, context.getTemplate(), this);
		}
	}

	public abstract void addToCall(CallRenderAST call);

	public abstract void evaluateCall(EvaluationContext context, List<Object> arguments, Map<String, Object> keywordArguments);
}
