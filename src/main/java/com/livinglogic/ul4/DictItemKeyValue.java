/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.io.IOException;

public class DictItemKeyValue extends DictItem
{
	protected AST key;
	protected AST value;

	public DictItemKeyValue(AST key, AST value)
	{
		this.key = key;
		this.value = value;
	}

	public String toString()
	{
		return key.toString() + ": " + value.toString();
	}

	public void addTo(EvaluationContext context, Map dict) throws IOException
	{
		dict.put(key.evaluate(context), value.evaluate(context));
	}
}
