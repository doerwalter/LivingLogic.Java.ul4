/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class ElIfBlockAST extends ConditionalBlockWithCondition
{
	public ElIfBlockAST(InterpretedTemplate template, Slice startPos, Slice stopPos, CodeAST condition)
	{
		super(template, startPos, stopPos, condition);
	}

	public String getType()
	{
		return "elifblock";
	}
}
