/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import org.apache.commons.lang.StringUtils;

public class SourceException extends RuntimeException
{
	private static String makeMessage(InterpretedTemplate template, SourcePart part)
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
		buffer.append(part.getStartPos());
		buffer.append(":");
		buffer.append(part.getEndPos());
		buffer.append("\n");
		buffer.append(part.getSnippet().toString());
		return buffer.toString();
	}

	public SourceException(Throwable cause, InterpretedTemplate template, SourcePart part)
	{
		super(makeMessage(template, part), cause);
	}
}