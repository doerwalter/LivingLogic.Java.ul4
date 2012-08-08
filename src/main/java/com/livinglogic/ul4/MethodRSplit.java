/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class MethodRSplit implements Method
{
	public String getName()
	{
		return "rsplit";
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
				throw new ArgumentCountMismatchException("method", "rsplit", args.length, 0, 2);
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
		throw new ArgumentTypeMismatchException("{}.rsplit()", obj);
	}

	public static List<String> call(String obj, int maxsplit)
	{
		ArrayList<String> result = new ArrayList<String>();
		int start, end;
		start = end = obj.length() - 1;
		while (maxsplit-- > 0)
		{
			while (start >= 0 && Character.isWhitespace(obj.charAt(start)))
				--start;
			if (start < 0)
				break;
			end = start--;
			while (start >= 0 && !Character.isWhitespace(obj.charAt(start)))
				--start;
			if (start != end)
				result.add(0, obj.substring(start+1, end+1));
		}
		if (start >= 0)
		{
			while (start >= 0 && Character.isWhitespace(obj.charAt(start)))
				--start;
			if (start >= 0)
				result.add(0, obj.substring(0, start+1));
		}
		return result;
	}

	public static List<String> call(Object obj, Object separator)
	{
		if (obj instanceof String)
		{
			if (separator == null)
				return call((String)obj);
			else if (separator instanceof String)
				return call((String)obj, (String)separator, 0x7fffffff);
		}
		throw new ArgumentTypeMismatchException("{}.rsplit({})", obj, separator);
	}

	public static List<String> call(String obj, String separator, int maxsplit)
	{
		if (separator.length() == 0)
			throw new UnsupportedOperationException("empty separator not supported");

		ArrayList<String> result = new ArrayList<String>();
		int start = obj.length(), end = start, seplen = separator.length();
		while (maxsplit-- > 0)
		{
			start = obj.lastIndexOf(separator, end-seplen);
			if (start < 0)
				break;
			result.add(0, obj.substring(start+seplen, end));
			end = start;
		}
		result.add(0, obj.substring(0, end));
		return result;
	}

	public static List<String> call(Object obj, Object separator, Object maxsplit)
	{
		if (obj instanceof String)
		{
			if (separator == null)
				return call((String)obj, Utils.toInt(maxsplit));
			else if (separator instanceof String)
				return call((String)obj, (String)separator, Utils.toInt(maxsplit));
		}
		throw new ArgumentTypeMismatchException("{}.rsplit({}, {})", obj, separator, maxsplit);
	}
}
