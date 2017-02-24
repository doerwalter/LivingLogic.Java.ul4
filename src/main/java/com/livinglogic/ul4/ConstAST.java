/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
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
	protected Object value;

	public ConstAST(Tag tag, Slice pos, Object value)
	{
		super(tag, pos);
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

	public Object evaluate(EvaluationContext context)
	{
		return value;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(value);
	}

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

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "value":
				return value;
			default:
				return super.getItemStringUL4(key);
		}
	}
}
