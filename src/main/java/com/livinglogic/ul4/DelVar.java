/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;

public class DelVar extends AST
{
	protected String varname;

	public DelVar(Location location, String varname)
	{
		super(location);
		this.varname = varname;
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("delvar(" + FunctionRepr.call(varname) + ")\n");
		return buffer.toString();
	}

	public String getType()
	{
		return "delvar";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.remove(varname);
		return null;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(varname);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		varname = (String)decoder.load();
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("varname", new ValueMaker(){public Object getValue(Object object){return ((ChangeVar)object).varname;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
