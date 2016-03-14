/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class SeqItemAST extends SeqItemASTBase
{
	protected AST value;

	public SeqItemAST(Tag tag, int start, int end, AST value)
	{
		super(tag, start, end);
		this.value = value;
	}

	public String getType()
	{
		return "seqitem";
	}

	public void evaluateList(EvaluationContext context, List result)
	{
		result.add(value.decoratedEvaluate(context));
	}

	public void evaluateSet(EvaluationContext context, Set result)
	{
		result.add(value.decoratedEvaluate(context));
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
