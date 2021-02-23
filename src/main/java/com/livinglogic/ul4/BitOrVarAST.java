/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class BitOrVarAST extends ChangeVarAST
{
	protected static class Type extends ChangeVarAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "BitOrVarAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.bitorvar";
		}

		@Override
		public String getDoc()
		{
			return "An augmented \"binary or\" assignment (x |= y).";
		}

		@Override
		public BitOrVarAST create(String id)
		{
			return new BitOrVarAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof BitOrVarAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public BitOrVarAST(Template template, Slice pos, LValue lvalue, AST value)
	{
		super(template, pos, lvalue, value);
	}

	public String getType()
	{
		return "bitorvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateBitOr(context, value.decoratedEvaluate(context));
		return null;
	}
}
