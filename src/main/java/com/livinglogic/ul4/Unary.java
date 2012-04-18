/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.HashMap;

abstract class Unary extends AST
{
	protected AST obj;

	public Unary(AST obj)
	{
		this.obj = obj;
	}

	public String toString(int indent)
	{
		return getType() + "(" + obj + ")";
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("obj", new ValueMaker(){public Object getValue(Object object){return ((Unary)object).obj;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
