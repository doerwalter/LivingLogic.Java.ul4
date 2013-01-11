/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.io.IOException;

public abstract class NormalMethod implements Method
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

	public Object evaluate(EvaluationContext context, Object obj, Object[] args, Map<String, Object> kwargs) throws IOException
	{
		return evaluate(context, obj, getSignature().makeArgumentArray(context, args, kwargs));
	}

	public abstract Object evaluate(EvaluationContext context, Object obj, Object[] args) throws IOException;
}
