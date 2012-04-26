/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;
import java.util.ArrayList;
import java.io.IOException;

public class List extends AST
{
	protected LinkedList<AST> items = new LinkedList<AST>();

	public List(Location location)
	{
		super(location);
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
}
