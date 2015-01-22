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

public class SetVarAST extends CodeAST
{
	/**
	 * This is either a string or a list of strings/lists
	 */
	protected Object lvalue;
	protected AST value;

	public SetVarAST(Tag tag, int start, int end, Object lvalue, AST value)
	{
		super(tag, start, end);
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

	protected static Set<String> attributes = makeExtendedSet(AST.attributes, "lvalue", "value");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("lvalue".equals(key))
			return lvalue;
		else if ("value".equals(key))
			return value;
		else
			return super.getItemStringUL4(key);
	}
}
