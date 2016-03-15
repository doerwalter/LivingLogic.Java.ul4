/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class PositionalArgumentAST extends ArgumentASTBase
{
	AST value;

	public PositionalArgumentAST(Tag tag, int start, int end, AST value)
	{
		super(tag, start, end);
		this.value = value;
	}

	public String getType()
	{
		return "posarg";
	}

	public void addToCall(CallRenderAST call)
	{
		for (ArgumentASTBase argument : call.arguments)
		{
			if (argument instanceof KeywordArgumentAST)
				throw new SyntaxException("positional argument follows keyword argument");
			else if (argument instanceof UnpackDictArgumentAST)
				throw new SyntaxException("positional argument follows keyword argument unpacking");
		}
		call.addArgument(this);
	}

	public void evaluateCall(EvaluationContext context, List<Object> arguments, Map<String, Object> keywordArguments)
	{
		arguments.add(value.decoratedEvaluate(context));
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
