/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

class Text extends AST
{
	public Text(Location location, int start, int end)
	{
		super(location, start, end);
	}

	public String getText()
	{
		InterpretedTemplate template = location.getRoot();
		String text = location.getCode();
		if (template != null)
			text = template.formatText(text);
		return text;
	}

	public void toString(Formatter formatter)
	{
		formatter.write("text ");
		formatter.write(FunctionRepr.call(getText()));
	}

	public String getType()
	{
		return "text";
	}

	public Object evaluate(EvaluationContext context)
	{
		context.write(getText());
		return null;
	}

	protected static Set<String> attributes = makeExtendedSet(AST.attributes, "text");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("text".equals(key))
			return getText();
		else
			return super.getItemStringUL4(key);
	}
}
