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
			return "A constant.";
		}

		@Override
		public ConstAST create(String id)
		{
			return new ConstAST(null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ConstAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected Object value;

	public ConstAST(Template template, Slice startPos, Object value)
	{
		super(template, startPos);
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

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "value":
				return value;
			default:
				return super.getAttrUL4(key);
		}
	}
}
