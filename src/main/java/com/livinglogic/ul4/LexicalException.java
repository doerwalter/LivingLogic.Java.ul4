/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class LexicalException extends Exception
{
	public LexicalException(String string)
	{
		super("Unmatched input " + string);
	}
}
