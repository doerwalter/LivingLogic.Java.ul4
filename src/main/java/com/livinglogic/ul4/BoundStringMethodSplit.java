/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class BoundStringMethodSplit extends BoundMethod<String>
{
	private static Signature signature = new Signature("split", "sep", null, "count", null);

	public BoundStringMethodSplit(String object)
	{
		super(object);
	}

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

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);

		Object separator = args[0];
		Object maxsplit = args[1];

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
		throw new ArgumentTypeMismatchException("{}.split({}, {})", object, separator, maxsplit);
	}
}
