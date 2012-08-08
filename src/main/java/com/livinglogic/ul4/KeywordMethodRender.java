/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class KeywordMethodRender implements KeywordMethod
{
	public String getName()
	{
		return "render";
	}

	public Object evaluate(EvaluationContext context, Object obj, Map<String, Object> args) throws IOException
	{
		return call(context.getWriter(), obj, args);
	}

	public static Object call(Writer writer, Template template, Map<String, Object> variables)
	{
		try
		{
			template.render(writer, variables);
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
		return null;
	}

	public static Object call(Writer writer, Object template, Map<String, Object> variables)
	{
		if (template instanceof Template)
			return call(writer, (Template)template, variables);
		throw new ArgumentTypeMismatchException("{}.render({})", template, variables);
	}
}
