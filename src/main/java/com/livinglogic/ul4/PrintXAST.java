/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
{@code PrintXAST} is an unary AST node that writes a string version of its
operand to the output stream and replaces the characters {@code <}, {@code >},
{@code &}, {@code '} and {@code "} with the appropriate XML character
entities.
**/
public class PrintXAST extends UnaryAST
{
	protected static class Type extends UnaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "PrintXAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.printx";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a ``<?printx?>`` tag (e.g. ``<?printx x?>``.";
		}

		@Override
		public PrintXAST create(String id)
		{
			return new PrintXAST(null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof PrintXAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public PrintXAST(Template template, Slice pos, CodeAST obj)
	{
		super(template, pos, obj);
	}

	public void toString(Formatter formatter)
	{
		formatter.write("printx ");
		super.toString(formatter);
	}

	public String getType()
	{
		return "printx";
	}

	public Object evaluate(EvaluationContext context)
	{
		context.write(FunctionXMLEscape.call(obj.decoratedEvaluate(context)));
		return null;
	}
}
