/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.ArrayList;
import java.util.List;


public class OperatorItemGetter extends Function implements UL4Instance, UL4Repr, UL4Dir
{
	protected static class Type extends AbstractInstanceType
	{
		@Override
		public String getModuleName()
		{
			return "operator";
		}

		@Override
		public String getNameUL4()
		{
			return "itemgetter";
		}

		@Override
		public String getDoc()
		{
			return "A callable object that fetches the given item(s) from its operand.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof OperatorItemGetter;
		}

		private static final Signature signature = new Signature().addVarPositional("items");

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		@Override
		public Object create(EvaluationContext context, BoundArguments args)
		{
			List arg = (List)args.get(0);

			int argCount = arg.size();
			if (argCount == 0)
				throw new ArgumentCountMismatchException("type", "itemgetter", argCount, 1, -1);

			return new OperatorItemGetter(arg);
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	@Override
	public String getModuleName()
	{
		return "operator";
	}

	@Override
	public String getNameUL4()
	{
		StringBuilder buffer = new StringBuilder();
		boolean first = true;
		buffer.append("itemgetter(");

		for (Object itemIndex : itemIndices)
		{
			if (first)
				first = false;
			else
				buffer.append(", ");
			buffer.append(FunctionRepr.call(itemIndex));
		}
		buffer.append(")");
		return buffer.toString();
	}

	private List<Object> itemIndices;

	public OperatorItemGetter(List<Object> itemIndices)
	{
		this.itemIndices = new ArrayList<Object>(itemIndices);
	}

	private static final Signature signature = new Signature().addPositionalOnly("obj");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, args.get(0), itemIndices);
	}

	private static Object call(EvaluationContext context, Object obj, List<Object> itemIndices)
	{
		if (itemIndices.size() == 1)
			return call(context, obj, itemIndices.get(0));
		else
		{
			List<Object> result = new ArrayList<Object>(itemIndices.size());
			for (Object itemIndex : itemIndices)
				result.add(call(context, obj, itemIndex));
			return result;
		}
	}

	private static Object call(EvaluationContext context, Object obj, Object itemIndex)
	{
		return UL4Type.getType(obj).getItem(context, obj, itemIndex);
	}
}
