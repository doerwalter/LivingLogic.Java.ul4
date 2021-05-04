/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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
	protected static class Type extends CallRenderAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "CallAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.call";
		}

		@Override
		public String getDoc()
		{
			return "AST node for calling an object (e.g. ``f(x, y)``).";
		}

		@Override
		public CallAST create(String id)
		{
			return new CallAST(null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof CallAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public CallAST(Template template, Slice pos, AST obj)
	{
		super(template, pos, obj);
	}

	public String getType()
	{
		return "call";
	}

	@Override
	public Object decoratedEvaluate(EvaluationContext context)
	{
		Object realObject = null;
		// Overwrite with a version that attaches a new stackframe when the called object is a template, because we want to see the call in the exception chain.
		try
		{
			context.tick();
			realObject = obj.decoratedEvaluate(context);

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
		catch (Exception ex)
		{
			decorateException(ex, realObject);
			throw ex;
		}
	}

	@Override
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
