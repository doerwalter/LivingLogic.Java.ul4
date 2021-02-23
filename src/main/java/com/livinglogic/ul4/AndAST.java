/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class AndAST extends BinaryAST
{
	protected static class Type extends BinarAST.Type
	{
		@Override
		public String getModuleName()
		{
			return "ul4";
		}

		@Override
		public String getNameUL4()
		{
			return "AndAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.and";
		}

		@Override
		public String getDoc()
		{
			return "A logical \"and\" expression (`x and y`).";
		}

		@Override
		public AndAST create(String id)
		{
			return new AndAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof AndAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public AndAST(Template template, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		super(template, pos, obj1, obj2);
	}

	public String getType()
	{
		return "and";
	}

	public static CodeAST make(Template template, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		if (obj1 instanceof ConstAST && obj2 instanceof ConstAST)
		{
			try
			{
				Object result = call(((ConstAST)obj1).value, ((ConstAST)obj2).value);
				if (!(result instanceof Undefined))
					return new ConstAST(template, pos, result);
			}
			catch (Exception ex)
			{
				// fall through to create a real {@code AndAST} object
			}
		}
		return new AndAST(template, pos, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context)
	{
		Object obj1ev = obj1.decoratedEvaluate(context);
		if (!FunctionBool.call(obj1ev))
			return obj1ev;
		else
			return obj2.decoratedEvaluate(context);
	}

	// this static version is only used for constant folding, not in evaluate(), because that would require that we evaluate both sides
	public static Object call(Object arg1, Object arg2)
	{
		return !FunctionBool.call(arg1) ? arg1 : arg2;
	}
}
