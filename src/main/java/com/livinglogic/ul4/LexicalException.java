/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class LexicalException extends Exception
{
	public LexicalException(int start, int end, String string)
	{
		super("Unmatched input " + string);
	}
}
