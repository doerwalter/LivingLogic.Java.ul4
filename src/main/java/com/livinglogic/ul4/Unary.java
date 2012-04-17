/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

abstract class Unary implements AST
{
	protected AST obj;

	public Unary(AST obj)
	{
		this.obj = obj;
	}

	public String toString()
	{
		return name() + "(" + obj + ")";
	}
}
