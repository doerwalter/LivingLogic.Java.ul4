/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;

import static com.livinglogic.ul4.Utils.getInnerException;


/**
A {@code GenericType} for exceptions, providing specific handling for their attributes, like {@code context}.
**/
public class ExceptionType extends GenericType
{
	public ExceptionType(Class clazz)
	{
		super(clazz);
	}

	@Override
	public String strInstance(EvaluationContext context, Object instance)
	{
		String message = ((Throwable)instance).getLocalizedMessage();
		return message != null ? message : "";
	}

	@Override
	public Set<String> dirInstance(EvaluationContext context, Object instance)
	{
		Set<String> result = super.dirInstance(context, instance);
		result.add("context");
		return result;
	}

	@Override
	public Object getAttr(EvaluationContext context, Object object, String key)
	{
		switch (key)
		{
			case "context":
				return getInnerException((Throwable)object);
			default:
				return super.getAttr(context, object, key);
		}
	}
}
