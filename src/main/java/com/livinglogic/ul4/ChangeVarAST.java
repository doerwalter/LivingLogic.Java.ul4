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

public abstract class ChangeVarAST extends CodeAST
{
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ChangeVarAST";
		}

		@Override
		public String getDoc()
		{
			return "Base class for all AST nodes that are assignment operators, i.e. that\nset or modify a variable/attribute or item.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ChangeVarAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected LValue lvalue;
	protected AST value;

	public ChangeVarAST(Template template, int posStart, int posStop, LValue lvalue, AST value)
	{
		super(template, posStart, posStop);
		this.lvalue = lvalue;
		this.value = value;
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(lvalue);
		encoder.dump(value);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		lvalue = (LValue)decoder.load();
		value = (AST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "lvalue", "value");

	@Override
	public Set<String> dirUL4(EvaluationContext context)
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
	{
		switch (key)
		{
			case "lvalue":
				return lvalue;
			case "value":
				return value;
			default:
				return super.getAttrUL4(context, key);
		}
	}
}
