/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class BoundStringMethodSplit extends BoundMethod<String>
{
	public BoundStringMethodSplit(String object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "split";
	}

	private static final Signature signature = new Signature().addBoth("sep", null).addBoth("maxsplit", null);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static List<String> call(EvaluationContext context, String object)
	{
		return Utils.array2List(StringUtils.split(object));
	}

	public static List<String> call(EvaluationContext context, String object, String separator)
	{
		if (separator == null)
			return call(context, object);
		return Utils.array2List(StringUtils.splitByWholeSeparatorPreserveAllTokens(object, separator));
	}

	public static List<String> call(EvaluationContext context, String object, String separator, int maxsplit)
	{
		if (separator == null)
			return Utils.array2List(StringUtils.splitByWholeSeparator(object, null, maxsplit+1));
		return Utils.array2List(StringUtils.splitByWholeSeparatorPreserveAllTokens(object, separator, maxsplit+1));
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		Object separator = args.get(0);
		Object maxsplit = args.get(1);

		if (maxsplit == null)
		{
			if (separator == null)
				return call(context, object, null);
			else if (separator instanceof String)
				return call(context, object, (String)separator);
		}
		else
		{
			if (separator == null)
				return call(context, object, null, Utils.toInt(maxsplit));
			else if (separator instanceof String)
				return call(context, object, (String)separator, Utils.toInt(maxsplit));
		}
		throw new ArgumentTypeMismatchException("{!t}.split({!t}, {!t}) not supported", object, separator, maxsplit);
	}
}
