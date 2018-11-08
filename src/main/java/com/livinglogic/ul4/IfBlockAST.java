/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class IfBlockAST extends ConditionalBlockWithCondition
{
	public IfBlockAST(InterpretedTemplate template, Slice pos, CodeAST condition)
	{
		super(template, pos, condition);
	}

	public String getType()
	{
		return "ifblock";
	}
}
