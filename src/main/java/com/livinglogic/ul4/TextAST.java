/*
** Copyright 2009-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Set;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

/**
The base class of the classes that represent literal text in the template source.
**/
public class TextAST extends AST
{
	protected static class Type extends AST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "TextAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.text";
		}

		@Override
		public String getDoc()
		{
			return "AST node for literal text (i.e. the stuff between tags).";
		}

		@Override
		public TextAST create(String id)
		{
			return new TextAST(null, "", 0, 0);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof TextAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected String text;

	public TextAST(Template template, String source, int startPos, int stopPos)
	{
		super(template, startPos, stopPos);
		this.text = source.substring(startPos, stopPos).intern();
	}

	public void toString(Formatter formatter)
	{
		formatter.write("text ");
		formatter.write(FunctionRepr.call(getText()));
	}

	public String getText()
	{
		return text;
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

	@Override
	public Set<String> dirUL4(EvaluationContext context)
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
	{
		switch (key)
		{
			case "text":
				return getText();
			default:
				return super.getAttrUL4(context, key);
		}
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		reprPosLineCol(formatter);
		formatter.append(" text=");
		formatter.visit(getText());
		formatter.append(">");
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(text);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		text = ((String)decoder.load()).intern();
	}
}
