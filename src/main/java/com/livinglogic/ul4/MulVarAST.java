/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class MulVarAST extends ChangeVarAST
{
	protected static class Type extends ChangeVarAST.Type
	{
		@Override
		public String getModuleName()
		{
			return "ul4";
		}

		@Override
		public String getNameUL4()
		{
			return "MulVarAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.mulvar";
		}

		@Override
		public String getDoc()
		{
			return "An augmented multiplication assignment (i.e. `x *= y`).";
		}

		@Override
		public MulVarAST create(String id)
		{
			return new MulVarAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof MulVarAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public MulVarAST(Template template, Slice pos, LValue lvalue, AST value)
	{
		super(template, pos, lvalue, value);
	}

	public String getType()
	{
		return "mulvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateMul(context, value.decoratedEvaluate(context));
		return null;
	}
}
