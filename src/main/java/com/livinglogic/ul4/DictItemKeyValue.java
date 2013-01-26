/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class DictItemKeyValue extends DictItem
{
	protected AST key;
	protected AST value;

	public DictItemKeyValue(AST key, AST value)
	{
		this.key = key;
		this.value = value;
	}

	public String toString(InterpretedCode code, int indent)
	{
		return key.toString() + ": " + value.toString(code, indent);
	}

	public void addTo(EvaluationContext context, Map dict) throws IOException
	{
		dict.put(key.decoratedEvaluate(context), value.decoratedEvaluate(context));
	}

	public Object object4UL4ON()
	{
		return Arrays.asList(key, value);
	}
}
