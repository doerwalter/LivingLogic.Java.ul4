/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

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

	public void addTo(EvaluationContext context, Map dict)
	{
		dict.put(key.evaluate(context), value.evaluate(context));
	}
}
