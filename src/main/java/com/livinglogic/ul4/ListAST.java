/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class ListAST extends CodeAST
{
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ListAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.list";
		}

		@Override
		public String getDoc()
		{
			return "AST node for creating a list object (e.g. ``[x, y, *z]``).";
		}

		@Override
		public ListAST create(String id)
		{
			return new ListAST(null, -1, -1);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ListAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected List<SeqItemASTBase> items = new LinkedList<SeqItemASTBase>();

	public ListAST(Template template, int posStart, int posStop)
	{
		super(template, posStart, posStop);
	}

	public List<SeqItemASTBase> getItems()
	{
		return items;
	}

	public void append(SeqItemASTBase item)
	{
		items.add(item);
	}

	public String getType()
	{
		return "list";
	}

	public Object evaluate(EvaluationContext context)
	{
		ArrayList result = new ArrayList(items.size());

		for (SeqItemASTBase item : items)
			item.decoratedEvaluateList(context, result);
		return result;
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(items);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		items = (List<SeqItemASTBase>)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "items");

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
			case "items":
				return items;
			default:
				return super.getAttrUL4(context, key);
		}
	}
}
