/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

/**
 * The base class of all nodes that model unary operations.
 */
abstract class Unary extends AST
{
	/**
	 * The operand of the unary operation
	 */
	protected AST obj;

	/**
	 * Create a new {@code Unary} object
	 * @param location The source code location where this node appears in.
	 * @param obj The operand
	 */
	public Unary(Location location, int start, int end, AST obj)
	{
		super(location, start, end);
		this.obj = obj;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj = (AST)decoder.load();
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
