/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
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

import com.livinglogic.ul4.PositionalArgumentAST;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

import com.livinglogic.vsql.VSQLAST;
import com.livinglogic.vsql.VSQLField;
import com.livinglogic.vsql.VSQLFieldRefAST;
import com.livinglogic.vsql.VSQLMethAST;
import com.livinglogic.vsql.VSQLFuncAST;
import com.livinglogic.vsql.VSQLAttrAST;
import com.livinglogic.vsql.UnsupportedUL4ASTException;
import com.livinglogic.utils.VSQLUtils;

import static com.livinglogic.utils.StringUtils.formatMessage;

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
			return new CallAST(null, -1, -1, null);
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

	public CallAST(Template template, int posStart, int posStop, AST obj)
	{
		super(template, posStart, posStop, obj);
	}

	public String getType()
	{
		return "call";
	}

	private void addVSQLArguments(List<Object> content, Map<String, VSQLField> vars)
	{
		AST prev = obj;
		for (AST arg : arguments)
		{
			if (arg instanceof PositionalArgumentAST posArg)
			{
				content.add(VSQLUtils.getSourceInfix(prev, posArg));
				content.add(posArg.getValue().asVSQL(vars));
				prev = posArg;
			}
			else
			{
				throw new UnsupportedUL4ASTException(formatMessage("vSQL argument {!`} of type {!t} is not supported", arg, arg));
			}
		}
		content.add(VSQLUtils.getSourceSuffix(prev, this));
	}

	@Override
	public VSQLAST asVSQL(Map<String, VSQLField> vars)
	{
		VSQLAST objV = obj.asVSQL(vars);

		if (objV instanceof VSQLFieldRefAST fieldRefV)
		{
			List<Object> content = new ArrayList<>();

			VSQLFieldRefAST fieldRefParentV = fieldRefV.getParent();

			if (fieldRefParentV != null)
			{
				content.add(VSQLUtils.getSourcePrefix(this, obj));
				content.add(fieldRefParentV);
				content.add(".");
				content.add(fieldRefV.getIdentifier());
				addVSQLArguments(content, vars);
				return new VSQLMethAST(content);
			}
			else
			{
				content.add(VSQLUtils.getSourcePrefix(this, obj));
				content.add(fieldRefV.getIdentifier());
				addVSQLArguments(content, vars);
				return new VSQLFuncAST(content);
			}
		}
		else if (objV instanceof VSQLAttrAST attrV)
		{
			List<Object> content = new ArrayList<>();
			content.add(VSQLUtils.getSourcePrefix(this, obj));
			content.add(attrV.getObj());
			content.add(".");
			content.add(attrV.getName());
			addVSQLArguments(content, vars);
			return new VSQLMethAST(content);
		}
		else
		{
			throw new UnsupportedUL4ASTException(formatMessage("vSQL expression {!`} of type {} is not callable", obj, objV.getDataTypeString()));
		}
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
			// method must be called supports {@link UL4GetAttr} and overwrites
			// {@code callAttrUL4} we can basically skip generating
			// a bound method object.
			if (obj instanceof AttrAST)
			{
				AST attrObject = ((AttrAST)obj).getObj();
				String attrName = ((AttrAST)obj).getAttrName();
				realObject = attrObject.decoratedEvaluate(context);
				// Note that we can't move the {@code makeArguments} call out to a
				// common spot as this would change the order of the AST evaluation.
				makeArguments(context, realArguments, realKeywordArguments);
				if (realObject instanceof UL4GetAttr)
				{
					return ((UL4GetAttr)realObject).callAttrUL4(context, attrName, realArguments, realKeywordArguments);
				}
				else
				{
					// This is an attribute access, but the resulting object doesn't
					// implement {@link UL4GetAttr}, so we have to get the attribute
					// via {@link AttrAST}.
					UL4Type type = UL4Type.getType(realObject);
					return type.callAttr(context, realObject, attrName, realArguments, realKeywordArguments);
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
