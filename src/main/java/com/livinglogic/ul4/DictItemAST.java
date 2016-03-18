/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Map;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class DictItemAST extends DictItemASTBase
{
	protected AST key;
	protected AST value;

	public DictItemAST(Tag tag, int start, int end, AST key, AST value)
	{
		super(tag, start, end);
		this.key = key;
		this.value = value;
	}

	public String getType()
	{
		return "dictitem";
	}

	public void evaluateDict(EvaluationContext context, Map result)
	{
		result.put(key.decoratedEvaluate(context), value.decoratedEvaluate(context));
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(key);
		encoder.dump(value);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		key = (AST)decoder.load();
		value = (AST)decoder.load();
	}
}
