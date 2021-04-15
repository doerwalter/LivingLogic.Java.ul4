/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ConditionalBlocksAST extends BlockAST
{
	protected static class Type extends BlockAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ConditionalBlocksAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.condblock";
		}

		@Override
		public String getDoc()
		{
			return "An if/elif/else block.";
		}

		@Override
		public ConditionalBlocksAST create(String id)
		{
			return new ConditionalBlocksAST(null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ConditionalBlocksAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ConditionalBlocksAST(Template template, Slice startPos, Slice stopPos)
	{
		super(template, startPos, stopPos);
	}

	public ConditionalBlocksAST(Template template, Slice startPos, Slice stopPos, IfBlockAST block)
	{
		super(template, startPos, stopPos);
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

	private BlockAST getLastBlock()
	{
		if (content.size() != 0)
			return (BlockAST)content.get(content.size()-1);
		else
			return null;
	}

	@Override
	public void finish(Tag endtag)
	{
		String type = endtag.getCode().trim();
		if (type != null && type.length() != 0 && !type.equals("if"))
			throw new BlockException("if ended by end" + type);
		super.finish(endtag);
		BlockAST lastBlock = getLastBlock();
		if (lastBlock != null)
		{
			int stop = endtag.getStartPos().getStart();
			lastBlock.setStopPos(stop, stop);
		}
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
			BlockAST lastBlock = getLastBlock();
			if (lastBlock instanceof ElseBlockAST)
				throw new BlockException("elif can't follow else in if/elif/else chain");
		}
		else if (item instanceof ElseBlockAST)
		{
			if (content.size() == 0)
				throw new BlockException("else can't be first in if/elif/else chain");
			BlockAST lastBlock = getLastBlock();
			if (lastBlock instanceof ElseBlockAST)
				throw new BlockException("duplicate else in if/elif/else chain");
		}
		if (content.size() != 0)
		{
			BlockAST lastBlock = getLastBlock();
			int start = item.getStartPos().getStart();
			lastBlock.setStopPos(start, start);
		}
		content.add(item);
	}

	@Override
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
