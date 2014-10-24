/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Arrays;
import java.util.Map;

public class DictItem
{
	protected AST key;
	protected AST value;

	public DictItem(AST key, AST value)
	{
		this.key = key;
		this.value = value;
	}

	public void addTo(EvaluationContext context, Map dict)
	{
		dict.put(key.decoratedEvaluate(context), value.decoratedEvaluate(context));
	}

	public Object object4UL4ON()
	{
		return Arrays.asList(key, value);
	}
}
