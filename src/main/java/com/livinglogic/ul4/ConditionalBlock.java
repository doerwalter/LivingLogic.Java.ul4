/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

abstract class ConditionalBlock extends Block
{
	public ConditionalBlock(Location location, int start, int end)
	{
		super(location, start, end);
	}

	abstract public boolean hasToBeExecuted(EvaluationContext context) throws IOException;

	public boolean handleLoopControl(String name)
	{
		return false;
	}
}
