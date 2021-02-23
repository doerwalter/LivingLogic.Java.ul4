/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class TrueDivVarAST extends ChangeVarAST
{
	protected static class Type extends AbstractInstanceType
	{
		public Type()
		{
			super("ul4", "TrueDivVarAST", "de.livinglogic.ul4.truedivvar", "An augmented true division assignment (x /= y).");
		}

		@Override
		public TrueDivVarAST create(String id)
		{
			return new TrueDivVarAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof TrueDivVarAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public TrueDivVarAST(Template template, Slice pos, LValue lvalue, AST value)
	{
		super(template, pos, lvalue, value);
	}

	public String getType()
	{
		return "truedivvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateTrueDiv(context, value.decoratedEvaluate(context));
		return null;
	}
}
