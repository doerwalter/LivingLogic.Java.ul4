/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

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
			return "AST node for a unary \"not\" expression (e.g. ``not x``).";
		}

		@Override
		public NotAST create(String id)
		{
			return new NotAST(null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof NotAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public NotAST(Template template, Slice pos, CodeAST obj)
	{
		super(template, pos, obj);
	}

	public String getType()
	{
		return "not";
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
