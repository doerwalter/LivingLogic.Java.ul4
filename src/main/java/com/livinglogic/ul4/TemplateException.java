/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class TemplateException extends RuntimeException
{
	protected Template template;

	public TemplateException(Throwable cause, Template template)
	{
		super(template.getFullNameUL4() != null ? "in template named " + template.getFullNameUL4() : "in unnamed template", cause);
		this.template = template;
	}
}
