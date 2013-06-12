/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeSet;
import static com.livinglogic.utils.SetUtils.union;

import java.io.IOException;
import java.util.Set;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

/**
 * The base class of all nodes that model binary operations.
 */
abstract class Binary extends AST
{
	/**
	 * The AST node for the left operand.
	 */
	protected AST obj1;

	/**
	 * The AST node for the right operand.
	 */
	protected AST obj2;

	/**
	 * Create a new {@code Binary} object
	 * @param location The source code location where this node appears in.
	 * @param obj1 The left operand
	 * @param obj2 The right operand
	 */
	public Binary(Location location, int start, int end, AST obj1, AST obj2)
	{
		super(location, start, end);
		this.obj1 = obj1;
		this.obj2 = obj2;
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

	protected static Set<String> attributes = union(AST.attributes, makeSet("obj1", "obj2"));

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("obj1".equals(key))
			return obj1;
		else if ("obj2".equals(key))
			return obj2;
		else
			return super.getItemStringUL4(key);
	}
}
