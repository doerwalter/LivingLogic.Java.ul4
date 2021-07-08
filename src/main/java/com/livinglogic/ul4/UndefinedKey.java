/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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

	public static final UL4Type type = new Type();

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
		formatter.append(" key=");
		formatter.visit(key);
		formatter.append(" object=");
		formatter.visit(object);
		formatter.append(">");
	}

	public String toString()
	{
		String objectRepr = FunctionRepr.call(object);

		if (objectRepr.length() > 200)
			objectRepr = Utils.formatMessage("{!t} instance", object);
		return Utils.formatMessage("undefined key {!r} of {}", key, objectRepr);
	}
}
