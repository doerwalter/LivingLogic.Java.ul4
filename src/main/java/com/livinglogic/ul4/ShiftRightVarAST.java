/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ShiftRightVarAST extends ChangeVarAST
{
	protected static class Type extends ChangeVarAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ShiftRightVarAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.shiftrightvar";
		}

		@Override
		public String getDoc()
		{
			return "AST node for an augmented assignment expression that assigns the result\nof a \"shift right\" expression to its left operand. (e.g. ``x >>= y``).";
		}

		@Override
		public ShiftRightVarAST create(String id)
		{
			return new ShiftRightVarAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ShiftRightVarAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ShiftRightVarAST(Template template, int posStart, int posStop, LValue lvalue, AST value)
	{
		super(template, posStart, posStop, lvalue, value);
	}

	public String getType()
	{
		return "shiftrightvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateShiftRight(context, value.decoratedEvaluate(context));
		return null;
	}
}
