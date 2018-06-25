/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Thrown by implementions of {@link Function} or {@link FunctionWithContext}
 * when the function/method cannot handle the number of arguments provided.
 */
public class ArgumentCountMismatchException extends UnsupportedOperationException
{
	public ArgumentCountMismatchException(String type, String name, int given, int requiredMin, int requiredMax)
	{
		super(type + " " + name + "() expects " + (requiredMin == requiredMax ? "exactly " + requiredMin + " argument" + (requiredMin==1 ? "" : "s") : (requiredMax < 0 ? "at least " + requiredMin + " argument" + (requiredMin==1 ? "" : "s") : requiredMin + "-" + requiredMax + " arguments")) + ", " + given + " given!");
	}

	public ArgumentCountMismatchException(String type, String name, int given, int required)
	{
		this(type, name, given, required, required);
	}
}
