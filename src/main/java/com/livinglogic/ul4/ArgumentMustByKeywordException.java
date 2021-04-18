/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Exception when an argument must be passed by keyword
**/
public class ArgumentMustBeKeywordException extends ArgumentException
{
	public ArgumentMustBeKeywordException(String name, String parameterName)
	{
		super(name + "() parameter " + parameterName + " must be given by keyword");
	}

	public ArgumentMustBeKeywordException(UL4Name object, String parameterName)
	{
		this(object.getFullNameUL4(), parameterName);
	}
}
