/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public abstract class ArgumentASTBase extends CodeAST
{
	public ArgumentASTBase(Template template, Slice pos)
	{
		super(template, pos);
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

	/**
	 * Used by the parser to attach this node to its parent.
	 */
	public abstract void addToCall(CallRenderAST call);

	/**
	 * Used during AST evaluation.
	 */
	public abstract void evaluateCall(EvaluationContext context, List<Object> arguments, Map<String, Object> keywordArguments);
}
