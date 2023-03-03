/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ElIfBlockAST extends ConditionalBlockWithCondition
{
	protected static class Type extends ConditionalBlockWithCondition.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ElIfBlockAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.elifblock";
		}

		@Override
		public String getDoc()
		{
			return "AST node for an ``<?elif?>`` block.";
		}

		@Override
		public ElIfBlockAST create(String id)
		{
			return new ElIfBlockAST(null, -1, -1, -1, -1, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ElIfBlockAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ElIfBlockAST(Template template, int startPosStart, int startPosStop, int stopPosStart, int stopPosStop, CodeAST condition)
	{
		super(template, startPosStart, startPosStop, stopPosStart, stopPosStop, condition);
	}

	public String getType()
	{
		return "elifblock";
	}

	@Override
	public String getBlockTag()
	{
		return "<?elif?>";
	}
}
