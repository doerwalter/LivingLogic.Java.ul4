/*
** Copyright 2009-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class NotContainsAST extends BinaryAST
{
	protected static class Type extends BinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "NotContainsAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.notcontains";
		}

		@Override
		public String getDoc()
		{
			return "AST node for an inverted containment testing expression (e.g. ``x not in y``).";
		}

		@Override
		public NotContainsAST create(String id)
		{
			return new NotContainsAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof NotContainsAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public NotContainsAST(Template template, int posStart, int posStop, CodeAST obj1, CodeAST obj2)
	{
		super(template, posStart, posStop, obj1, obj2);
	}

	public String getType()
	{
		return "notcontains";
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static boolean call(EvaluationContext context, Object obj, Object container)
	{
		return !ContainsAST.call(context, obj, container);
	}
}
