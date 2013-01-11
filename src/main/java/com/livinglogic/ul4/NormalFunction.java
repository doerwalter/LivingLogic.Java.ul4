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

	private Signature signature = null;

	protected void makeSignature(Signature signature)
	{
	}

	private Signature getSignature()
	{
		if (signature == null)
		{
			signature = new Signature(getName());
			makeSignature(signature);
		}
		return signature;
	}

	public Object evaluate(EvaluationContext context, Object[] args, Map<String, Object> kwargs)
	{
		return evaluate(context, getSignature().makeArgumentArray(context, args, kwargs));
	}

	public abstract Object evaluate(EvaluationContext context, Object[] args);
}
