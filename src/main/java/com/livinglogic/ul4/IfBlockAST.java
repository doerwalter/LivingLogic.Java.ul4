/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class IfBlockAST extends ConditionalBlockWithCondition
{
	protected static class Type extends AbstractInstanceType
	{
		public Type()
		{
			super("ul4", "IfBlockAST", "de.livinglogic.ul4.ifblock", "An if block.");
		}

		@Override
		public IfBlockAST create(String id)
		{
			return new IfBlockAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof IfBlockAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public IfBlockAST(Template template, Slice startPos, Slice stopPos, CodeAST condition)
	{
		super(template, startPos, stopPos, condition);
	}

	public String getType()
	{
		return "ifblock";
	}
}
