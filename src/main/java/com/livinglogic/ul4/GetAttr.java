/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;

public class GetAttr extends AST
{
	protected AST obj;
	protected String attrname;

	public GetAttr(Location location, AST obj, String attrname)
	{
		super(location);
		this.obj = obj;
		this.attrname = attrname;
	}

	public String toString(int indent)
	{
		return "getattr(" + obj + ", " + FunctionRepr.call(attrname) + ")";
	}

	public String getType()
	{
		return "getattr";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return Utils.getItem(obj.decoratedEvaluate(context), attrname);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj);
		encoder.dump(attrname);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj = (AST)decoder.load();
		attrname = (String)decoder.load();
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("obj", new ValueMaker(){public Object getValue(Object object){return ((GetAttr)object).obj;}});
			v.put("attrname", new ValueMaker(){public Object getValue(Object object){return ((GetAttr)object).attrname;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
