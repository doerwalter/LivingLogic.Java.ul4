/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

public class Dict extends AST
{
	protected LinkedList<DictItem> items = new LinkedList<DictItem>();

	public Dict()
	{
	}

	public void append(AST key, AST value)
	{
		items.add(new DictItemKeyValue(key, value));
	}

	public void append(AST dict)
	{
		items.add(new DictItemDict(dict));
	}

	public void append(DictItem item)
	{
		items.add(item);
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");

		boolean first = true;
		for (DictItem item : items)
		{
			if (first)
				first = false;
			else
				buffer.append(", ");
			buffer.append(item);
		}
		buffer.append("}");
		return buffer.toString();
	}

	public String getType()
	{
		return "dict";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Map result = new HashMap(items.size());

		for (DictItem item : items)
			item.addTo(context, result);
		return result;
	}
}
