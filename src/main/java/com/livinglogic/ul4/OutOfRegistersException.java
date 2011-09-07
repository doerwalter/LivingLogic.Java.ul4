/*
** Copyright 2009-2011 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class OutOfRegistersException extends RuntimeException
{
	public OutOfRegistersException()
	{
		super("out of registers");
	}
}
