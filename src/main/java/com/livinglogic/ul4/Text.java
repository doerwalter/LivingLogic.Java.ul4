/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

import static com.livinglogic.utils.StringUtils.removeWhitespace;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

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

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.write(getText());
		return null;
	}
}
