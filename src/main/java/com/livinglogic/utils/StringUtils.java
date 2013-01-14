/*
** Copyright 2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

/**
 * Class that provides utilities for working with string
 *
 * @author W. Doerwald
 */

public class StringUtils
{
	public static String removeWhitespace(String string)
	{
		String[] lines = string.split("\n");
		StringBuilder buffer = new StringBuilder();

		for (String line : lines)
		{
			buffer.append(org.apache.commons.lang.StringUtils.stripStart(line, null));
		}

		return buffer.toString();
	}
}
