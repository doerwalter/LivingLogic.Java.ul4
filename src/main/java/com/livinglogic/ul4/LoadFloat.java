/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class LoadFloat extends LoadConst
{
	protected double value;

	public LoadFloat(Location location, double value)
	{
		super(location);
		this.value = value;
	}

	public String getType()
	{
		return "loadfloat";
	}

	public Object getValue()
	{
		return new Double(value);
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
