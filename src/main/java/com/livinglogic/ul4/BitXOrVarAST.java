/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class BitXOrVarAST extends ChangeVarAST
{
	protected static class Type extends ChangeVarAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "BitXOrVarAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.bitxorvar";
		}

		@Override
		public String getDoc()
		{
			return "An augmented \"binary exclusive or\" assignment (x ^= y).";
		}

		@Override
		public BitXOrVarAST create(String id)
		{
			return new BitXOrVarAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof BitXOrVarAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public BitXOrVarAST(Template template, Slice pos, LValue lvalue, AST value)
	{
		super(template, pos, lvalue, value);
	}

	public String getType()
	{
		return "bitxorvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateBitXOr(context, value.decoratedEvaluate(context));
		return null;
	}
}
