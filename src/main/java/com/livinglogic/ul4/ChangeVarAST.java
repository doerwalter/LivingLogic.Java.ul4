/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
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

	public ChangeVarAST(InterpretedTemplate template, Slice pos, LValue lvalue, AST value)
	{
		super(template, pos);
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
