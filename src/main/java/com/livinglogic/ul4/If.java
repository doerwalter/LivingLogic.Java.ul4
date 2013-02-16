/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class If extends ConditionalBlockWithCondition
{
	public If(Location location, int start, int end, AST condition)
	{
		super(location, start, end, condition);
	}

	public String getType()
	{
		return "if";
	}
}
