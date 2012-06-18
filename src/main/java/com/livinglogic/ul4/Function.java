/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public interface Function
{
	public Object evaluate(EvaluationContext context, Object... args);

	public String getName();
}
