/*
** Copyright 2009-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Exception when a required argument to a function is missing
**/
public class MissingArgumentException extends ArgumentException
{
	public MissingArgumentException(String name, String parameterName, int parameterPosition)
	{
		super(name + "(): required argument " + parameterName + " (position " + parameterPosition + ") missing");
	}

	public MissingArgumentException(UL4Name object, String parameterName, int parameterPosition)
	{
		this(object.getFullNameUL4(), parameterName, parameterPosition);
	}

	public MissingArgumentException(String name, ParameterDescription parameter)
	{
		this(name, parameter.getName(), parameter.getPosition());
	}

	public MissingArgumentException(UL4Name name, ParameterDescription parameter)
	{
		this(name.getFullNameUL4(), parameter);
	}
}
