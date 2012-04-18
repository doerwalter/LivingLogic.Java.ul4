/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public abstract class AST
{
	abstract public Object evaluate(EvaluationContext context) throws IOException;

	abstract public String getType();

	public String toString()
	{
		return toString(0);
	}

	abstract public String toString(int indent);
}
