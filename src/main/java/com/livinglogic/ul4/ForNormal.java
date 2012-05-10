/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;

import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;

public class ForNormal extends For
{
	protected String itername;

	public ForNormal(Location location, AST container, String itername)
	{
		super(location, container);
		this.itername = itername;
	}

	public String getType()
	{
		return "for";
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("for ");
		buffer.append(itername);
		buffer.append(" in ");
		buffer.append(container.toString(indent));
		buffer.append("\n");
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("{\n");
		++indent;
		for (AST item : content)
			buffer.append(item.toString(indent));
		--indent;
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("}\n");
		return buffer.toString();
	}

	public static void unpackLoopVariable(EvaluationContext context, Object item, String varname)
	{
		context.put(varname, item);
	}

	protected void unpackLoopVariable(EvaluationContext context, Object item)
	{
		unpackLoopVariable(context, item, itername);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(itername);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		itername = (String)decoder.load();
	}
}
