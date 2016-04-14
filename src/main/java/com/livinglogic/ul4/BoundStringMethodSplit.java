/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

public class BoundStringMethodSplit extends BoundMethod<String>
{
	public BoundStringMethodSplit(String object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "str.split";
	}

	private static final Signature signature = new Signature("sep", null, "count", null);

	public Signature getSignature()
	{
		return signature;
	}

	public static List<String> call(String object)
	{
		return Arrays.asList(StringUtils.split(object));
	}

	public static List<String> call(String object, String separator)
	{
		if (separator == null)
			return call(object);
		return Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens(object, separator));
	}

	public static List<String> call(String object, String separator, int maxsplit)
	{
		if (separator == null)
			return Arrays.asList(StringUtils.splitByWholeSeparator(object, null, maxsplit+1));
		return Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens(object, separator, maxsplit+1));
	}

	public Object evaluate(BoundArguments args)
	{
		Object separator = args.get(0);
		Object maxsplit = args.get(1);

		if (maxsplit == null)
		{
			if (separator == null)
				return call(object, null);
			else if (separator instanceof String)
				return call(object, (String)separator);
		}
		else
		{
			if (separator == null)
				return call(object, null, Utils.toInt(maxsplit));
			else if (separator instanceof String)
				return call(object, (String)separator, Utils.toInt(maxsplit));
		}
		throw new ArgumentTypeMismatchException("{!t}.split({!t}, {!t}) not supported", object, separator, maxsplit);
	}
}
