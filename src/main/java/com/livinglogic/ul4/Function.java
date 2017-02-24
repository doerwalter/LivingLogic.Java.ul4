/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

/**
 * <p>A {@code Function} object implements a function that can be called from
 * UL4.
 *
 * <p>{@code Function} is abstract. Subclasses must implement {@link #evaluate}.
 * Also when the function requires arguments {@link #getSignature} must be
 * overwritten.</p>
 */
public abstract class Function implements UL4Call, UL4Name, UL4Type, UL4Repr
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

	public Object callUL4(List<Object> args, Map<String, Object> kwargs)
	{
		// We can clean up here, as the function implementation shouldn't be a "closure",
		// i.e. it should not return the variables map or anything that needs the map
		try (BoundArguments arguments = new BoundArguments(getSignature(), this, args, kwargs))
		{
			return evaluate(arguments);
		}
	}

	/**
	 * Evaluate this function and return the result.
	 *
	 * @param args The arguments for the call.
	 * @return The result of the function call.
	 */
	public abstract Object evaluate(BoundArguments args);

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<function ");
		formatter.append(nameUL4());
		formatter.append(">");
	}
}
