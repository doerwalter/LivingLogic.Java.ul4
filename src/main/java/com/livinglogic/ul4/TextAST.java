/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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
			return new TextAST(null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof TextAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public TextAST(Template template, Slice pos)
	{
		super(template, pos);
	}

	public void toString(Formatter formatter)
	{
		formatter.write("text ");
		formatter.write(FunctionRepr.call(getText()));
	}

	public String getText()
	{
		return getSource();
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

	@Override
	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "text":
				return getText();
			default:
				return super.getAttrUL4(key);
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
}
