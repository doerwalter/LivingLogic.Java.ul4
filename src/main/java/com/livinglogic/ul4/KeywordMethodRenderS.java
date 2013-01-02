/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Map;

public class KeywordMethodRenderS implements KeywordMethod
{
	public String getName()
	{
		return "renders";
	}

	public Object evaluate(EvaluationContext context, Object obj, Map<String, Object> args) throws IOException
	{
		return call(obj, args);
	}

	public static String call(Template template, Map<String, Object> variables)
	{
		return template.renders(variables);
	}

	public static String call(Object template, Map<String, Object> variables)
	{
		if (template instanceof Template)
			return call((Template)template, variables);
		throw new ArgumentTypeMismatchException("{}.renders({})", template, variables);
	}
}
