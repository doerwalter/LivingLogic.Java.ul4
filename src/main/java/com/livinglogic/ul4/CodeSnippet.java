/*
** Copyright 2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import org.apache.commons.lang.StringUtils;

/**
 * A class that holds a code snippet (i.e. code inside a tag) and the code
 * before and after the relevant code inside the tag.
 * Use for formatting exception message
 */
public class CodeSnippet
{
	/**
	 * The code before the relevant code
	 */
	protected String prefix;

	/**
	 * The relevant code itself
	 */
	protected String code;

	/**
	 * The code after the relevant code
	 */
	protected String suffix;

	/**
	 * Create a new {@code CodeSnippet} object.
	 */
	public CodeSnippet(String code)
	{
		this.prefix = "";
		this.code = code;
		this.suffix = "";
	}

	/**
	 * Create a new {@code CodeSnippet} object.
	 */
	public CodeSnippet(String prefix, String code, String suffix)
	{
		this.prefix = prefix;
		this.code = code;
		this.suffix = suffix;
	}

	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		String prefix = rawRepr(this.prefix);
		String code = rawRepr(this.code);
		String suffix = rawRepr(this.suffix);
		buffer.append(prefix);
		buffer.append(code);
		buffer.append(suffix);
		buffer.append("\n");
		buffer.append(StringUtils.repeat(" ", prefix.length()));
		buffer.append(StringUtils.repeat("~", code.length()));
		return buffer.toString();
	}

	private static String rawRepr(String string)
	{
		string = FunctionRepr.call(string);
		return string.substring(1, string.length()-1);
	}

}
