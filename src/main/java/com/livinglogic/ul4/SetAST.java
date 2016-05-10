/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;
import java.util.HashSet;
import java.util.LinkedList;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class SetAST extends CodeAST
{
	protected List<SeqItemASTBase> items = new LinkedList<SeqItemASTBase>();

	public SetAST(Tag tag, Slice pos)
	{
		super(tag, pos);
	}

	public void append(SeqItemASTBase item)
	{
		items.add(item);
	}

	public String getType()
	{
		return "set";
	}

	public Object evaluate(EvaluationContext context)
	{
		HashSet result = new HashSet(items.size());

		for (SeqItemASTBase item : items)
			item.decoratedEvaluateSet(context, result);
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
		items = (List<SeqItemASTBase>)decoder.load();
	}
}
