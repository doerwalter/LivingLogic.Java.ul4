/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


/**
AST node for a {@code <?continue?>} tag inside a {@code <?for?>} block.
**/
public class ContinueAST extends CodeAST
{
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ContinueAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.continue";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a `<?continue?>` tag inside a `<?for?>` block.";
		}

		@Override
		public ContinueAST create(String id)
		{
			return new ContinueAST(null, -1, -1);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ContinueAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ContinueAST(Template template, int posStart, int posStop)
	{
		super(template, posStart, posStop);
	}

	public String getType()
	{
		return "continue";
	}

	@Override
	public Object evaluate(EvaluationContext context)
	{
		throw new ContinueException();
	}

	public String toString(int indent)
	{
		return "continue";
	}
}
