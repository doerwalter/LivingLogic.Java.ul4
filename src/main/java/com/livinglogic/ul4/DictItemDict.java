/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class DictItemDict extends DictItem
{
	protected AST dict;

	public DictItemDict(AST dict)
	{
		this.dict = dict;
	}

	public String toString(InterpretedCode code, int indent)
	{
		return "**" + dict.toString(code, indent);
	}

	public void addTo(EvaluationContext context, Map dict) throws IOException
	{
		dict.putAll((Map)this.dict.decoratedEvaluate(context));
	}

	public Object object4UL4ON()
	{
		return Arrays.asList(dict);
	}
}
