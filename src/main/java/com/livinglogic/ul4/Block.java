/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;

abstract class Block extends AST
{
	protected List<AST> content = new LinkedList<AST>();
	protected Location endlocation = null;

	public Block(Location location)
	{
		super(location);
	}

	public void append(AST item)
	{
		content.add(item);
	}

	public void finish(InterpretedTemplate template, Location endlocation)
	{
		this.endlocation = endlocation;
	}

	abstract public boolean handleLoopControl(String name);

	public Object evaluate(EvaluationContext context) throws IOException
	{
		for (AST item : content)
			item.decoratedEvaluate(context);
		return null;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(content);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		content = (List<AST>)decoder.load();
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("endlocation", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)object).endlocation;}});
			v.put("content", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)object).content;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
