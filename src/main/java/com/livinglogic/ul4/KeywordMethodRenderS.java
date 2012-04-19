/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.io.IOException;

public class KeywordMethodRenderS implements KeywordMethod
{
	public Object call(EvaluationContext context, Object obj, Map<String, Object> args) throws IOException
	{
		if (null != obj && obj instanceof Template)
			return ((Template)obj).renders(args);
		throw new UnsupportedOperationException("renders() method requires a template!");
	}

	public String getName()
	{
		return "renders";
	}
}
