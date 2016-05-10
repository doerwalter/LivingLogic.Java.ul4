/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class ElIfBlockAST extends ConditionalBlockWithCondition
{
	public ElIfBlockAST(Tag tag, Slice pos, CodeAST condition)
	{
		super(tag, pos, condition);
	}

	public String getType()
	{
		return "elifblock";
	}
}
