/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public abstract class SeqItemASTBase extends CodeAST
{
	public SeqItemASTBase(InterpretedTemplate template, Slice pos)
	{
		super(template, pos);
	}

	public Object evaluate(EvaluationContext context)
	{
		// this will never be called
		return null;
	}

	public void decoratedEvaluateList(EvaluationContext context, List result)
	{
		try
		{
			context.tick();
			evaluateList(context, result);
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

	public abstract void evaluateList(EvaluationContext context, List result);

	public void decoratedEvaluateSet(EvaluationContext context, Set result)
	{
		try
		{
			context.tick();
			evaluateSet(context, result);
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

	public abstract void evaluateSet(EvaluationContext context, Set result);
}
