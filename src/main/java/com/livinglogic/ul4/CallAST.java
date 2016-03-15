/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Set;
import static java.util.Arrays.asList;
import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class CallAST extends CallRenderAST
{
	public CallAST(Tag tag, int start, int end, AST obj)
	{
		super(tag, start, end, obj);
	}

	public String getType()
	{
		return "call";
	}

	@Override
	public Object decoratedEvaluate(EvaluationContext context)
	{
		// Overwrite with a version that rewraps SourceException too, because we want to see the call in the exception chain.
		try
		{
			context.tick();
			return evaluate(context);
		}
		catch (BreakException|ContinueException|ReturnException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new SourceException(ex, context.getTemplate(), this);
		}
	}

	public Object evaluate(EvaluationContext context)
	{
		Object realObject = obj.decoratedEvaluate(context);

		List<Object> realArguments = new ArrayList<Object>();
		Map<String, Object> realKeywordArguments = new HashMap<String, Object>();

		for (ArgumentASTBase argument : arguments)
			argument.decoratedEvaluateCall(context, realArguments, realKeywordArguments);

		return call(context, realObject, realArguments, realKeywordArguments);
	}

	public static Object call(UL4Call obj, List<Object> args, Map<String, Object> kwargs)
	{
		return obj.callUL4(args, kwargs);
	}

	public static Object call(EvaluationContext context, UL4CallWithContext obj, List<Object> args, Map<String, Object> kwargs)
	{
		return obj.callUL4(context, args, kwargs);
	}

	public static Object call(EvaluationContext context, Object obj, List<Object> args, Map<String, Object> kwargs)
	{
		if (obj instanceof UL4Call)
			return call((UL4Call)obj, args, kwargs);
		else if (obj instanceof UL4CallWithContext)
			return call(context, (UL4CallWithContext)obj, args, kwargs);
		throw new NotCallableException(obj);
	}
}
