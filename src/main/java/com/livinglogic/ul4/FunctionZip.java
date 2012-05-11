/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;

public class FunctionZip implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		return Utils.zip(args);
	}

	public String getName()
	{
		return "zip";
	}
}
