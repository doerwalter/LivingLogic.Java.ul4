/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Exception when a * argument isn't a list
 */
public class RemainingArgumentsException extends ArgumentException
{
	public RemainingArgumentsException(String name)
	{
		super("* argument for " + name + "() must be list");
	}

	public RemainingArgumentsException(UL4Name object)
	{
		this(object.getFullNameUL4());
	}

	public RemainingArgumentsException(Object object)
	{
		this(object instanceof UL4Name ? ((UL4Name)object).getFullNameUL4() : Utils.objectType(object));
	}
}
