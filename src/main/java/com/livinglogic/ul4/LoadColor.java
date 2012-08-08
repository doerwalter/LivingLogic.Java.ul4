/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class LoadColor extends LoadConst
{
	protected Color value;

	public LoadColor(Location location, Color value)
	{
		super(location);
		this.value = value;
	}

	public String getType()
	{
		return "color";
	}

	public Object getValue()
	{
		return value;
	}

	public String toString(int indent)
	{
		return FunctionRepr.call(value);
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return value;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(value);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		value = (Color)decoder.load();
	}
}
