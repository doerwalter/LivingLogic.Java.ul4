/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class UnpackSeqItemAST extends SeqItemASTBase
{
	protected AST value;

	public UnpackSeqItemAST(Tag tag, int start, int end, AST value)
	{
		super(tag, start, end);
		this.value = value;
	}

	public String getType()
	{
		return "unpackseqitem";
	}

	public void evaluateList(EvaluationContext context, List result)
	{
		for (Iterator iter = Utils.iterator(value.decoratedEvaluate(context)); iter.hasNext();)
			result.add(iter.next());
	}

	public void evaluateSet(EvaluationContext context, Set result)
	{
		for (Iterator iter = Utils.iterator(value.decoratedEvaluate(context)); iter.hasNext();)
			result.add(iter.next());
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(value);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		value = (AST)decoder.load();
	}
}
