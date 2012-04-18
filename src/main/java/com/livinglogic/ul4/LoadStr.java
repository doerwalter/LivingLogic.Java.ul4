/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class LoadStr extends LoadConst
{
	protected String value;

	public LoadStr(String value)
	{
		this.value = value;
	}

	public String getType()
	{
		return "loadstr";
	}

	public Object getValue()
	{
		return value;
	}

	public String toString(int indent)
	{
		return Utils.repr(value);
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return value;
	}
}
