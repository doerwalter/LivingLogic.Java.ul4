/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class LoadTrue extends LoadConst
{
	public LoadTrue()
	{
	}

	public String name()
	{
		return "loadtrue";
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
