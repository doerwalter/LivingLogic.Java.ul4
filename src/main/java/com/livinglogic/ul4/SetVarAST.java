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

public class SetVarAST extends CodeAST
{
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "SetVarAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.setvar";
		}

		@Override
		public String getDoc()
		{
			return "A variable assignment (i.e. `x = y`).";
		}

		@Override
		public SetVarAST create(String id)
		{
			return new SetVarAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof SetVarAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	/**
	This is either a string or a list of strings/lists
	**/
	protected Object lvalue;
	protected AST value;

	public SetVarAST(Template template, Slice pos, Object lvalue, AST value)
	{
		super(template, pos);
		this.lvalue = lvalue;
		this.value = value;
	}

	public String getType()
	{
		return "setvar";
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(lvalue);
		encoder.dump(value);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		lvalue = decoder.load();
		value = (AST)decoder.load();
	}

	public Object evaluate(EvaluationContext context)
	{
		for (Utils.LValueValue lvv : Utils.unpackVariable(lvalue, value.decoratedEvaluate(context)))
		{
			lvv.getLValue().evaluateSet(context, lvv.getValue());
		}
		return null;
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "lvalue", "value");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "lvalue":
				return lvalue;
			case "value":
				return value;
			default:
				return super.getAttrUL4(key);
		}
	}
}
