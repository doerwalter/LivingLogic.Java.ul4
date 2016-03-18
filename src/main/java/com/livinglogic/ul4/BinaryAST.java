/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.io.IOException;
import java.util.Set;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

/**
 * The base class of all nodes that model binary operations.
 */
abstract class BinaryAST extends CodeAST
{
	/**
	 * The AST node for the left operand.
	 */
	protected CodeAST obj1;

	/**
	 * The AST node for the right operand.
	 */
	protected CodeAST obj2;

	/**
	 * Create a new {@code BinaryAST} object
	 * @param location The source code location where this node appears in.
	 * @param obj1 The left operand
	 * @param obj2 The right operand
	 */
	public BinaryAST(Tag tag, int start, int end, CodeAST obj1, CodeAST obj2)
	{
		super(tag, start, end);
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
		obj1 = (CodeAST)decoder.load();
		obj2 = (CodeAST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "obj1", "obj2");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "obj1":
				return obj1;
			case "obj2":
				return obj2;
			default:
				return super.getItemStringUL4(key);
		}
	}
}
