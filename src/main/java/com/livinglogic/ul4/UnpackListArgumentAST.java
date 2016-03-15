/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class UnpackListArgumentAST extends ArgumentASTBase
{
	AST value;

	public UnpackListArgumentAST(Tag tag, int start, int end, AST value)
	{
		super(tag, start, end);
		this.value = value;
	}

	public String getType()
	{
		return "unpacklistarg";
	}

	public void addToCall(CallRenderAST call)
	{
		for (ArgumentASTBase argument : call.arguments)
		{
			if (argument instanceof UnpackDictArgumentAST)
				throw new SyntaxException("positional argument follows keyword argument unpacking");
		}
		call.addArgument(this);
	}

	public void evaluateCall(EvaluationContext context, List<Object> arguments, Map<String, Object> keywordArguments)
	{
		for (Iterator iter = Utils.iterator(value.decoratedEvaluate(context)); iter.hasNext();)
			arguments.add(iter.next());
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
