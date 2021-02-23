/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ElseBlockAST extends ConditionalBlock
{
	protected static class Type extends ConditionalBlock.Type
	{
		@Override
		public String getModuleName()
		{
			return "ul4";
		}

		@Override
		public String getNameUL4()
		{
			return "ElseBlockAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.elseblock";
		}

		@Override
		public String getDoc()
		{
			return "An else block.";
		}

		@Override
		public ElseBlockAST create(String id)
		{
			return new ElseBlockAST(null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ElseBlockAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ElseBlockAST(Template template, Slice startPos, Slice stopPos)
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
