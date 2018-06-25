/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Set;
import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class CallAST extends CallRenderAST
{
	public CallAST(Tag tag, Slice pos, AST obj)
	{
		super(tag, pos, obj);
	}

	public String getType()
	{
		return "call";
	}

	@Override
	public Object decoratedEvaluate(EvaluationContext context)
	{
		boolean addFrame = false;
		// Overwrite with a version that rewraps LocationException too, because we want to see the call in the exception chain (but only if its a call to a template).
		try
		{
			context.tick();
			Object realObject = obj.decoratedEvaluate(context);

			if (FunctionIsTemplate.call(realObject))
				addFrame = true;

			List<Object> realArguments = new ArrayList<Object>();

			Map<String, Object> realKeywordArguments = new LinkedHashMap<String, Object>();

			for (ArgumentASTBase argument : arguments)
				argument.decoratedEvaluateCall(context, realArguments, realKeywordArguments);

			return call(context, realObject, realArguments, realKeywordArguments);
		}
		catch (BreakException|ContinueException|ReturnException ex)
		{
			throw ex;
		}
		catch (LocationException ex)
		{
			if (addFrame)
				throw new LocationException(ex, this);
			else
				throw ex;
		}
		catch (Exception ex)
		{
			throw new LocationException(ex, this);
		}
	}

	public Object evaluate(EvaluationContext context)
	{
		// Do nothing here as the implementation is in {@code decoratedEvaluate}
		return null;
	}

	public static Object call(UL4Call obj, List<Object> args, Map<String, Object> kwargs)
	{
		if (obj == null)
			throw new NotCallableException(obj);
		return obj.callUL4(args, kwargs);
	}

	public static Object call(EvaluationContext context, UL4CallWithContext obj, List<Object> args, Map<String, Object> kwargs)
	{
		if (obj == null)
			throw new NotCallableException(obj);
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
