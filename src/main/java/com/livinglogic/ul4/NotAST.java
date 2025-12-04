/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

import com.livinglogic.vsql.VSQLNotAST;
import com.livinglogic.vsql.VSQLField;
import com.livinglogic.utils.VSQLUtils;


public class NotAST extends UnaryAST
{
	protected static class Type extends UnaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "NotAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.not";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a unary \"not\" expression (e.g. `not x`).";
		}

		@Override
		public NotAST create(String id)
		{
			return new NotAST(null, -1, -1, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof NotAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public NotAST(Template template, int posStart, int posStop, CodeAST obj)
	{
		super(template, posStart, posStop, obj);
	}

	public String getType()
	{
		return "not";
	}

	@Override
	public VSQLNotAST asVSQL(Map<String, VSQLField> vars)
	{
		return new VSQLNotAST(
			VSQLUtils.getSourcePrefix(this, obj),
			obj.asVSQL(vars),
			VSQLUtils.getSourceSuffix(obj, this)
		);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(context, obj.decoratedEvaluate(context));
	}

	public static boolean call(EvaluationContext context, Object obj)
	{
		return !Bool.call(context, obj);
	}
}
