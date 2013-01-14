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

class Text extends Tag
{
	public Text(Location location)
	{
		super(location);
	}

	public String getText(boolean keepWhitespace)
	{
		String text = location.getCode();
		if (!keepWhitespace)
			text = removeWhitespace(text);
		return text;
	}

	public String toString(int indent)
	{
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("text(");
		buffer.append(FunctionRepr.call(getText(true)));
		buffer.append(")\n");
		return buffer.toString();
	}

	public String getType()
	{
		return "text";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.write(getText(context.getKeepWhitespace()));
		return null;
	}
}
