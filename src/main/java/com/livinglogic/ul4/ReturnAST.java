/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
{@code ReturnAST} is an unary AST node that can only be used inside functions
and that returns an expression from that function.
**/
public class ReturnAST extends UnaryAST
{
	protected static class Type extends UnaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ReturnAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.return";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a `<?return?>` tag (e.g. `<?return x?>`).";
		}

		@Override
		public ReturnAST create(String id)
		{
			return new ReturnAST(null, -1, -1, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ReturnAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ReturnAST(Template template, int posStart, int posStop, CodeAST obj)
	{
		super(template, posStart, posStop, obj);
	}

	public void toString(Formatter formatter)
	{
		formatter.write("return ");
		super.toString(formatter);
	}

	public String getType()
	{
		return "return";
	}

	public Object evaluate(EvaluationContext context)
	{
		throw new ReturnException(obj.decoratedEvaluate(context));
	}
}
