/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import org.apache.commons.lang.StringUtils;

public class TagException extends RuntimeException
{
	private static String makeMessage(InterpretedTemplate template, Tag tag)
	{
		StringBuilder buffer = new StringBuilder();
		String name = template.nameUL4();
		if (name == null)
			buffer.append("in unnamed template");
		else
		{
			buffer.append("in template ");
			buffer.append(FunctionRepr.call(name));
		}
		buffer.append(": offset ");
		buffer.append(tag.getStartPos());
		buffer.append(":");
		buffer.append(tag.getEndPos());
		buffer.append("\n");
		buffer.append(tag.getSnippet().toString());
		return buffer.toString();
	}

	public TagException(Throwable cause, InterpretedTemplate template, Tag tag)
	{
		super(makeMessage(template, tag), cause);
	}
}
