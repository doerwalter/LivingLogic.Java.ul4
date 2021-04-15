/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Collection;
import java.util.Map;
import static java.util.Arrays.asList;

public class ContainsAST extends BinaryAST
{
	protected static class Type extends BinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ContainsAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.contains";
		}

		@Override
		public String getDoc()
		{
			return "A \"containment\" test (x in y).";
		}

		@Override
		public ContainsAST create(String id)
		{
			return new ContainsAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ContainsAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ContainsAST(Template template, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		super(template, pos, obj1, obj2);
	}

	public String getType()
	{
		return "contains";
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
				// fall through to create a real {@code ContainsAST} object
			}
		}
		return new ContainsAST(template, pos, obj1, obj2);
	}

	@Override
	public Object evaluate(EvaluationContext context)
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static boolean call(String obj, String container)
	{
		return container.indexOf(obj) >= 0;
	}

	public static boolean call(Object obj, Collection container)
	{
		return container.contains(obj);
	}

	public static boolean call(Object obj, Object[] container)
	{
		return asList(container).contains(obj);
	}

	public static boolean call(Object obj, Map container)
	{
		return container.containsKey(obj);
	}

	public static boolean call(Object obj, Object container)
	{
		if (container instanceof String)
		{
			if (obj instanceof String)
				return call((String)obj, (String)container);
		}
		else if (container instanceof Collection)
			return call(obj, (Collection)container);
		else if (container instanceof Object[])
			return call(obj, (Object[])container);
		else if (container instanceof Map)
			return call(obj, (Map)container);
		throw new ArgumentTypeMismatchException("{!t} in {!t} not supported", obj, container);
	}
}
