/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.io.IOException;

public class DictItemDict extends DictItem
{
	protected AST dict;

	public DictItemDict(AST dict)
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
