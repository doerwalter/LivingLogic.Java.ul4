/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class NotContainsAST extends BinaryAST
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
			return "NotContainsAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.notcontains";
		}

		@Override
		public String getDoc()
		{
			return "An \"inverted containment\" test (i.e. `x not in y`).";
		}

		@Override
		public NotContainsAST create(String id)
		{
			return new NotContainsAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof NotContainsAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public NotContainsAST(Template template, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		super(template, pos, obj1, obj2);
	}

	public String getType()
	{
		return "notcontains";
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
				// fall through to create a real {@code NotContainsAST} object
			}
		}
		return new NotContainsAST(template, pos, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static boolean call(Object obj, Object container)
	{
		return !ContainsAST.call(obj, container);
	}
}
