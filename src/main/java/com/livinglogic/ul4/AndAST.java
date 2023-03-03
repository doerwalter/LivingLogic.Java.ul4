/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class AndAST extends BinaryAST
{
	protected static class Type extends BinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "AndAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.and";
		}

		@Override
		public String getDoc()
		{
			return "AST node for the binary \"and\" expression (i.e. ``x and y``).";
		}

		@Override
		public AndAST create(String id)
		{
			return new AndAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof AndAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public AndAST(Template template, int posStart, int posStop, CodeAST obj1, CodeAST obj2)
	{
		super(template, posStart, posStop, obj1, obj2);
	}

	public String getType()
	{
		return "and";
	}

	@Override
	public Object evaluate(EvaluationContext context)
	{
		Object obj1ev = obj1.decoratedEvaluate(context);
		if (!Bool.call(context, obj1ev))
			return obj1ev;
		else
			return obj2.decoratedEvaluate(context);
	}

	// this static version is only used for constant folding, not in evaluate(), because that would require that we evaluate both sides
	public static Object call(EvaluationContext context, Object arg1, Object arg2)
	{
		return !Bool.call(context, arg1) ? arg1 : arg2;
	}
}
