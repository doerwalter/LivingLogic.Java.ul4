/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Map;

public abstract class DictItemASTBase extends CodeAST
{
	public DictItemASTBase(InterpretedTemplate template, Slice pos)
	{
		super(template, pos);
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
		catch (BreakException|ContinueException|ReturnException|LocationException ex)
		{
			throw ex;
		}
		catch (RuntimeException ex)
		{
			decorateException(ex);
			throw ex;
		}
		catch (Exception ex)
		{
			RuntimeException newex = new RuntimeException(ex);
			decorateException(newex);
			throw newex;
		}
	}

	public abstract void evaluateDict(EvaluationContext context, Map result);
}
