/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class ConditionalBlockBlock extends Block
{
	public ConditionalBlockBlock(Location location)
	{
		super(location);
	}

	public ConditionalBlockBlock(Location location, If block)
	{
		super(location);
		startNewBlock(block);
	}

	public String getType()
	{
		return "ieie";
	}

	public String toString(InterpretedCode code, int indent)
	{
		StringBuilder buffer = new StringBuilder();
		for (AST item : content)
			buffer.append(item.toString(code, indent));
		return buffer.toString();
	}

	public boolean handleLoopControl(String name)
	{
		return false;
	}

	public void append(Tag item)
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
				throw new BlockException("elif can't follow else in if/elif/else chain");
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

	public void finish(Location endlocation)
	{
		super.finish(endlocation);
		((Block)content.get(content.size()-1)).endlocation = endlocation;
		String type = endlocation.getCode().trim();
		if (type != null && type.length() != 0 && !type.equals("if"))
			throw new BlockException("if ended by end" + type);
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		for (Tag item : content)
		{
			if (((ConditionalBlock)item).hasToBeExecuted(context))
				return item.decoratedEvaluate(context);
		}
		return null;
	}
}
