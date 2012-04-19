/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodRenderS implements Method
{
	public Object call(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		if (null != obj && obj instanceof Template)
		{
			switch (args.length)
			{
				case 0:
					return ((Template)obj).renders(null);
				default:
					throw new ArgumentCountMismatchException("method", "renders", args.length, 0);
			}
		}
		throw new UnsupportedOperationException("renders() method requires a template!");

	}

	public String getName()
	{
		return "renders";
	}
}
