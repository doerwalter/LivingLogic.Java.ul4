/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
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
		BoundArguments arguments = new BoundArguments(getSignature(), this, args, kwargs);
		Object result = null;
		try
		{
			result = evaluate(context, arguments);
		}
		finally
		{
			// We can clean up here, as the function implementation shouldn't be a "closure",
			// i.e. it should not return the variables map or anything that needs the map
			arguments.cleanup();
		}
		return result;
	}

	public abstract Object evaluate(EvaluationContext context, BoundArguments args);

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<function ");
		formatter.append(nameUL4());
		formatter.append(">");
	}
}
