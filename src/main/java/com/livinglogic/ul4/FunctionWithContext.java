/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public abstract class FunctionWithContext implements UL4CallWithContext, UL4Name
{
	public abstract String nameUL4();

	private Signature signature = null;

	protected Signature makeSignature()
	{
		return new Signature(nameUL4());
	}

	private Signature getSignature()
	{
		if (signature == null)
			signature = makeSignature();
		return signature;
	}

	public Object callUL4(EvaluationContext context, Object[] args, Map<String, Object> kwargs)
	{
		return evaluate(context, getSignature().makeArgumentArray(args, kwargs));
	}

	public abstract Object evaluate(EvaluationContext context, Object[] args);
}
