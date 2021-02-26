/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
An {@code ParameterDescription} object is used by {@link Signature} objects
to store information about one particular parameter.
**/
public class ParameterDescription implements UL4Repr
{
	/**
	The name of the parameter.
	**/
	protected String name;
	/**
	The position of the parameter in the signature.
	**/
	protected int position;
	public enum Type
	{
		/**
		The parameter must have an argument in the call.
		**/
		REQUIRED,
		/**
		The argument is optional. The parameter has a default value.
		**/
		DEFAULT,
		/**
		The parameter collects all additional positional arguments in a list.
		**/
		VAR_POSITIONAL,
		/**
		The parameter collects all additional keyword arguments in a map.
		**/
		VAR_KEYWORD,
	}
	/**
	The type of the parameter
	**/
	protected Type type;
	/**
	The default value of the parameter (if it is of type {@code DEFAULT})
	**/
	protected Object defaultValue;

	public ParameterDescription(String name, int position, Type type, Object defaultValue)
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
