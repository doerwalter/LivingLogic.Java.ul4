/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class GetSlice extends AST
{
	protected AST obj;
	protected AST index1;
	protected AST index2;

	public GetSlice(Location location, AST obj, AST index1, AST index2)
	{
		super(location);
		this.obj = obj;
		this.index1 = index1;
		this.index2 = index2;
	}

	public String toString(int indent)
	{
		return "getslice(" + obj + ", " + index1 + ", " + index2 + ")";
	}

	public String getType()
	{
		return "getslice";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return Utils.getSlice(obj.evaluate(context), index1 != null ? index1.evaluate(context) : null, index2 != null ? index2.evaluate(context) : null);
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("obj", new ValueMaker(){public Object getValue(Object object){return ((GetSlice)object).obj;}});
			v.put("index1", new ValueMaker(){public Object getValue(Object object){return ((GetSlice)object).index1;}});
			v.put("index2", new ValueMaker(){public Object getValue(Object object){return ((GetSlice)object).index2;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
