/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

class TextAST extends AST
{
	protected String source;

	public TextAST(String source, int startPos, int endPos)
	{
		super(startPos, endPos);
		this.source = source;
	}

	@Override
	public String getText()
	{
		return source.substring(startPos, endPos);
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

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "text");

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
