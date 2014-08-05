/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class IfBlockAST extends ConditionalBlockWithCondition
{
	public IfBlockAST(Location location, int start, int end, AST condition)
	{
		super(location, start, end, condition);
	}

	public String getType()
	{
		return "ifblock";
	}
}
