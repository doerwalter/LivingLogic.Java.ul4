/*
** Copyright 2015-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

/**
An exception that is thrown when an UL4 AST node is not supported by VSQL.
**/
public class UnsupportedUL4ASTException extends RuntimeException
{
	public UnsupportedUL4ASTException(String message)
	{
		super(message);
	}
}
