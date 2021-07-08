/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;


public class DictAST extends CodeAST
{
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "DictAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.dict";
		}

		@Override
		public String getDoc()
		{
			return "AST node for creating a dict object (e.g. `{k: v, **u}`.";
		}

		@Override
		public DictAST create(String id)
		{
			return new DictAST(null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof DictAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected List<DictItemASTBase> items = new LinkedList<DictItemASTBase>();

	public DictAST(Template template, Slice pos)
	{
		super(template, pos);
	}

	public void append(DictItemASTBase item)
	{
		items.add(item);
	}

	public String getType()
	{
		return "dict";
	}

	@Override
	public Object evaluate(EvaluationContext context)
	{
		Map result = new LinkedHashMap();

		for (DictItemASTBase item : items)
			item.decoratedEvaluateDict(context, result);
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
		items = (List<DictItemASTBase>)decoder.load();
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
