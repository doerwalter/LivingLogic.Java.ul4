/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class List extends AST
{
	protected java.util.List<AST> items = new LinkedList<AST>();

	public List()
	{
		super();
	}

	public void append(AST item)
	{
		items.add(item);
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");

		boolean first = true;
		for (AST item : items)
		{
			if (first)
				first = false;
			else
				buffer.append(", ");
			buffer.append(item.toString(indent));
		}
		buffer.append("]");
		return buffer.toString();
	}

	public String getType()
	{
		return "list";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		ArrayList result = new ArrayList(items.size());

		for (AST item : items)
			result.add(item.decoratedEvaluate(context));
		return result;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(items);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		items = (java.util.List<AST>)decoder.load();
	}
}
