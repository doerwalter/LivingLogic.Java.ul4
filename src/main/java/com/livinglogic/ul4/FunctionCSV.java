/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import org.apache.commons.lang.StringEscapeUtils;

public class FunctionCSV implements Function
{
	public String getName()
	{
		return "csv";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "csv", args.length, 1);
	}

	public static String call(Object obj)
	{
		if (obj == null)
			return "";
		if (!(obj instanceof String))
			obj = FunctionRepr.call(obj);
		return StringEscapeUtils.escapeCsv((String)obj);
	}
}
