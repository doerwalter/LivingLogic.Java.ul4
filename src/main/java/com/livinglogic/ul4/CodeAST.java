/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
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
	 * @param pos The slice in the template source, where the source for this object is located.
	 */
	public CodeAST(Tag tag, Slice pos)
	{
		super(pos);
		this.tag = tag;
	}

	@Override
	public InterpretedTemplate getTemplate()
	{
		return tag.getTemplate();
	}

	public Tag getTag()
	{
		return tag;
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

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "tag":
				return tag;
			default:
				return super.getAttrUL4(key);
		}
	}
}
