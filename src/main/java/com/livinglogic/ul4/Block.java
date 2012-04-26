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

abstract class Block extends AST
{
	protected LinkedList<AST> content = new LinkedList<AST>();

	public Block(Location location)
	{
		super(location);
	}

	public void append(AST item)
	{
		content.add(item);
	}

	public void finish(InterpretedTemplate template, Location startLocation, Location endLocation)
	{
	}

	abstract public boolean handleLoopControl(String name);

	public Object evaluate(EvaluationContext context) throws IOException
	{
		for (AST item : content)
			item.decoratedEvaluate(context);
		return null;
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("content", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)object).content;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
