/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class MethodSplit extends NormalMethod
{
	public String getName()
	{
		return "split";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("sep", null);
		signature.add("count", null);
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args) throws IOException
	{
		return call(obj, args[0], args[1]);
	}

	public static List<String> call(String obj)
	{
		return Arrays.asList(StringUtils.split(obj));
	}

	public static List<String> call(String obj, String separator)
	{
		if (separator == null)
			return call(obj);
		return Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens(obj, separator));
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
			if (maxsplit == null)
			{
				if (separator == null)
					return call((String)obj, null);
				else if (separator instanceof String)
					return call((String)obj, (String)separator);
			}
			else
			{
				if (separator == null)
					return call((String)obj, null, Utils.toInt(maxsplit));
				else if (separator instanceof String)
					return call((String)obj, (String)separator, Utils.toInt(maxsplit));
			}
		}
		throw new ArgumentTypeMismatchException("{}.split({}, {})", obj, separator, maxsplit);
	}
}
