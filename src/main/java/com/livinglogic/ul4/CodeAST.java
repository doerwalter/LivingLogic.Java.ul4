/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.io.IOException;
import java.util.Set;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.UL4ONSerializable;

/**
 * The base class of all nodes in the abstract syntax tree, i.e. everything
 * inside a template tag.
 */
public abstract class CodeAST extends AST
{
	/**
	 * The {@code Tag} object this node belongs to.
	 */
	protected Tag tag;

	/**
	 * Create a new {@code CodeAST} object.
	 * @param tag The {@code Tag} object this node belongs to.
	 * @param start The start offset in the template source, where the source for this object is located.
	 * @param end The end offset in the template source, where the source for this object is located.
	 */
	public CodeAST(Tag tag, int startPos, int endPos)
	{
		super(startPos, endPos);
		this.tag = tag;
	}

	@Override
	public String getSource()
	{
		return tag.getSource();
	}

	@Override
	public String getText()
	{
		return tag.getSource().substring(startPos, endPos);
	}

	public Tag getTag()
	{
		return tag;
	}

	public CodeSnippet getSnippet()
	{
		return new CodeSnippet(tag.getSource(), tag.getStartPos(), tag.getEndPos(), startPos, endPos);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(tag);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		tag = (Tag)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(AST.attributes, "tag");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "tag":
				return tag;
			default:
				return super.getItemStringUL4(key);
		}
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" startPos=");
		formatter.visit(startPos);
		formatter.append(" endPos=");
		formatter.visit(endPos);
		formatter.append(" code=");
		formatter.visit(getText());
		formatter.append(">");
	}
}
