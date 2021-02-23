/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FloorDivVarAST extends ChangeVarAST
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
			return "FloorDivVarAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.floordivvar";
		}

		@Override
		public String getDoc()
		{
			return "An augmented floor division assignment (x //= y).";
		}

		@Override
		public FloorDivVarAST create(String id)
		{
			return new FloorDivVarAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof FloorDivVarAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}
	public FloorDivVarAST(Template template, Slice pos, LValue lvalue, AST value)
	{
		super(template, pos, lvalue, value);
	}

	public String getType()
	{
		return "floordivvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateFloorDiv(context, value.decoratedEvaluate(context));
		return null;
	}
}
