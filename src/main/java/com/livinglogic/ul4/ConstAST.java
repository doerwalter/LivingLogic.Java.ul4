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

public class ConstAST extends CodeAST
{
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ConstAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.const";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a constant value.";
		}

		@Override
		public ConstAST create(String id)
		{
			return new ConstAST(null, -1, -1, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ConstAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected Object value;

	public ConstAST(Template template, int posStart, int posStop, Object value)
	{
		super(template, posStart, posStop);
		this.value = value;
	}

	public String getType()
	{
		return "const";
	}

	public String toString(int indent)
	{
		return FunctionRepr.call(value);
	}

	@Override
	public Object evaluate(EvaluationContext context)
	{
		return value;
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(value);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		value = decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "value");

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
			case "value":
				return value;
			default:
				return super.getAttrUL4(context, key);
		}
	}
}
