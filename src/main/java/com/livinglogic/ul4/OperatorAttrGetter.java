/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;


public class OperatorAttrGetter extends Function implements UL4Instance, UL4Repr, UL4GetAttr, UL4Dir
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
			return "attrgetter";
		}

		@Override
		public String getDoc()
		{
			return "A callable object that fetches the given attribute(s) from its operand.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof OperatorAttrGetter;
		}

		private static final Signature signature = new Signature().addVarPositional("attrs");

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
				throw new ArgumentCountMismatchException("type", "attrgetter", argCount, 1, -1);

			for (Object attrName : arg)
			{
				if (!(attrName instanceof String))
					throw new ArgumentTypeMismatchException(getFullNameUL4() + " arguments must be strings");
			}

			return new OperatorAttrGetter(arg);
		}
	}

	public static final UL4Type type = new Type();

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
		buffer.append("attrgetter(");

		for (String[] attrNames : allAttrNames)
		{
			if (first)
				first = false;
			else
				buffer.append(", ");
			buffer.append(FunctionRepr.call(BoundStringMethodJoin.call(".", attrNames)));
		}
		buffer.append(")");
		return buffer.toString();
	}

	private List<String[]> allAttrNames;

	public OperatorAttrGetter(List<String> attrNames)
	{
		allAttrNames = new ArrayList<String[]>();
		for (String attrName : attrNames)
			allAttrNames.add(StringUtils.splitByWholeSeparatorPreserveAllTokens(attrName, "."));
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
		return call(context, args.get(0), allAttrNames);
	}

	private static Object call(EvaluationContext context, Object obj, List<String[]> allAttrNames)
	{
		if (allAttrNames.size() == 1)
			return call(context, obj, allAttrNames.get(0));
		else
		{
			List<Object> result = new ArrayList<Object>(allAttrNames.size());
			for (String[] attrNames : allAttrNames)
				result.add(call(context, obj, attrNames));
			return result;
		}
	}

	private static Object call(EvaluationContext context, Object obj, String attrName)
	{
		return UL4Type.getType(obj).getAttr(context, obj, attrName);
	}

	private static Object call(EvaluationContext context, Object obj, String[] attrNames)
	{
		for (String attrName : attrNames)
			obj = call(context, obj, attrName);
		return obj;
	}
}
