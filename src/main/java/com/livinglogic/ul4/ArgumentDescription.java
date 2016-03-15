/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ArgumentDescription implements UL4Repr
{
	protected String name;
	protected int position;
	public enum Type
	{
		REQUIRED,
		DEFAULT,
		VAR_POSITIONAL,
		VAR_KEYWORD,
	}
	protected Type type;
	protected Object defaultValue;

	public ArgumentDescription(String name, int position, Type type, Object defaultValue)
	{
		this.name = name;
		this.position = position;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	public String getName()
	{
		return name;
	}

	public int getPosition()
	{
		return position;
	}

	public Type getType()
	{
		return type;
	}

	public Object getDefaultValue()
	{
		return defaultValue;
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" ");
		formatter.append(toString());
		formatter.append(">");
	}

	public String toString()
	{
		switch (type)
		{
			case REQUIRED:
				return name;
			case DEFAULT:
				return name + "=" + FunctionRepr.call(defaultValue);
			case VAR_POSITIONAL:
				return "*" + name;
			case VAR_KEYWORD:
				return "**" + name;
		}
		return null;
	}
}
