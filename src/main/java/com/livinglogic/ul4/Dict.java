/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class Dict extends AST
{
	protected LinkedList<DictItem> items = new LinkedList<DictItem>();

	public Dict(Location location)
	{
		super(location);
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
		StringBuilder buffer = new StringBuilder();
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

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		LinkedList itemList = new LinkedList();
		for (DictItem item : items)
			itemList.add(item.object4UL4ON());
		encoder.dump(itemList);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		java.util.List<java.util.List<Object>> itemList = (java.util.List<java.util.List<Object>>)decoder.load();
		items = new LinkedList<DictItem>();
		for (java.util.List item : itemList)
		{
			if (item.size() == 2)
				items.add(new DictItemKeyValue((AST)item.get(0), (AST)item.get(1)));
			else
				items.add(new DictItemDict((AST)item.get(0)));
		}
	}
}
