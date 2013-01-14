/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

class Text extends Tag
{
	private String text;

	public Text(Location location, String text)
	{
		super(location);
		this.text = text;
	}

	public String getText()
	{
		return text != null ? text : location.getCode();
	}

	public String toString(int indent)
	{
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("text(");
		buffer.append(FunctionRepr.call(getText()));
		buffer.append(")\n");
		return buffer.toString();
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

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(text);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		text = (String)decoder.load();
	}
}
