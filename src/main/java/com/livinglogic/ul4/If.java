/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class If extends ConditionalBlockWithCondition
{
	public If(Location location, AST condition)
	{
		super(location, condition);
	}

	public String getType()
	{
		return "if";
	}
}
