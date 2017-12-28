/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class ConditionalBlocks extends BlockAST
{
	public ConditionalBlocks(Tag tag, Slice pos)
	{
		super(tag, pos);
	}

	public ConditionalBlocks(Tag tag, Slice pos, IfBlockAST block)
	{
		super(tag, pos);
		startNewBlock(block);
	}

	@Override
	public String getType()
	{
		return "condblock";
	}

	@Override
	public IndentAST popTrailingIndent()
	{
		if (content.size() > 0)
			return ((BlockLike)content.get(content.size()-1)).popTrailingIndent();
		else
			return null;
	}

	@Override
	public void append(AST item)
	{
		((ConditionalBlock)content.get(content.size()-1)).append(item);
	}

	@Override
	public void finish(Tag endtag)
	{
		String type = endtag.getCodeText().trim();
		if (type != null && type.length() != 0 && !type.equals("if"))
			throw new BlockException("if ended by end" + type);
		super.finish(endtag);
		((BlockAST)content.get(content.size()-1)).endtag = endtag;
	}

	@Override
	public boolean handleLoopControl(String name)
	{
		return false;
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
