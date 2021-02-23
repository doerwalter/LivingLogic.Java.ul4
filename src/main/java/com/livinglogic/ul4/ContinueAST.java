/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ContinueAST extends CodeAST
{
	protected static class Type extends AbstractInstanceType
	{
		public Type()
		{
			super("ul4", "ContinueAST", "de.livinglogic.ul4.continue", "A continue tag.");
		}

		@Override
		public ContinueAST create(String id)
		{
			return new ContinueAST(null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ContinueAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ContinueAST(Template template, Slice pos)
	{
		super(template, pos);
	}

	public String getType()
	{
		return "continue";
	}

	public Object evaluate(EvaluationContext context)
	{
		throw new ContinueException();
	}

	public String toString(int indent)
	{
		return "continue";
	}
}
