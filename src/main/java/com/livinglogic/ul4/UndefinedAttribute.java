/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.StringUtils.formatMessage;


public class UndefinedAttribute extends Undefined
{
	protected static class Type extends Undefined.Type
	{
		@Override
		public String getNameUL4()
		{
			return "undefinedattribute";
		}

		@Override
		public String getDoc()
		{
			return "The result of accessing an undefined attribute";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof UndefinedAttribute;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	private Object object;
	private Object attrName;

	public UndefinedAttribute(Object object, String attrName)
	{
		this.object = object;
		this.attrName = attrName;
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" ");
		formatter.visit(attrName);
		formatter.append(" of ");
		formatter.visit(object);
		formatter.append(">");
	}

	public String toString()
	{
		return formatMessage("undefined attribute {!r} of {!R}", attrName, object);
	}
}
