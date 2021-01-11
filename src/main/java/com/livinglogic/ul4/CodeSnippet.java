/*
** Copyright 2017-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import org.apache.commons.lang.StringUtils;

/**
 * A class that holds the data for formatting a code snippet (i.e. code
 * inside a tag with the surrounding tag)
 * Used for formatting exception message.
 */
public class CodeSnippet
{
	/**
	 * The complete source code
	 */
	protected String source;

	/**
	 * The start position of the tag
	 */
	protected int startPos;

	/**
	 * The end position of the tag
	 */
	protected int endPos;

	/**
	 * The start position of the code inside the tag
	 */
	protected int startPosCode;

	/**
	 * The end position of the code inside the tag
	 */
	protected int endPosCode;

	/**
	 * Create a new {@code CodeSnippet} object.
	 */
	public CodeSnippet(String source, int startPos, int endPos)
	{
		this.source = source;
		this.startPos = startPos;
		this.endPos = endPos;
		this.startPosCode = startPos;
		this.endPosCode = endPos;
	}

	/**
	 * Create a new {@code CodeSnippet} object.
	 */
	public CodeSnippet(String source, int startPos, int endPos, int startPosCode, int endPosCode)
	{
		this.source = source;
		this.startPos = startPos;
		this.endPos = endPos;
		this.startPosCode = startPosCode;
		this.endPosCode = endPosCode;
	}

	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		String prefix = rawRepr(source.substring(startPos, startPosCode));
		String code = rawRepr(source.substring(startPosCode, endPosCode));
		String suffix = rawRepr(source.substring(endPosCode, endPos));
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
