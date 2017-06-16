/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
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

	public LocationException(Throwable cause, SourcePart location)
	{
		super(makeMessage(location), cause);
		this.location = location;
	}

	private static String rawRepr(String string)
	{
		string = FunctionRepr.call(string);
		return string.substring(1, string.length()-1);
	}

	private static void makeCodeSnippet(StringBuilder buffer, SourcePart location)
	{
		String source = location.getTemplate().getSource();
		Slice tagPos, codePos;

		if (location instanceof Tag)
		{
			tagPos = ((Tag)location).getPos();
			codePos = tagPos;
		}
		else // AST
		{
			codePos = location.getPos();
			if (location instanceof CodeAST)
			{
				Tag tag = ((CodeAST)location).getTag();
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

	private static String makeMessage(SourcePart location)
	{
		StringBuilder buffer = new StringBuilder();
		String name = null;
		InterpretedTemplate template = location.getTemplate();
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
		int offset = location.getPos().getStart();
		buffer.append(offset);
		buffer.append(":");
		buffer.append(location.getPos().getStop());

		// Determine line and column number
		int line = 1;
		int col;
		String source = location.getTemplate().getSource();
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
		makeCodeSnippet(buffer, location);
		return buffer.toString();
	}

	public Slice getOuterPos()
	{
		if (location instanceof Tag)
			return ((Tag)location).getPos();
		else // AST
		{
			Slice codePos = location.getPos();
			if (location instanceof CodeAST)
			{
				Tag tag = ((CodeAST)location).getTag();
				// top level templates have no tag
				return  tag == null ? codePos : tag.getPos();
			}
			else // TextAST
				return codePos;
		}
	}

	public Slice getInnerPos()
	{
		return location.getPos();
	}

	protected static Set<String> attributes = makeSet("cause", "location", "template", "outerpos", "innerpos");

	public Set<String> dirUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "cause":
				return getCause();
			case "location":
				return location;
			case "template":
				return location.getTemplate();
			case "outerpos":
				return getOuterPos();
			case "innerpos":
				return getInnerPos();
			default:
				throw new AttributeException(this, key);
		}
	}
}
