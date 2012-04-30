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

import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;

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

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj1);
		encoder.dump(obj2);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj1 = (AST)decoder.load();
		obj2 = (AST)decoder.load();
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
