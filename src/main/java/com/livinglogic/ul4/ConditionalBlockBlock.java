/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class ConditionalBlockBlock extends Block
{
	public ConditionalBlockBlock()
	{
		super();
	}

	public ConditionalBlockBlock(If block)
	{
		super();
		startNewBlock(block);
	}

	public String getType()
	{
		return "if";
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();
		for (AST item : content)
			buffer.append(item.toString(indent));
		return buffer.toString();
	}

	public void append(AST item)
	{
		((ConditionalBlock)content.get(content.size()-1)).append(item);
	}

	public void startNewBlock(ConditionalBlock item)
	{
		if (item instanceof If)
		{
			if (content.size() != 0)
				throw new BlockException("if must be first in if/elif/else chain");
		}
		else if (item instanceof ElIf)
		{
			if (content.size() == 0)
				throw new BlockException("elif can't be first in if/elif/else chain");
			AST last = content.get(content.size()-1);
			if (last instanceof Else)
				throw new BlockException("else can't follow elif in if/elif/else chain");
		}
		else if (item instanceof Else)
		{
			if (content.size() == 0)
				throw new BlockException("else can't be first in if/elif/else chain");
			AST last = (Block)content.get(content.size()-1);
			if (last instanceof Else)
				throw new BlockException("duplicate else in if/elif/else chain");
		}
		content.add(item);
	}

	public void finish(String name)
	{
		if (name != null && name.length() != 0 && !name.equals("if"))
			throw new BlockException("if ended by end" + name);
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		for (AST item : content)
		{
			if (((ConditionalBlock)item).hasToBeExecuted(context))
				return item.evaluate(context);
		}
		return null;
	}
}
