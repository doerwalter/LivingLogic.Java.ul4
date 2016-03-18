/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class KeywordArgumentAST extends ArgumentASTBase
{
	String name;
	AST value;

	public KeywordArgumentAST(Tag tag, int start, int end, String name, AST value)
	{
		super(tag, start, end);
		this.name = name;
		this.value = value;
	}

	public String getType()
	{
		return "keywordarg";
	}

	public void addToCall(CallRenderAST call)
	{
		call.addArgument(this);
	}

	public void evaluateCall(EvaluationContext context, List<Object> arguments, Map<String, Object> keywordArguments)
	{
		Object oldValue = keywordArguments.get(name);
		if (oldValue != null && keywordArguments.containsKey(name))
			throw new DuplicateArgumentException(name);

		keywordArguments.put(name, value.decoratedEvaluate(context));
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(name);
		encoder.dump(value);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		name = (String)decoder.load();
		value = (AST)decoder.load();
	}
}
