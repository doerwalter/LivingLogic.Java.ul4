/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.HashMap;

public abstract class LoadConst extends AST
{
	public LoadConst(Location location)
	{
		super(location);
	}

	abstract public Object getValue();

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("value", new ValueMaker(){public Object getValue(Object object){return ((LoadConst)object).getValue();}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
