/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

/**
<p>A {@code Function} object implements a function that can be called from
UL4.

<p>{@code Function} is abstract. Subclasses must implement {@link #evaluate}.
Also when the function requires arguments {@link #getSignature} must be
overwritten.</p>
**/
public abstract class Function implements UL4Instance, UL4Call, UL4Name, UL4Repr
{
	protected static class Type extends AbstractInstanceType
	{
		@Override
		public String getNameUL4()
		{
			return "function";
		}

		@Override
		public String getDoc()
		{
			return "A callable object (i.e. a function etc.).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof Function;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected Function()
	{
	}

	public String getModuleName()
	{
		return null;
	}

	public abstract String getNameUL4();

	@Override
	public String getFullNameUL4()
	{
		String moduleName = getModuleName();
		if (moduleName != null)
			return moduleName + "." + getNameUL4();
		return getNameUL4();
	}

	/**
	<p>Return a signature for this function.</p>

	<p>The default returns a signature without any arguments.</p>
	**/
	protected Signature getSignature()
	{
		return Signature.noParameters;
	}

	@Override
	public Object callUL4(EvaluationContext context, List<Object> args, Map<String, Object> kwargs)
	{
		// We can clean up here, as the function implementation shouldn't be a "closure",
		// i.e. it should not return the variables map or anything that needs the map
		try (BoundArguments arguments = new BoundArguments(getSignature(), this, args, kwargs))
		{
			return evaluate(context, arguments);
		}
	}

	/**
	Evaluate this function and return the result.

	@param args The arguments for the call.
	@return The result of the function call.
	**/
	public abstract Object evaluate(EvaluationContext context, BoundArguments args);

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<function ");
		formatter.append(getFullNameUL4());
		formatter.append(">");
	}
}
