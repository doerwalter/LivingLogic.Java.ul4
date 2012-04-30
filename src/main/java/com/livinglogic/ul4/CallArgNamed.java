/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.Arrays;
import java.io.IOException;

public class CallArgNamed extends CallArg
{
	protected String name;
	protected AST value;

	public CallArgNamed(String name, AST value)
	{
		this.name = name;
		this.value = value;
	}

	public String toString()
	{
		return name + "=" + value.toString();
	}

	public void addTo(EvaluationContext context, Map dict) throws IOException
	{
		dict.put(name, value.decoratedEvaluate(context));
	}

	public Object object4UL4ON()
	{
		return Arrays.asList(name, value);
	}
}
