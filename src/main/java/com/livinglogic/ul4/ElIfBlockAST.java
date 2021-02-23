/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ElIfBlockAST extends ConditionalBlockWithCondition
{
	protected static class Type extends AbstractInstanceType
	{
		public Type()
		{
			super("ul4", "ElIfBlockAST", "de.livinglogic.ul4.elifblock", "An elif block.");
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

	public static UL4Type type = new Type();

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
