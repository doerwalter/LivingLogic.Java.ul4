/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class StoreVar extends AST
{
	/**
	 * This is either a string or a list of strings/lists
	 */
	protected Object varname;
	protected AST value;

	public StoreVar(Location location, int start, int end, Object varname, AST value)
	{
		super(location, start, end);
		this.varname = varname;
		this.value = value;
	}

	public String getType()
	{
		return "storevar";
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(varname);
		encoder.dump(value);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		varname = decoder.load();
		value = (AST)decoder.load();
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.unpackVariable(varname, value.decoratedEvaluate(context));
		return null;
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("varname", new ValueMaker(){public Object getValue(Object object){return ((ChangeVar)object).varname;}});
			v.put("value", new ValueMaker(){public Object getValue(Object object){return ((ChangeVar)object).value;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
