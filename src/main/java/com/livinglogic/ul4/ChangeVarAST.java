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

public abstract class ChangeVarAST extends CodeAST
{
	protected LValue lvalue;
	protected AST value;

	public ChangeVarAST(Tag tag, int start, int end, LValue lvalue, AST value)
	{
		super(tag, start, end);
		this.lvalue = lvalue;
		this.value = value;
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
		lvalue = (LValue)decoder.load();
		value = (AST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "lvalue", "value");

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
