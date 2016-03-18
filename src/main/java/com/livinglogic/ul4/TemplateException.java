/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class TemplateException extends RuntimeException
{
	protected InterpretedTemplate template;

	public TemplateException(Throwable cause, InterpretedTemplate template)
	{
		super(template.nameUL4() != null ? "in template named " + template.nameUL4() : "in unnamed template", cause);
		this.template = template;
	}
}
