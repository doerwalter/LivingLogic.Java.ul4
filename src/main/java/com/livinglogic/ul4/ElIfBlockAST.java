/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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
			return "An elif block.";
		}

		@Override
		public ElIfBlockAST create(String id)
		{
			return new ElIfBlockAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ElIfBlockAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ElIfBlockAST(Template template, Slice startPos, Slice stopPos, CodeAST condition)
	{
		super(template, startPos, stopPos, condition);
	}

	public String getType()
	{
		return "elifblock";
	}
}
