/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class ElseBlockAST extends ConditionalBlock
{
	public ElseBlockAST(InterpretedTemplate template, Slice startPos, Slice stopPos)
	{
		super(template, startPos, stopPos);
	}

	public String getType()
	{
		return "elseblock";
	}

	public boolean hasToBeExecuted(EvaluationContext context)
	{
		return true;
	}

	public void toString(Formatter formatter)
	{
		formatter.write("else:");
		formatter.lf();
		formatter.indent();
		super.toString(formatter);
		formatter.dedent();
	}
}
