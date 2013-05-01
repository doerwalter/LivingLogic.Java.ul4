/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.livinglogic.utils.SetUtils.makeSet;
import static com.livinglogic.utils.SetUtils.union;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public abstract class ChangeVar extends AST
{
	protected String varname;
	protected AST value;

	public ChangeVar(Location location, int start, int end, String varname, AST value)
	{
		super(location, start, end);
		this.varname = varname;
		this.value = value;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(varname);
		encoder.dump(value);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		varname = (String)decoder.load();
		value = (AST)decoder.load();
	}

	protected static Set<String> attributes = union(AST.attributes, makeSet("varname", "value"));

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("varname".equals(key))
			return varname;
		else if ("value".equals(key))
			return value;
		else
			return super.getItemStringUL4(key);
	}
}
