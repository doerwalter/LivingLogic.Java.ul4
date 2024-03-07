/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Exception when an parameter has been specified twice in a signature.
**/
public class DuplicateParameterException extends ArgumentException
{
	public DuplicateParameterException(String name, String parameterName, int parameterPosition)
	{
		super((name != null ? name + "() " : "") + "parameter " + FunctionRepr.call(parameterName) + (parameterPosition >= 0 ? " (position " + parameterPosition + ")": "") + " specified multiple times");
	}

	public DuplicateParameterException(String parameterName)
	{
		this(null, parameterName, -1);
	}

	public DuplicateParameterException(String name, String parameterName)
	{
		this(name, parameterName, -1);
	}

	public DuplicateParameterException(UL4Name object, String parameterName)
	{
		this(object.getFullNameUL4(), parameterName);
	}

	public DuplicateParameterException(Object object, String parameterName)
	{
		this(object instanceof UL4Name ? ((UL4Name)object).getFullNameUL4() : Utils.objectType(object), parameterName);
	}

	public DuplicateParameterException(String name, ParameterDescription parameter)
	{
		this(name, parameter.getName(), parameter.getPosition());
	}

	public DuplicateParameterException(UL4Name object, ParameterDescription parameter)
	{
		this(object.getFullNameUL4(), parameter);
	}
}
