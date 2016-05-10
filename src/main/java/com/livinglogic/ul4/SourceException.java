/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import org.apache.commons.lang.StringUtils;

public class SourceException extends RuntimeException
{
	protected SourcePart part;

	public SourceException(Throwable cause, SourcePart part)
	{
		super(makeMessage(part), cause);
		this.part = part;
	}

	private static String rawRepr(String string)
	{
		string = FunctionRepr.call(string);
		return string.substring(1, string.length()-1);
	}

	private static void makeCodeSnippet(StringBuilder buffer, SourcePart part)
	{
		String source = part.getTemplate().getSource();
		Slice tagPos, codePos;

		if (part instanceof Tag)
		{
			tagPos = ((Tag)part).getPos();
			codePos = tagPos;
		}
		else // AST
		{
			codePos = part.getPos();
			if (part instanceof CodeAST)
			{
				Tag tag = ((CodeAST)part).getTag();
				// top level templates have no tag
				tagPos = tag == null ? codePos : tag.getPos();
			}
			else // TextAST
			{
				tagPos = codePos;
			}
		}
		String prefix = rawRepr(source.substring(tagPos.getStart(), codePos.getStart()));
		String code = rawRepr(source.substring(codePos.getStart(), codePos.getStop()));
		String suffix = rawRepr(source.substring(codePos.getStop(), tagPos.getStop()));
		buffer.append(prefix);
		buffer.append(code);
		buffer.append(suffix);
		buffer.append("\n");
		buffer.append(StringUtils.repeat(" ", prefix.length()));
		buffer.append(StringUtils.repeat("~", code.length()));
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
		int offset = part.getPos().getStart();
		buffer.append(offset);
		buffer.append(":");
		buffer.append(part.getPos().getStop());

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
		makeCodeSnippet(buffer, part);
		return buffer.toString();
	}
}
