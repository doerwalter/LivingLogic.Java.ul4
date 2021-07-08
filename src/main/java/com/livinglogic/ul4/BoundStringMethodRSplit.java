/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

public class BoundStringMethodRSplit extends BoundMethod<String>
{
	public BoundStringMethodRSplit(String object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "rsplit";
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

	public static List<String> call(EvaluationContext context, String object, int maxsplit)
	{
		ArrayList<String> result = new ArrayList<String>();
		int start, end;
		start = end = object.length() - 1;
		while (maxsplit-- > 0)
		{
			while (start >= 0 && Character.isWhitespace(object.charAt(start)))
				--start;
			if (start < 0)
				break;
			end = start--;
			while (start >= 0 && !Character.isWhitespace(object.charAt(start)))
				--start;
			if (start != end)
				result.add(0, object.substring(start+1, end+1));
		}
		if (start >= 0)
		{
			while (start >= 0 && Character.isWhitespace(object.charAt(start)))
				--start;
			if (start >= 0)
				result.add(0, object.substring(0, start+1));
		}
		return result;
	}

	public static List<String> call(EvaluationContext context, String object, Object separator)
	{
		if (separator == null)
			return call(context, object);
		else if (separator instanceof String)
			return call(context, object, (String)separator, 0x7fffffff);
		throw new ArgumentTypeMismatchException("{!t}.rsplit({!t}) not supported", object, separator);
	}

	public static List<String> call(EvaluationContext context, String object, String separator, int maxsplit)
	{
		if (separator.length() == 0)
			throw new UnsupportedOperationException("empty separator not supported");

		ArrayList<String> result = new ArrayList<String>();
		int start = object.length(), end = start, seplen = separator.length();
		while (maxsplit-- > 0)
		{
			start = object.lastIndexOf(separator, end-seplen);
			if (start < 0)
				break;
			result.add(0, object.substring(start+seplen, end));
			end = start;
		}
		result.add(0, object.substring(0, end));
		return result;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		Object separator = args.get(0);
		Object maxsplit = args.get(1);

		if (maxsplit == null)
		{
			if (separator == null)
				return call(context, object);
			else if (separator instanceof String)
				return call(context, object, (String)separator);
		}
		else
		{
			if (separator == null)
				return call(context, object, Utils.toInt(maxsplit));
			else if (separator instanceof String)
				return call(context, object, (String)separator, Utils.toInt(maxsplit));
		}
		throw new ArgumentTypeMismatchException("{!t}.rsplit({!t}, {!t}) not supported", object, separator, maxsplit);
	}
}
