/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class LoadTrue extends LoadConst
{
	public LoadTrue(Location location)
	{
		super(location);
	}

	public String getType()
	{
		return "true";
	}

	public Object getValue()
	{
		return Boolean.TRUE;
	}

	public String toString(int indent)
	{
		return "True";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return true;
	}
}
