/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class UnknownFunctionException extends RuntimeException
{
	public UnknownFunctionException(String functionName)
	{
		super("Function '" + functionName + "' unknown!");
	}
}
