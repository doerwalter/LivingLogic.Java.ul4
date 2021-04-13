/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public abstract class Undefined implements UL4Instance, UL4Bool, UL4Repr
{
	protected static class Type extends AbstractInstanceType
	{
		@Override
		public String getNameUL4()
		{
			return "undefined";
		}

		@Override
		public String getDoc()
		{
			return "The result of accessing an undefined variable, attribute, dictionary key or list index.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof Undefined;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public boolean boolUL4()
	{
		return false;
	}
}
