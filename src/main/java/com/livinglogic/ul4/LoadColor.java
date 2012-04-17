/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class LoadColor extends LoadConst
{
	protected Color value;

	public LoadColor(Color value)
	{
		this.value = value;
	}

	public String name()
	{
		return "loadcolor";
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
