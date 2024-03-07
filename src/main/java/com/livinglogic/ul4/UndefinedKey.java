/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class UndefinedKey extends Undefined
{
	protected static class Type extends Undefined.Type
	{
		@Override
		public String getNameUL4()
		{
			return "undefinedkey";
		}

		@Override
		public String getDoc()
		{
			return "The result of accessing an dictionary key or list index.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof UndefinedKey;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	private Object object;
	private Object key;

	public UndefinedKey(Object object, Object key)
	{
		this.object = object;
		this.key = key;
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" ");
		formatter.visit(key);
		formatter.append(" in ");
		formatter.visit(object);
		formatter.append(">");
	}

	public String toString()
	{
		return Utils.formatMessage("undefined key {!r} of {!R}", key, object);
	}
}
