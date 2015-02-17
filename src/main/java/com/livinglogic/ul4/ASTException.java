/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ASTException extends RuntimeException
{
	protected AST node;

	public ASTException(Throwable cause, AST node)
	{
		super("in " + node.getType(), cause);
		this.node = node;
	}
}
