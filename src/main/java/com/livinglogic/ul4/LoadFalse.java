/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class LoadFalse extends LoadConst
{
	public LoadFalse()
	{
	}

	public String getType()
	{
		return "loadfalse";
	}

	public Object getValue()
	{
		return Boolean.FALSE;
	}

	public String toString(int indent)
	{
		return "False";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return false;
	}
}
