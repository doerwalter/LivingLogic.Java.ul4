/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * An {@code ArgumentDescription} object is used by {@link Signature} objects
 * to store information about one particular argument.
 */
public class ArgumentDescription implements UL4Repr
{
	/**
	 * The name of the argument
	 */
	protected String name;
	/**
	 * The position of the argument in the signature.
	 */
	protected int position;
	public enum Type
	{
		/**
		 * The argument must be specified in the call.
		 */
		REQUIRED,
		/**
		 * The argument is optional and has a default value.
		 */
		DEFAULT,
		/**
		 * The argument collects all additional positional arguments in a list.
		 */
		VAR_POSITIONAL,
		/**
		 * The argument collects all additional keyword arguments in a map.
		 */
		VAR_KEYWORD,
	}
	/**
	 * The type of the argument
	 */
	protected Type type;
	/**
	 * The default value of the argument (if it is of type {@code DEFAULT})
	 */
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
