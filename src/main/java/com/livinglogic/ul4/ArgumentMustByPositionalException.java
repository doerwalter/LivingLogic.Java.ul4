/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Exception when an argument must be passed by position
**/
public class ArgumentMustBePositionalException extends ArgumentException
{
	public ArgumentMustBePositionalException(String name, String parameterName)
	{
		super(name + "() parameter " + parameterName + " must be given by position");
	}

	public ArgumentMustBePositionalException(UL4Name object, String parameterName)
	{
		this(object.getFullNameUL4(), parameterName);
	}
}
