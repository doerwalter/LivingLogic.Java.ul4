/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Map;


/**
Abstract base class for AST nodes that represent items in a dictionary (key-value pairs, or and unpacking expression (`**expr`)).
**/
public abstract class DictItemASTBase extends CodeAST
{
	public DictItemASTBase(Template template, int posStart, int posStop)
	{
		super(template, posStart, posStop);
	}

	@Override
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
		catch (Exception ex)
		{
			decorateException(ex);
			throw ex;
		}
	}

	public abstract void evaluateDict(EvaluationContext context, Map result);
}
