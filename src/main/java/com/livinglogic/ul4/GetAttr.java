/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class GetAttr extends AST
{
	protected AST obj;
	protected String attrname;

	public GetAttr(AST obj, String attrname)
	{
		this.obj = obj;
		this.attrname = attrname;
	}

	public String toString(int indent)
	{
		return "getattr(" + obj + ", " + Utils.repr(attrname) + ")";
	}

	public String getType()
	{
		return "getattr";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return Utils.getItem(obj.evaluate(context), attrname);
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
