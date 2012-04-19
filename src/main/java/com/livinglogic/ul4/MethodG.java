/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodG implements Method
{
	public Object call(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		if (args.length == 0)
		{
			if (obj != null && obj instanceof Color)
				return ((Color)obj).getG();
			throw new UnsupportedOperationException(Utils.objectType(obj) + ".g() not supported!");
		}
		throw new ArgumentCountMismatchException("method", "g", args.length, 0);
	}

	public String getName()
	{
		return "g";
	}
}
