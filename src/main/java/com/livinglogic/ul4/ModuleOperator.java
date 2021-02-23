/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;
import java.util.List;

import static com.livinglogic.utils.SetUtils.makeSet;


public class ModuleOperator extends Module
{
	public ModuleOperator()
	{
		super("operator", "Various operators as functions");
		addObject(attrGetter);
	}

	private static abstract class OperatorFunction extends Function
	{
		@Override
		public String getFullNameUL4()
		{
			return "operator." + getNameUL4();
		}
	}

	private static class FunctionAttrGetter extends OperatorFunction
	{
		@Override
		public String getNameUL4()
		{
			return "attrgetter";
		}

		private static final Signature signature = new Signature("attr", Signature.remainingParameters);

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		@Override
		public Object evaluate(BoundArguments arguments)
		{
			List<Object> arg = (List<Object>)arguments.get(0);

			int argCount = arg.size();
			if (argCount == 0)
				throw new ArgumentCountMismatchException("type", "attrgetter", argCount, 1, -1);

			for (Object attrName : arg)
			{
				if (!(attrName instanceof String))
					throw new ArgumentTypeMismatchException(getFullNameUL4() + " arguments must be strings");
			}

			return new OperatorAttrGetter(arg.toArray(new String[0]));
		}
	}

	private static FunctionAttrGetter attrGetter = new FunctionAttrGetter();

	public static Module module = new ModuleOperator();
}
