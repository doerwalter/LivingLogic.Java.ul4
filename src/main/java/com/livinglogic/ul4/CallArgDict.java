/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.io.IOException;

public class CallArgDict extends CallArg
{
	protected AST dict;

	public CallArgDict(AST dict)
	{
		this.dict = dict;
	}

	public String toString()
	{
		return "**" + dict.toString();
	}

	public void addTo(EvaluationContext context, Map dict) throws IOException
	{
		dict.putAll((Map)this.dict.evaluate(context));
	}
}
