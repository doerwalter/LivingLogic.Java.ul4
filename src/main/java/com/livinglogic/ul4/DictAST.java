/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class DictAST extends AST
{
	protected LinkedList<DictItem> items = new LinkedList<DictItem>();

	public DictAST(Location location, int start, int end)
	{
		super(location, start, end);
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

	public String getType()
	{
		return "dict";
	}

	public Object evaluate(EvaluationContext context)
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
		List<List<Object>> itemList = (List<List<Object>>)decoder.load();
		items = new LinkedList<DictItem>();
		for (List item : itemList)
		{
			if (item.size() == 2)
				items.add(new DictItemKeyValue((AST)item.get(0), (AST)item.get(1)));
			else
				items.add(new DictItemDict((AST)item.get(0)));
		}
	}
}