/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import org.apache.commons.lang.StringUtils;

public class SourceException extends RuntimeException
{
	public SourceException(Throwable cause, SourcePart part)
	{
		super(makeMessage(part), cause);
	}

	private static String makeMessage(SourcePart part)
	{
		StringBuilder buffer = new StringBuilder();
		String name = null;
		InterpretedTemplate template = part.getTemplate();
		if (template.parentTemplate != null)
			buffer.insert(0, "in local template ");
		else
			buffer.insert(0, "in template ");
		boolean first = true;
		while (template != null)
		{
			if (first)
				first = false;
			else
				buffer.append(" in ");
			name = template.nameUL4();
			if (name == null)
				buffer.append("(unnamed)");
			else
				buffer.append(FunctionRepr.call(name));
			template = template.parentTemplate;
		}
		buffer.append(": offset ");
		int offset = part.getStartPos();
		buffer.append(offset);
		buffer.append(":");
		buffer.append(part.getEndPos());

		// Determine line and column number
		int line = 1;
		int col;
		String source = part.getTemplate().getSource();
		int lastLineFeed = source.lastIndexOf("\n", offset);

		if (lastLineFeed == -1)
		{
			col = offset+1;
		}
		else
		{
			col = 1;
			for (int i = 0; i < offset; ++i)
			{
				if (source.charAt(i) == '\n')
				{
					++line;
					col = 0;
				}
				++col;
			}
		}

		buffer.append("; line ");
		buffer.append(line);
		buffer.append("; col ");
		buffer.append(col);
		buffer.append("\n");
		buffer.append(part.getSnippet().toString());
		return buffer.toString();
	}
}
