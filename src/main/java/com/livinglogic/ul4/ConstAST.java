/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.io.IOException;
import java.util.Set;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class ConstAST extends AST
{
	protected Object value;

	public ConstAST(Location location, int start, int end, Object value)
	{
		super(location, start, end);
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

	protected static Set<String> attributes = makeExtendedSet(AST.attributes, "value");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("value".equals(key))
			return value;
		else
			return super.getItemStringUL4(key);
	}
}
