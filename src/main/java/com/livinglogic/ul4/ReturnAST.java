/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * {@code ReturnAST} is an unary AST node that can only be used inside functions
 * and that returns an expression from that function.
 */
public class ReturnAST extends UnaryAST
{
	protected static class Type extends AbstractInstanceType
	{
		public Type()
		{
			super("ul4", "ReturnAST", "de.livinglogic.ul4.return", "A return tag.");
		}

		@Override
		public ReturnAST create(String id)
		{
			return new ReturnAST(null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ReturnAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ReturnAST(Template template, Slice pos, CodeAST obj)
	{
		super(template, pos, obj);
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
