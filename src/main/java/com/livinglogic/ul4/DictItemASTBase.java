/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Map;

public abstract class DictItemASTBase extends CodeAST
{
	public DictItemASTBase(Tag tag, int start, int end)
	{
		super(tag, start, end);
	}

	public Object evaluate(EvaluationContext context)
	{
		// this will never be called
		return null;
	}

	public void decoratedEvaluateDict(EvaluationContext context, Map result)
	{
		try
		{
			context.tick();
			evaluateDict(context, result);
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

	public abstract void evaluateDict(EvaluationContext context, Map result);
}
