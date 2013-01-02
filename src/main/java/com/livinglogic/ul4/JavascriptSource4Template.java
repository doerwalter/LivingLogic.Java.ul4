/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


public class JavascriptSource4Template
{
	private InterpretedTemplate template;

	public JavascriptSource4Template(InterpretedTemplate template)
	{
		this.template = template;
	}

	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("ul4.Template.loads(");
		buffer.append(FunctionAsJSON.call(template.dumps()));
		buffer.append(")");
		return buffer.toString();
	}
}
