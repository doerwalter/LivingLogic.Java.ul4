/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class Const extends AST
{
	protected Object value;

	public Const(Object value)
	{
		super();
		this.value = value;
	}

	public String getType()
	{
		return "const";
	}

	public String toString(InterpretedTemplate template, int indent)
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
		value = decoder.load();
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("value", new ValueMaker(){public Object getValue(Object object){return ((Const)object).value;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
