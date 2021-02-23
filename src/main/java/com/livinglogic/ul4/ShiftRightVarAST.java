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
			return "An augmented right shift assignment (i.e. `x >>= y`).";
		}

		@Override
		public ShiftRightVarAST create(String id)
		{
			return new ShiftRightVarAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ShiftRightVarAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ShiftRightVarAST(Template template, Slice pos, LValue lvalue, AST value)
	{
		super(template, pos, lvalue, value);
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
