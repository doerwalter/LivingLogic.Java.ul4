/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
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
			return "AST node for a ``<?break?>`` tag inside a ``<?for?>`` loop.";
		}

		@Override
		public BreakAST create(String id)
		{
			return new BreakAST(null, -1, -1);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof BreakAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public BreakAST(Template template, int posStart, int posStop)
	{
		super(template, posStart, posStop);
	}

	public String getType()
	{
		return "break";
	}

	@Override
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
