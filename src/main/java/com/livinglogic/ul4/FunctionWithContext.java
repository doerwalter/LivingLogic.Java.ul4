/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public abstract class FunctionWithContext implements UL4CallWithContext, UL4Name, UL4Type, UL4Repr
{
	public abstract String nameUL4();

	public String typeUL4()
	{
		return "function";
	}

	private static final Signature signature = new Signature(); // default signature: no arguments

	protected Signature getSignature()
	{
		return signature;
	}

	public Object callUL4(EvaluationContext context, List<Object> args, Map<String, Object> kwargs)
	{
		return evaluate(context, getSignature().makeArgumentList(this, args, kwargs));
	}

	public abstract Object evaluate(EvaluationContext context, List<Object> args);

	public String reprUL4()
	{
		StringBuilder builder = new StringBuilder();

		builder.append("<function ");
		builder.append(nameUL4());
		builder.append(">");

		return builder.toString();
	}
}
