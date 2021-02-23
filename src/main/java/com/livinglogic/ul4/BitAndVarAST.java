/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class BitAndVarAST extends ChangeVarAST
{
	protected static class Type extends AbstractInstanceType
	{
		public Type()
		{
			super("ul4", "BitAndVarAST", "de.livinglogic.ul4.bitandvar", "An augmented \"binary and\" assignment (x &= y).");
		}

		@Override
		public BitAndVarAST create(String id)
		{
			return new BitAndVarAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof BitAndVarAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public BitAndVarAST(Template template, Slice pos, LValue lvalue, AST value)
	{
		super(template, pos, lvalue, value);
	}

	public String getType()
	{
		return "bitandvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateBitAnd(context, value.decoratedEvaluate(context));
		return null;
	}
}
