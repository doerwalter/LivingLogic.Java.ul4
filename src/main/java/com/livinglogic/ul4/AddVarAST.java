/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class AddVarAST extends ChangeVarAST
{
	protected static class Type extends ChangeVarAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "AddVarAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.addvar";
		}

		@Override
		public String getDoc()
		{
			return "An augmented addition assignment (x += y).";
		}

		@Override
		public AddVarAST create(String id)
		{
			return new AddVarAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof AddVarAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public AddVarAST(Template template, Slice pos, LValue lvalue, AST value)
	{
		super(template, pos, lvalue, value);
	}

	public String getType()
	{
		return "addvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateAdd(context, value.decoratedEvaluate(context));
		return null;
	}
}
