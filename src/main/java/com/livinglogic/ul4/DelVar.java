/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

import java.util.Map;
import java.util.HashMap;

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
		buffer.append("delvar(" + Utils.repr(varname) + ")\n");
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
