/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


/**
Abstract base class for conditional blocks like {@code if}, {@code elif}, and {@code else}.
**/
abstract class ConditionalBlock extends BlockAST
{
	public ConditionalBlock(Template template, int startPosStart, int startPosStop, int stopPosStart, int stopPosStop)
	{
		super(template, startPosStart, startPosStop, stopPosStart, stopPosStop);
	}

	abstract public boolean hasToBeExecuted(EvaluationContext context);

	public boolean handleLoopControl(String name)
	{
		return false;
	}
}
