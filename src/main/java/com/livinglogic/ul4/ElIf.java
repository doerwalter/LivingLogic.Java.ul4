/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class ElIf extends ConditionalBlockWithCondition
{
	public ElIf(Location location, AST condition)
	{
		super(location, condition);
	}

	public String getType()
	{
		return "elif";
	}
}
