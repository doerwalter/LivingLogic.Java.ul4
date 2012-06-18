/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodLum implements Method
{
	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		if (args.length == 0)
		{
			if (obj != null && obj instanceof Color)
				return ((Color)obj).lum();
			throw new UnsupportedOperationException(Utils.objectType(obj) + ".lum) not supported!");
		}
		throw new ArgumentCountMismatchException("method", "lum", args.length, 0);
	}

	public String getName()
	{
		return "lum";
	}
}
