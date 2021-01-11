/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class IfBlockAST extends ConditionalBlockWithCondition
{
	public IfBlockAST(InterpretedTemplate template, Slice startPos, Slice stopPos, CodeAST condition)
	{
		super(template, startPos, stopPos, condition);
	}

	public String getType()
	{
		return "ifblock";
	}
}
