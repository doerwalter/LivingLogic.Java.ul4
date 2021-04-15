/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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
The base class of all nodes that model unary operations.
**/
public abstract class UnaryAST extends CodeAST
{
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "UnaryAST";
		}

		@Override
		public String getDoc()
		{
			return "An unary expression (i.e. an expression with one operand).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof UnaryAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	/**
	The operand of the unary operation
	**/
	protected CodeAST obj;

	/**
	Create a new {@code UnaryAST} object
	@param template The template where this node appears in.
	@param pos The position of this node in the sourcecode of the template
	@param obj The operand
	**/
	public UnaryAST(Template template, Slice pos, CodeAST obj)
	{
		super(template, pos);
		this.obj = obj;
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj);
	}

	@Override
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

	@Override
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
