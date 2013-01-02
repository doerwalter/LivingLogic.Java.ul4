/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import org.apache.commons.lang.StringEscapeUtils;

public class JavaSource4Template
{
	private InterpretedTemplate template;

	public JavaSource4Template(InterpretedTemplate template)
	{
		this.template = template;
	}

	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("new com.livinglogic.ul4.InterpretedTemplate.loads(\"");
		buffer.append(StringEscapeUtils.escapeJava(template.dumps()));
		buffer.append("\")");
		return buffer.toString();
	}
}
