/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

/**
 * <p>A {@code FunctionWithContext} object implements a function that can be
 * called from UL4. It is similar to {@link Function} except that
 * {@link #evaluate} gets passed the context in addition to the arguments.</p>
 *
 * <p>{@code FunctionWithContext} is abstract. Subclasses must implement
 * {@link #evaluate}. Also when the function requires arguments
 * {@link #getSignature} must be overwritten.</p>
 */
public abstract class FunctionWithContext implements UL4CallWithContext, UL4Name, UL4Type, UL4Repr
{
	public abstract String nameUL4();

	public String typeUL4()
	{
		return "function";
	}

	private static final Signature signature = new Signature(); // default signature: no arguments

	/**
	 * <p>Return a signature for this function.</p>
	 *
	 * <p>The default returns a signature without any arguments.</p>
	 */
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
			arguments.close();
		}
		return result;
	}

	/**
	 * Evaluate this function and return the result.
	 *
	 * @param context The context of the call.
	 * @param args The arguments for the call.
	 * @return The result of the function call.
	 */
	public abstract Object evaluate(EvaluationContext context, BoundArguments args);

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<function ");
		formatter.append(nameUL4());
		formatter.append(">");
	}
}
