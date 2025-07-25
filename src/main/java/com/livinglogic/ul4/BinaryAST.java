/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
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
The base class of all nodes that model binary operations.
**/
public abstract class BinaryAST extends CodeAST
{
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "BinaryAST";
		}

		@Override
		public String getDoc()
		{
			return "Base class for all UL4 AST nodes implementing binary expressions\n(i.e. operators with two operands).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof BinaryAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	/**
	The AST node for the left operand.
	**/
	protected CodeAST obj1;

	/**
	The AST node for the right operand.
	**/
	protected CodeAST obj2;

	/**
	Create a new {@code BinaryAST} object
	@param template The template this node belongs to.
	@param obj1 The left operand
	@param obj2 The right operand
	**/
	public BinaryAST(Template template, int posStart, int posStop, CodeAST obj1, CodeAST obj2)
	{
		super(template, posStart, posStop);
		this.obj1 = obj1;
		this.obj2 = obj2;
	}

	public CodeAST getObj1()
	{
		return obj1;
	}

	public CodeAST getObj2()
	{
		return obj2;
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj1);
		encoder.dump(obj2);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj1 = (CodeAST)decoder.load();
		obj2 = (CodeAST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "obj1", "obj2");

	public Set<String> dirUL4(EvaluationContext context)
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
	{
		switch (key)
		{
			case "obj1":
				return obj1;
			case "obj2":
				return obj2;
			default:
				return super.getAttrUL4(context, key);
		}
	}
}
