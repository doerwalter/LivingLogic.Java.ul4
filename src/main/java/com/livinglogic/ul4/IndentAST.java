/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

/**
 * An IndentAST is a literal text in the template source that represents the
 * indentation at the beginning of a line. For "smart" whitespace mode
 */
class IndentAST extends TextAST
{
	protected String text;

	public IndentAST(String source, int startPos, int endPos, String text)
	{
		super(source, startPos, endPos);
		this.text = text;
	}

	@Override
	public String getText()
	{
		return text != null ? text : super.getText();
	}

	public void setText(String text)
	{
		this.text = text;
	}

	@Override
	public Object evaluate(EvaluationContext context)
	{
		for (String indent : context.indents)
			context.write(indent);
		context.write(getText());
		return null;
	}

	public void toString(Formatter formatter)
	{
		formatter.write("indent ");
		formatter.write(FunctionRepr.call(getText()));
	}

	public String getType()
	{
		return "indent";
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
