/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class MethodSplit implements Method
{
	public String getName()
	{
		return "split";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return call(obj);
			case 1:
				return call(obj, args[0]);
			case 2:
				return call(obj, args[0], args[1]);
			default:
				throw new ArgumentCountMismatchException("method", "split", args.length, 0, 2);
		}
	}

	public static List<String> call(String obj)
	{
		return Arrays.asList(StringUtils.split(obj));
	}

	public static List<String> call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		throw new ArgumentTypeMismatchException("{}.split()", obj);
	}

	public static List<String> call(String obj, String separator)
	{
		if (separator == null)
			return call(obj);
		return Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens(obj, separator));
	}

	public static List<String> call(Object obj, Object separator)
	{
		if (obj instanceof String)
		{
			if (separator == null)
				return call((String)obj);
			else if (separator instanceof String)
				return call((String)obj, (String)separator);
		}
		throw new ArgumentTypeMismatchException("{}.split({})", obj, separator);
	}

	public static List<String> call(String obj, String separator, int maxsplit)
	{
		if (separator == null)
			return Arrays.asList(StringUtils.splitByWholeSeparator(obj, null, maxsplit+1));
		return Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens(obj, separator, maxsplit+1));
	}

	public static List<String> call(Object obj, Object separator, Object maxsplit)
	{
		if (obj instanceof String)
		{
			if (separator == null)
				return call((String)obj, null, Utils.toInt(maxsplit));
			else if (separator instanceof String)
				return call((String)obj, (String)separator, Utils.toInt(maxsplit));
		}
		throw new ArgumentTypeMismatchException("{}.split({}, {})", obj, separator, maxsplit);
	}
}
