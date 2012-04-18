/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

abstract class Binary extends AST
{
	protected AST obj1;
	protected AST obj2;

	public Binary(AST obj1, AST obj2)
	{
		this.obj1 = obj1;
		this.obj2 = obj2;
	}

	public String toString(int indent)
	{
		return getType() + "(" + obj1 + ", " + obj2 + ")";
	}
}
