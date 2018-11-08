/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

import static com.livinglogic.utils.SetUtils.makeSet;

public class LocationException extends RuntimeException implements UL4Dir, UL4GetAttr
{
	protected SourcePart location;

	public LocationException(SourcePart location)
	{
		super(makeMessage(location));
		this.location = location;
	}

	private static String rawRepr(String string)
	{
		string = FunctionRepr.call(string);
		return string.substring(1, string.length()-1);
	}

	private static String templateDescription(SourcePart location)
	{
		StringBuilder buffer = new StringBuilder();
		String name = null;
		InterpretedTemplate template = location.getTemplate();
		buffer.append(template.parentTemplate != null ? "in local template " : "in template ");
		boolean first = true;
		while (template != null)
		{
			if (first)
				first = false;
			else
				buffer.append(" in ");
			name = template.nameUL4();
			buffer.append(name == null ? "(unnamed)" : FunctionRepr.call(name));
			template = template.parentTemplate;
		}
		return buffer.toString();
	}

	private static String locationDescription(SourcePart location)
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("offset ");
		Slice pos = location.getPos();
		int offset = pos.getStart();
		buffer.append(offset);
		buffer.append(":");
		buffer.append(pos.getStop());

		// Determine line and column number
		int line = 1;
		int col;
		InterpretedTemplate template = location.getTemplate();
		String source = template.getSource();
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
		return buffer.toString();
	}

	private static String sourcePrefix(SourcePart location)
	{
		InterpretedTemplate template = location.getTemplate();
		String source = template.getSource();
		int outerStartPos = location.getPos().getStart();
		int innerStartPos = outerStartPos;
		int maxPrefix = 40;
		boolean found = false; // Have we found a natural stopping position?
		while (maxPrefix > 0)
		{
			// We arrived at the start of the source code
			if (outerStartPos == 0)
			{
				found = true;
				break;
			}
			// We arrived at the start of the line
			if (source.charAt(outerStartPos-1) == '\n')
			{
				found = true;
				break;
			}
			--maxPrefix;
			--outerStartPos;
		}
		String result = source.substring(outerStartPos, innerStartPos);
		if (!found)
			result = "..." + result;
		return result;
	}

	private static String sourceSuffix(SourcePart location)
	{
		InterpretedTemplate template = location.getTemplate();
		String source = template.getSource();
		int outerStopPos = location.getPos().getStop();
		int innerStopPos = outerStopPos;
		int maxSuffix = 40;
		boolean found = false; // Have we found a natural stopping position?
		while (maxSuffix > 0)
		{
			// We arrived at the end of the source code
			if (outerStopPos >= source.length())
			{
				found = true;
				break;
			}
			// We arrived at the end of the line
			if (source.charAt(outerStopPos) == '\n')
			{
				found = true;
				break;
			}
			--maxSuffix;
			++outerStopPos;
		}
		String result = source.substring(innerStopPos, outerStopPos);
		if (!found)
			result += "...";
		return result;
	}

	private static String sourceSnippet(SourcePart location)
	{
		StringBuilder buffer = new StringBuilder();
		Slice pos = location.getPos();
		InterpretedTemplate template = location.getTemplate();
		String source = template.getSource();

		String prefix = rawRepr(sourcePrefix(location));
		String code = rawRepr(location.getSource());
		String suffix = rawRepr(sourceSuffix(location));
		buffer.append(prefix);
		buffer.append(code);
		buffer.append(suffix);
		buffer.append("\n");
		buffer.append(StringUtils.repeat(" ", prefix.length()));
		buffer.append(StringUtils.repeat("~", code.length()));
		return buffer.toString();
	}

	private static String makeMessage(SourcePart location)
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append(templateDescription(location));
		buffer.append(": ");
		buffer.append(locationDescription(location));
		buffer.append("\n");
		buffer.append(sourceSnippet(location));
		return buffer.toString();
	}

	protected static Set<String> attributes = makeSet("context", "location");

	public Set<String> dirUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "context":
				return getCause();
			case "location":
				return location;
			default:
				throw new AttributeException(this, key);
		}
	}
}
