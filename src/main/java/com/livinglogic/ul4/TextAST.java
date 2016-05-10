/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
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
 * The base class of the classes that represent literal text in the template source.
 */
public class TextAST extends AST
{
	protected InterpretedTemplate template;

	public TextAST(InterpretedTemplate template, Slice pos)
	{
		super(pos);
		this.template = template;
	}

	@Override
	public InterpretedTemplate getTemplate()
	{
		return template;
	}

	// Used by {@see InterpretedTemplate#compile} to fix the template references for inner templates
	void setTemplate(InterpretedTemplate template)
	{
		this.template = template;
	}

	public void toString(Formatter formatter)
	{
		formatter.write("text ");
		formatter.write(FunctionRepr.call(getCodeText()));
	}

	public String getType()
	{
		return "text";
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(template);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		template = (InterpretedTemplate)decoder.load();
	}

	public Object evaluate(EvaluationContext context)
	{
		context.write(getCodeText());
		return null;
	}

	protected static Set<String> attributes = makeExtendedSet(AST.attributes, "template", "text");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "template":
				return template;
			case "text":
				return getCodeText();
			default:
				return super.getItemStringUL4(key);
		}
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" pos=(");
		formatter.visit(pos.getStart());
		formatter.append(":");
		formatter.visit(pos.getStop());
		formatter.append(") text=");
		formatter.visit(getCodeText());
		formatter.append(">");
	}
}
