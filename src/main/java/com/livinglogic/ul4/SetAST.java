/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class SetAST extends CodeAST
{
	protected static class Type extends CodeAST.Type
	{

		@Override
		public String getModuleName()
		{
			return "ul4";
		}

		@Override
		public String getNameUL4()
		{
			return "SetAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.set";
		}

		@Override
		public String getDoc()
		{
			return "A set \"literal\".";
		}

		@Override
		public SetAST create(String id)
		{
			return new SetAST(null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof SetAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected List<SeqItemASTBase> items = new LinkedList<SeqItemASTBase>();

	public SetAST(Template template, Slice pos)
	{
		super(template, pos);
	}

	public void append(SeqItemASTBase item)
	{
		items.add(item);
	}

	public String getType()
	{
		return "set";
	}

	public Object evaluate(EvaluationContext context)
	{
		HashSet result = new HashSet(items.size());

		for (SeqItemASTBase item : items)
			item.decoratedEvaluateSet(context, result);
		return result;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(items);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		items = (List<SeqItemASTBase>)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "items");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "items":
				return items;
			default:
				return super.getAttrUL4(key);
		}
	}
}
