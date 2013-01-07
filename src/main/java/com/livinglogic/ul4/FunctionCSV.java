/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

public class FunctionCSV extends NormalFunction
{
	public String getName()
	{
		return "csv";
	}

	protected void makeArgumentDescriptions(ArgumentDescriptions argumentDescriptions)
	{
		argumentDescriptions.add("obj");
	}

	public Object evaluate(EvaluationContext context, Object[] args)
	{
		return call(args[0]);
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
