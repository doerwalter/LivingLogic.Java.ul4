/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;

public class LoadStr extends LoadConst
{
	protected String value;

	public LoadStr(Location location, String value)
	{
		super(location);
		this.value = value;
	}

	public String getType()
	{
		return "str";
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
		value = (String)decoder.load();
	}
}
