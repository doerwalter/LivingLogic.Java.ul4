/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class BitAndVarAST extends ChangeVarAST
{
	protected static class Type extends ChangeVarAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "BitAndVarAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.bitandvar";
		}

		@Override
		public String getDoc()
		{
			return "AST node for an augmented assignment expression that assigns the result\nof a binary bitwise \"and\" expression to its left operand.\n(e.g. ``x &= y``).";
		}

		@Override
		public BitAndVarAST create(String id)
		{
			return new BitAndVarAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof BitAndVarAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public BitAndVarAST(Template template, int posStart, int posStop, LValue lvalue, AST value)
	{
		super(template, posStart, posStop, lvalue, value);
	}

	public String getType()
	{
		return "bitandvar";
	}

	@Override
	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateBitAnd(context, value.decoratedEvaluate(context));
		return null;
	}
}
