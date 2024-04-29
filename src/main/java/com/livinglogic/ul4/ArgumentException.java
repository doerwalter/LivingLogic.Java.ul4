/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Base class of all exceptions for some kind of problems with arguments.
**/
public class ArgumentException extends UnsupportedOperationException
{
	public ArgumentException(String message)
	{
		super(message);
	}
}
