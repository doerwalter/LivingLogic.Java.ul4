/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class IsNotAST extends BinaryAST
{
	protected static class Type extends BinaryAST.Type
	{
		@Override
		public String getModuleName()
		{
			return "ul4";
		}

		@Override
		public String getNameUL4()
		{
			return "IsNotAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.isnot";
		}

		@Override
		public String getDoc()
		{
			return "An `is not` expression (`x is not y`).";
		}

		@Override
		public IsNotAST create(String id)
		{
			return new IsNotAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof IsNotAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public IsNotAST(Template template, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		super(template, pos, obj1, obj2);
	}

	public String getType()
	{
		return "isnot";
	}

	public static CodeAST make(Template template, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		if (obj1 instanceof ConstAST && obj2 instanceof ConstAST)
		{
			// No need to catch any exception here or check for {@code Undefined}, the "is not" oparator can't fail
			boolean result = call(((ConstAST)obj1).value, ((ConstAST)obj2).value);
			return new ConstAST(template, pos, result);
		}
		return new IsNotAST(template, pos, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static boolean call(Object obj1, Object obj2)
	{
		return obj1 != obj2;
	}
}
