/*
** Copyright 2009-2011 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class UnterminatedStringException extends Exception
{
	public UnterminatedStringException()
	{
		super("Unterminated string");
	}
}
