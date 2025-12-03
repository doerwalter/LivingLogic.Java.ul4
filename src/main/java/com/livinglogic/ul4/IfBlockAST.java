/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class IfBlockAST extends ConditionalBlockWithCondition
{
	protected static class Type extends ConditionalBlockWithCondition.Type
	{
		@Override
		public String getNameUL4()
		{
			return "IfBlockAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.ifblock";
		}

		@Override
		public String getDoc()
		{
			return "AST node for an ``<?if?>`` block in an ``<?if?>/<?elif?>/<?else?>`` block.";
		}

		@Override
		public IfBlockAST create(String id)
		{
			return new IfBlockAST(null, -1, -1, -1, -1, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof IfBlockAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public IfBlockAST(Template template, int startPosStart, int startPosStop, int stopPosStart, int stopPosStop, CodeAST condition)
	{
		super(template, startPosStart, startPosStop, stopPosStart, stopPosStop, condition);
	}

	public String getType()
	{
		return "ifblock";
	}

	@Override
	public String getBlockTag()
	{
		return "<?if?>";
	}
}
