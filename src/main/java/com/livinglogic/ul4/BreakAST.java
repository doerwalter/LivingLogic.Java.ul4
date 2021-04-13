/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class BreakAST extends CodeAST
{
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "BreakAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.break";
		}

		@Override
		public String getDoc()
		{
			return "A break tag.";
		}

		@Override
		public BreakAST create(String id)
		{
			return new BreakAST(null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof BreakAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public BreakAST(Template template, Slice pos)
	{
		super(template, pos);
	}

	public String getType()
	{
		return "break";
	}

	public Object evaluate(EvaluationContext context)
	{
		throw new BreakException();
	}

	public String toString(int indent)
	{
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("break\n");
		return buffer.toString();
	}
}
