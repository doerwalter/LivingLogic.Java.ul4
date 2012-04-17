/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class LoadNone extends LoadConst
{
	public LoadNone()
	{
	}

	public String name()
	{
		return "loadnone";
	}

	public Object getValue()
	{
		return null;
	}

	public String toString()
	{
		return "None";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return null;
	}
}
