/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class ListAST extends AST
{
	protected List<AST> items = new LinkedList<AST>();

	public ListAST(Location location, int start, int end)
	{
		super(location, start, end);
	}

	public void append(AST item)
	{
		items.add(item);
	}

	public String getType()
	{
		return "list";
	}

	public Object evaluate(EvaluationContext context)
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
		items = (List<AST>)decoder.load();
	}
}