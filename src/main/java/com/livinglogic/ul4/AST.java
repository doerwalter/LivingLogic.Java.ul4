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
import com.livinglogic.ul4on.Utils;
import com.livinglogic.utils.ObjectAsMap;

public abstract class AST extends ObjectAsMap
{
	protected Location location = null;

	public AST(Location location)
	{
		this.location = location;
	}

	abstract public Object evaluate(EvaluationContext context) throws IOException;

	abstract public String getType();

	public Location getLocation()
	{
		return location;
	}

	public String toString()
	{
		return toString(0);
	}

	abstract public String toString(int indent);

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>();
			v.put("type", new ValueMaker(){public Object getValue(Object object){return ((AST)object).getType();}});
			v.put("location", new ValueMaker(){public Object getValue(Object object){return ((AST)object).getLocation();}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
