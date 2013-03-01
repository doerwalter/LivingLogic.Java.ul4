/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public abstract class NormalMethod implements Method, UL4Name
{
	public abstract String nameUL4();

	private Signature signature = null;

	protected void makeSignature(Signature signature)
	{
	}

	private Signature getSignature()
	{
		if (signature == null)
		{
			signature = new Signature(nameUL4());
			makeSignature(signature);
		}
		return signature;
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args, Map<String, Object> kwargs)
	{
		return evaluate(context, obj, getSignature().makeArgumentArray(args, kwargs));
	}

	public abstract Object evaluate(EvaluationContext context, Object obj, Object[] args);
}
