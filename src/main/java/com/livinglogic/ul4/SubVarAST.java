/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class SubVarAST extends ChangeVarAST
{
	protected static class Type extends ChangeVarAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "SubVarAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.subvar";
		}

		@Override
		public String getDoc()
		{
			return "AST node for an augmented assignment expression that subtracts a value from\na variable/attribute/item. (e.g. `x -= y`).";
		}

		@Override
		public SubVarAST create(String id)
		{
			return new SubVarAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof SubVarAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public SubVarAST(Template template, int posStart, int posStop, LValue lvalue, AST value)
	{
		super(template, posStart, posStop, lvalue, value);
	}

	public String getType()
	{
		return "subvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateSub(context, value.decoratedEvaluate(context));
		return null;
	}
}
