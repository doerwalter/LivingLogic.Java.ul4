/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class ConditionalBlocks extends BlockAST
{
	public ConditionalBlocks(Location location, int start, int end)
	{
		super(location, start, end);
	}

	public ConditionalBlocks(Location location, int start, int end, IfBlockAST block)
	{
		super(location, start, end);
		startNewBlock(block);
	}

	public String getType()
	{
		return "condblock";
	}

	public boolean handleLoopControl(String name)
	{
		return false;
	}

	public void append(AST item)
	{
		((ConditionalBlock)content.get(content.size()-1)).append(item);
	}

	public void startNewBlock(ConditionalBlock item)
	{
		if (item instanceof IfBlockAST)
		{
			if (content.size() != 0)
				throw new BlockException("if must be first in if/elif/else chain");
		}
		else if (item instanceof ElIfBlockAST)
		{
			if (content.size() == 0)
				throw new BlockException("elif can't be first in if/elif/else chain");
			AST last = content.get(content.size()-1);
			if (last instanceof ElseBlockAST)
				throw new BlockException("elif can't follow else in if/elif/else chain");
		}
		else if (item instanceof ElseBlockAST)
		{
			if (content.size() == 0)
				throw new BlockException("else can't be first in if/elif/else chain");
			AST last = (BlockAST)content.get(content.size()-1);
			if (last instanceof ElseBlockAST)
				throw new BlockException("duplicate else in if/elif/else chain");
		}
		content.add(item);
	}

	public void finish(Location endlocation)
	{
		super.finish(endlocation);
		((BlockAST)content.get(content.size()-1)).endlocation = endlocation;
		String type = endlocation.getCode().trim();
		if (type != null && type.length() != 0 && !type.equals("if"))
			throw new BlockException("if ended by end" + type);
	}

	public Object evaluate(EvaluationContext context)
	{
		for (AST item : content)
		{
			if (((ConditionalBlock)item).hasToBeExecuted(context))
				return item.decoratedEvaluate(context);
		}
		return null;
	}
}
