/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class Var extends AST
{
	protected String name;

	public Var(String name)
	{
		super();
		this.name = name;
	}

	public String toString(InterpretedCode code, int indent)
	{
		return name;
	}

	public String getType()
	{
		return "var";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return context.get(name);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(name);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		name = (String)decoder.load();
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("name", new ValueMaker(){public Object getValue(Object object){return ((Var)object).name;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
