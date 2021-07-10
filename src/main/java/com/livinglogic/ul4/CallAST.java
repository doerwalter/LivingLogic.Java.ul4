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

	public static final Type type = new Type();

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

	private void makeArguments(EvaluationContext context, List<Object> realArguments, Map<String, Object> realKeywordArguments)
	{
			for (ArgumentASTBase argument : arguments)
				argument.decoratedEvaluateCall(context, realArguments, realKeywordArguments);
	}

	@Override
	public Object decoratedEvaluate(EvaluationContext context)
	{
		Object realObject = null;
		// Overwrite with a version that attaches a new stackframe when the called object is a template, because we want to see the call in the exception chain.
		try
		{
			context.tick();

			List<Object> realArguments = new ArrayList<Object>();
			Map<String, Object> realKeywordArguments = new LinkedHashMap<String, Object>();

			// If {@code obj} is an attribute access, this means that this
			// looks like a method call, so if the resulting object for which the
			// method  must be called suuports {@link UL4GetAttr} and overwrites
			// {@code callAttrUL4} we can basically skip generating
			// a bound method object.
			if (obj instanceof AttrAST)
			{
				AST attrObject = ((AttrAST)obj).getObj();
				String attrName = ((AttrAST)obj).getAttrName();
				realObject = attrObject.decoratedEvaluate(context);
				if (realObject instanceof UL4GetAttr)
				{
					makeArguments(context, realArguments, realKeywordArguments);
					return ((UL4GetAttr)realObject).callAttrUL4(context, attrName, realArguments, realKeywordArguments);
				}
				else
				{
					// This is an attribute access, but the resulting object doesn't
					// implement {@link UL4GetAttr}, so we have to get the attribute
					// via {@link AttrAST}.
					realObject = AttrAST.call(context, realObject, attrName);
				}
			}
			else
				realObject = obj.decoratedEvaluate(context);

			makeArguments(context, realArguments, realKeywordArguments);

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

	public static Object call(EvaluationContext context, UL4Call obj, List<Object> args, Map<String, Object> kwargs)
	{
		if (obj == null)
			throw new NotCallableException(obj);
		return obj.callUL4(context, args, kwargs);
	}

	public static Object call(EvaluationContext context, Object obj, List<Object> args, Map<String, Object> kwargs)
	{
		if (obj instanceof UL4Call)
			return call(context, (UL4Call)obj, args, kwargs);
		throw new NotCallableException(obj);
	}
}
