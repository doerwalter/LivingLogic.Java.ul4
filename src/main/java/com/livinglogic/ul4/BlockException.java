/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


/**
Exception class for issues related to block nesting in the UL4 template engine.
**/
public class BlockException extends RuntimeException
{
	public BlockException(String message)
	{
		super(message);
	}
}
