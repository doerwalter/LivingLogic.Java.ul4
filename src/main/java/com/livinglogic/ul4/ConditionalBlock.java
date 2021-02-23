/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

abstract class ConditionalBlock extends BlockAST
{
	public ConditionalBlock(Template template, Slice startPos, Slice stopPos)
	{
		super(template, startPos, stopPos);
	}

	abstract public boolean hasToBeExecuted(EvaluationContext context);

	public boolean handleLoopControl(String name)
	{
		return false;
	}
}
