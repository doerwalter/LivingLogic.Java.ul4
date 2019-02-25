/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
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
 * The base class of all nodes that model unary operations.
 */
abstract class UnaryAST extends CodeAST
{
	/**
	 * The operand of the unary operation
	 */
	protected CodeAST obj;

	/**
	 * Create a new {@code UnaryAST} object
	 * @param template The template where this node appears in.
	 * @param slice The position of this node in the sourcecode of the template
	 * @param obj The operand
	 */
	public UnaryAST(InterpretedTemplate template, Slice pos, CodeAST obj)
	{
		super(template, pos);
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
		obj = (CodeAST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "obj");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "obj":
				return obj;
			default:
				return super.getAttrUL4(key);
		}
	}
}
