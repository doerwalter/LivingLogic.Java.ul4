/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.io.IOException;

public class KeywordMethodRender implements KeywordMethod
{
	public Object call(EvaluationContext context, Object obj, Map<String, Object> args) throws IOException
	{
		if (null != obj && obj instanceof Template)
		{
			((Template)obj).render(context.getWriter(), args);
			return null;
		}
		throw new UnsupportedOperationException("render() method requires a template!");
	}

	public String getName()
	{
		return "render";
	}
}
