/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ShiftRightVarAST extends ChangeVarAST
{
	protected static class Type extends AbstractInstanceType
	{
		public Type()
		{
			super("ul4", "ShiftRightVarAST", "de.livinglogic.ul4.shiftrightvar", "An augmented right shift assignment (x >>= y).");
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
