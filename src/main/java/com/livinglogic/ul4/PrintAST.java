/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
{@code PrintAST} is an unary AST node that writes a string version of its
operand to the output stream.
**/
public class PrintAST extends UnaryAST
{
	protected static class Type extends UnaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "PrintAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.print";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a ``<?print?>`` tag (e.g. ``<?print x?>``).";
		}

		@Override
		public PrintAST create(String id)
		{
			return new PrintAST(null, -1, -1, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof PrintAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public PrintAST(Template template, int posStart, int posStop, CodeAST obj)
	{
		super(template, posStart, posStop, obj);
	}

	public void toString(Formatter formatter)
	{
		formatter.write("print ");
		super.toString(formatter);
	}

	public String getType()
	{
		return "print";
	}

	public Object evaluate(EvaluationContext context)
	{
		context.write(Str.call(context, obj.decoratedEvaluate(context)));
		return null;
	}
}
