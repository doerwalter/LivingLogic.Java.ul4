/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodRender implements Method
{
	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		if (null != obj && obj instanceof Template)
		{
			switch (args.length)
			{
				case 0:
					((Template)obj).render(context.getWriter(), null);
					return null;
				default:
					throw new ArgumentCountMismatchException("method", "render", args.length, 0);
			}
		}
		throw new UnsupportedOperationException("render() method requires a template!");

	}

	public String getName()
	{
		return "render";
	}
}
