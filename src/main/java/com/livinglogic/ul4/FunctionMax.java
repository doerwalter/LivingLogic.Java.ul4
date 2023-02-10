/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;

import static java.util.Arrays.asList;


public class FunctionMax extends Function
{
	@Override
	public String getNameUL4()
	{
		return "max";
	}

	private static final Signature signature = new Signature()
		.addKeywordOnly("default", Signature.noValue)
		.addKeywordOnly("key", null)
		.addVarPositional("args")
	;

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		Object defaultValue = args.get(0);
		Object key = args.get(1);
		List<Object> argList = (List<Object>)args.get(2);
		if (argList.size() == 0)
			throw new MissingArgumentException("max", "args", 0);
		return call(context, argList, defaultValue, key);
	}

	public static Object call(EvaluationContext context, List<Object> objs, Object defaultValue, Object key)
	{
		Iterator iter = Utils.iterator(objs.size() == 1 ? objs.get(0) : objs);

		Object maxValue = null;
		Object maxKey = null;
		boolean first = true;

		for (;iter.hasNext();)
		{
			Object testValue = iter.next();
			Object testKey = key != null ? CallAST.call(context, key, asList(testValue), null) : testValue;
			if (first || GTAST.call(context, testKey, maxKey))
			{
				maxValue = testValue;
				maxKey = testKey;
			}
			first = false;
		}
		if (first)
		{
			if (defaultValue == Signature.noValue)
				throw new UnsupportedOperationException("max() arg is an empty sequence!");
			else
				maxValue = defaultValue;
		}
		return maxValue;
	}

	public static final Function function = new FunctionMax();
}
