/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class WhitespaceException extends RuntimeException
{
	public WhitespaceException(String whitespace)
	{
		super("whitespace mode " + whitespace + " unknown");
	}
}
