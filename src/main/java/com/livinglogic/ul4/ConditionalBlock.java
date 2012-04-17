/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

abstract class ConditionalBlock extends Block
{
	abstract public boolean hasToBeExecuted(EvaluationContext context) throws IOException;
}
