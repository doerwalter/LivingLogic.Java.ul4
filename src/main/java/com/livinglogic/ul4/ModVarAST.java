/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ModVarAST extends ChangeVarAST
{
	protected static class Type extends AbstractInstanceType
	{
		public Type()
		{
			super("ul4", "ModVarAST", "de.livinglogic.ul4.modvar", "An augmented modulo assignment (x %= y).");
		}

		@Override
		public ModVarAST create(String id)
		{
			return new ModVarAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ModVarAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ModVarAST(Template template, Slice pos, LValue lvalue, AST value)
	{
		super(template, pos, lvalue, value);
	}

	public String getType()
	{
		return "modvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateMod(context, value.decoratedEvaluate(context));
		return null;
	}
}
