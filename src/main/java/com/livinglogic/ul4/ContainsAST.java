/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
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
			return "AST node for the binary containment testing operator (e.g. ``x in y``).";
		}

		@Override
		public ContainsAST create(String id)
		{
			return new ContainsAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ContainsAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ContainsAST(Template template, int posStart, int posStop, CodeAST obj1, CodeAST obj2)
	{
		super(template, posStart, posStop, obj1, obj2);
	}

	public String getType()
	{
		return "contains";
	}

	@Override
	public Object evaluate(EvaluationContext context)
	{
		return call(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static boolean call(EvaluationContext context, String obj, String container)
	{
		return container.indexOf(obj) >= 0;
	}

	public static boolean call(EvaluationContext context, Object obj, Collection container)
	{
		return container.contains(obj);
	}

	public static boolean call(EvaluationContext context, Object obj, Object[] container)
	{
		return asList(container).contains(obj);
	}

	public static boolean call(EvaluationContext context, Object obj, Map container)
	{
		return container.containsKey(obj);
	}

	public static boolean call(EvaluationContext context, Object obj, Object container)
	{
		if (container instanceof String)
		{
			if (obj instanceof String)
				return call(context, (String)obj, (String)container);
		}
		else if (container instanceof Collection)
			return call(context, obj, (Collection)container);
		else if (container instanceof Object[])
			return call(context, obj, (Object[])container);
		else if (container instanceof Map)
			return call(context, obj, (Map)container);
		throw new ArgumentTypeMismatchException("{!t} in {!t} not supported", obj, container);
	}
}
