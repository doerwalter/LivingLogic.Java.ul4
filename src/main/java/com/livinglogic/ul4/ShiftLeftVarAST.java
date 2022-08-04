/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ShiftLeftVarAST extends ChangeVarAST
{
	protected static class Type extends ChangeVarAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ShiftLeftVarAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.shiftleftvar";
		}

		@Override
		public String getDoc()
		{
			return "AST node for an augmented assignment expression that assigns the result\nof a \"shift left\" expression to its left operand. (e.g. ``x <<= y``).";
		}

		@Override
		public ShiftLeftVarAST create(String id)
		{
			return new ShiftLeftVarAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ShiftLeftVarAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ShiftLeftVarAST(Template template, int posStart, int posStop, LValue lvalue, AST value)
	{
		super(template, posStart, posStop, lvalue, value);
	}

	public String getType()
	{
		return "shiftleftvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateShiftLeft(context, value.decoratedEvaluate(context));
		return null;
	}
}
