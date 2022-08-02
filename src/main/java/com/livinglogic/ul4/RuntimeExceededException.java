/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class RuntimeExceededException extends RuntimeException
{
	public RuntimeExceededException(long milliseconds)
	{
		super("Maximum runtime of " + milliseconds + " ms exceeded");
	}
}
