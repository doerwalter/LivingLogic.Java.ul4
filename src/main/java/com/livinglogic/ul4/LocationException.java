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
	protected AST location;
	int line;
	int col;

	public LocationException(AST location)
	{
		super(makeMessage(location));
		this.location = location;
	}

	private static String rawRepr(String string)
	{
		string = FunctionRepr.call(string);
		return string.substring(1, string.length()-1);
	}

	private static String templateDescription(AST location)
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

	private static String locationDescription(AST location)
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("offset ");
		Slice pos = location.getPos();
		buffer.append(pos.getStart());
		buffer.append(":");
		buffer.append(pos.getStop());
		buffer.append("; line ");
		buffer.append(location.getLine());
		buffer.append("; col ");
		buffer.append(location.getCol());
		return buffer.toString();
	}

	private static String sourceSnippet(AST location)
	{
		StringBuilder buffer = new StringBuilder();
		Slice pos = location.getPos();
		InterpretedTemplate template = location.getTemplate();
		String source = template.getFullSource();

		String prefix = rawRepr(location.getSourcePrefix());
		String code = rawRepr(location.getSource());
		String suffix = rawRepr(location.getSourceSuffix());
		buffer.append(prefix);
		buffer.append(code);
		buffer.append(suffix);
		buffer.append("\n");
		buffer.append(StringUtils.repeat(" ", prefix.length()));
		buffer.append(StringUtils.repeat("~", code.length()));
		return buffer.toString();
	}

	private static String makeMessage(AST location)
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append(templateDescription(location));
		buffer.append(": ");
		buffer.append(locationDescription(location));
		buffer.append("\n");
		buffer.append(sourceSnippet(location));
		return buffer.toString();
	}

	protected static Set<String> attributes = makeSet(
		"context",
		"location"
	);

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
