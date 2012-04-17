/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ArgumentCountMismatchException extends RuntimeException
{
	public ArgumentCountMismatchException(String type, String name, int given, int requiredMin, int requiredMax)
	{
		super(type + " " + name + "() expects " + (requiredMin == requiredMax ? "exactly " + requiredMin + " argument" + (requiredMin==1 ? "" : "s") : requiredMin + "-" + requiredMax + " arguments") + ", " + given + " given!");
	}

	public ArgumentCountMismatchException(String type, String name, int given, int required)
	{
		this(type, name, given, required, required);
	}
}
