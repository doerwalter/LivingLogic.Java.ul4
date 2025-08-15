/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

import java.util.StringTokenizer;

import org.apache.commons.text.StringEscapeUtils;

import com.livinglogic.ul4.FunctionRepr;

import static com.livinglogic.ul4.Utils.objectType;


/**
Class that provides utilities for working with strings

@author W. Doerwald
**/

public class StringUtils
{
	/**
	Format a message replacing placeholders of the form {}, {!r}, {!R}, {!`}, {!t},
	{0}, {0!r}, {!R}, {!`} or {0!t}.
	**/
	public static String formatMessage(String template, Object... args)
	{
		StringBuilder buffer = new StringBuilder();
		int argIndex = 0;

		StringTokenizer tokenizer = new StringTokenizer(template, "{}", true);

		boolean outsidePlaceholder = true;

		int form = 0;
		String position = null;
		while (tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();
			if ("{".equals(token))
			{
				outsidePlaceholder = false;
				form = 0;
				token = null;
			}
			else if ("}".equals(token))
			{
				Object arg = args[position == null || position.length() == 0 ? argIndex++ : Integer.parseInt(position)];
				switch (form)
				{
					case 0:
						buffer.append(arg != null ? arg.toString() : "null");
						break;
					case 1:
						String repr = FunctionRepr.call(arg);
						if (repr.length() <= 300)
							buffer.append(repr);
						else
						{
							buffer.append(objectType(arg));
							buffer.append(" instance");
						}
						break;
					case 2:
						buffer.append(FunctionRepr.call(arg));
						break;
					case 3:
						buffer.append(objectType(arg));
						break;
					case 4:
						String output = arg != null ? arg.toString() : "null";
						int maxcount = 0;
						int count = 0;
						for (int pos = 0; pos < output.length(); ++pos)
						{
							char c = output.charAt(pos);
							if (c == '`')
							{
								++count;
							}
							else
							{
								if (count > maxcount)
									maxcount = count;
								count = 0;
							}
						}
						if (count > maxcount)
							maxcount = count;
						int i;
						for (i = 0; i < maxcount + 1; ++i)
							buffer.append('`');
						buffer.append(output);
						for (i = 0; i < maxcount + 1; ++i)
							buffer.append('`');
						break;
				}
				outsidePlaceholder = true;
			}
			else if (outsidePlaceholder)
				buffer.append(token);
			else
			{
				if (token.endsWith("!r"))
				{
					form = 1;
					position = token.substring(0, token.length() - 2);
				}
				else if (token.endsWith("!R"))
				{
					form = 2;
					position = token.substring(0, token.length() - 2);
				}
				else if (token.endsWith("!t"))
				{
					form = 3;
					position = token.substring(0, token.length() - 2);
				}
				else if (token.endsWith("!`"))
				{
					form = 4;
					position = token.substring(0, token.length() - 2);
				}
				else
				{
					form = 0;
					position = token;
				}
			}
		}
		return buffer.toString();
	}

	/**
	Return a string with the first leter converted to lowercase.
	@param text the string to be converted.
	@return the converted string.
	**/
	public static String lowerCaseFirstLetter(String text)
	{
		if (text == null)
			return null;
		else if (text.length() == 1)
			return text.toLowerCase();
		else
		{
			String firstLetter = text.substring(0, 1);
			String rest = text.substring(1);
			return firstLetter.toLowerCase() + rest;
		}
	}

	/**
	Return a string converted to a JSON string literal.

	`null` is supported and returns `"null"`.

	@param s the string to be converted.
	@return the converted string.
	**/
	public static String stringAsJson(String s)
	{
		if (s == null)
			return "null";

		return "\"" + StringEscapeUtils.escapeJava(s).replace("<", "\\u003c") + "\"";
	}

	/**
	Return a string converted to a SQL string literal.

	`null` is supported and returns `"null"`.

	@param s the string to be converted.
	@return the converted string.
	**/
	public static String stringAsSQL(String s)
	{
		if (s == null)
			return "null";

		return "'" + s.replace("'", "''") + "'";
	}
}
