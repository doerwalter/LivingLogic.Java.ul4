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
		Signature signature = getSignature();
		List<Object> arguments = signature.makeArgumentList(this, args, kwargs);
		Object result = evaluate(context, arguments);
		signature.cleanup(arguments);
		return result;
	}

	public abstract Object evaluate(EvaluationContext context, List<Object> args);

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<function ");
		formatter.append(nameUL4());
		formatter.append(">");
	}
}
