/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class Name extends AST
{
	protected String value;

	public Name(Location location, String value)
	{
		super(location);
		this.value = value;
	}

	public String toString(int indent)
	{
		return value;
	}

	public String getType()
	{
		return "loadvar";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return context.get(value);
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("name", new ValueMaker(){public Object getValue(Object object){return ((Name)object).value;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
