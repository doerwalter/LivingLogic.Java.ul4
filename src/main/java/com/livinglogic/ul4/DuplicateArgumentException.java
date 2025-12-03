/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Exception when an parameter has been specified both as a positional argument and as a keyword argument.
**/
public class DuplicateArgumentException extends ArgumentException
{
	public DuplicateArgumentException(String name, String parameterName, int parameterPosition)
	{
		super((name != null ? name + "() " : "") + "argument " + FunctionRepr.call(parameterName) + (parameterPosition >= 0 ? " (position " + parameterPosition + ")": "") + " specified multiple times");
	}

	public DuplicateArgumentException(String parameterName)
	{
		this(null, parameterName, -1);
	}

	public DuplicateArgumentException(String name, String parameterName)
	{
		this(name, parameterName, -1);
	}

	public DuplicateArgumentException(UL4Name object, String parameterName)
	{
		this(object.getFullNameUL4(), parameterName);
	}

	public DuplicateArgumentException(Object object, String parameterName)
	{
		this(object instanceof UL4Name ? ((UL4Name)object).getFullNameUL4() : Utils.objectType(object), parameterName);
	}

	public DuplicateArgumentException(String name, ParameterDescription parameter)
	{
		this(name, parameter.getName(), parameter.getPosition());
	}

	public DuplicateArgumentException(UL4Name object, ParameterDescription parameter)
	{
		this(object.getFullNameUL4(), parameter);
	}
}
