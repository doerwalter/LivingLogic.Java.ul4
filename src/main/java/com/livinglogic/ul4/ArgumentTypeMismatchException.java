/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Thrown by implementions of {@link Function} or {@link Method} when the
 * function/method cannot handle the combination of argument types.
 */
public class ArgumentTypeMismatchException extends UnsupportedOperationException
{
	public ArgumentTypeMismatchException(String template, Object... args)
	{
		super(format(template, args));
	}

	private static String format(String template, Object... args)
	{
		String[] parts = (template + " not supported!").split("\\{\\}");
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < parts.length; ++i)
		{
			if (i != 0)
				buffer.append(Utils.objectType(args[i-1]));
			buffer.append(parts[i]);
		}
		return buffer.toString();
	}
}
