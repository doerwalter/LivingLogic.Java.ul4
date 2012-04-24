/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class LoadInt extends LoadConst
{
	protected Object value;

	public LoadInt(Location location, Object value)
	{
		super(location);
		this.value = value;
	}

	public String getType()
	{
		return "loadint";
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
