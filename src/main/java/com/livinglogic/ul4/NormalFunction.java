/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public abstract class NormalFunction implements Function
{
	public abstract String getName();

	private ArgumentDescriptions argumentDescriptions = null;

	protected void makeArgumentDescriptions(ArgumentDescriptions argumentDescriptions)
	{
	}

	private ArgumentDescriptions getArgumentDescriptions()
	{
		if (argumentDescriptions == null)
		{
			argumentDescriptions = new ArgumentDescriptions(getName());
			makeArgumentDescriptions(argumentDescriptions);
		}
		return argumentDescriptions;
	}

	public Object evaluate(EvaluationContext context, Object[] args, Map<String, Object> kwargs)
	{
		return evaluate(context, getArgumentDescriptions().makeArgumentArray(context, args, kwargs));
	}

	public abstract Object evaluate(EvaluationContext context, Object[] args);
}
