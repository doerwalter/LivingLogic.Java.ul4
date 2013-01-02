/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class TemplateException extends RuntimeException
{
	protected Template template;

	public TemplateException(Throwable cause, Template template)
	{
		super(template.getName() != null ? "in template named " + template.getName() : "in unnamed template", cause);
		this.template = template;
	}
}
