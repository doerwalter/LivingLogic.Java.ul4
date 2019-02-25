/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

abstract class ConditionalBlock extends BlockAST
{
	public ConditionalBlock(InterpretedTemplate template, Slice pos)
	{
		super(template, pos);
	}

	abstract public boolean hasToBeExecuted(EvaluationContext context);

	public boolean handleLoopControl(String name)
	{
		return false;
	}
}
