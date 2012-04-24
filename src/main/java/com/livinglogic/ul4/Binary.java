/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

abstract class Binary extends AST
{
	protected AST obj1;
	protected AST obj2;

	public Binary(Location location, AST obj1, AST obj2)
	{
		super(location);
		this.obj1 = obj1;
		this.obj2 = obj2;
	}

	public String toString(int indent)
	{
		return getType() + "(" + obj1 + ", " + obj2 + ")";
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("obj1", new ValueMaker(){public Object getValue(Object object){return ((Binary)object).obj1;}});
			v.put("obj2", new ValueMaker(){public Object getValue(Object object){return ((Binary)object).obj2;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
