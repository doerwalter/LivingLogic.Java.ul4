/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class UnpackDictItemAST extends DictItemASTBase
{
	protected static class Type extends DictItemASTBase.Type
	{
		@Override
		public String getNameUL4()
		{
			return "UnpackDictItemAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.unpackdictitem";
		}

		@Override
		public String getDoc()
		{
			return "AST node for `**` unpacking expressions in dict \"literal\"\n(e.g. the `**u` in `{k: v, **u}`).";
		}

		@Override
		public UnpackDictItemAST create(String id)
		{
			return new UnpackDictItemAST(null, -1, -1, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof UnpackDictItemAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected AST item;

	public UnpackDictItemAST(Template template, int posStart, int posStop, AST item)
	{
		super(template, posStart, posStop);
		this.item = item;
	}

	public String getType()
	{
		return "unpackdictitem";
	}

	public void evaluateDict(EvaluationContext context, Map result)
	{
		Object item = this.item.decoratedEvaluate(context);

		if (item instanceof Map)
		{
			result.putAll((Map)item);
		}
		else
		{
			String exceptionMessage = "expressions for ** unpacking must evaluate to dicts or iterables of (key, value) pairs";

			for (Iterator iter = Utils.iterator(item); iter.hasNext();)
			{
				Object pair = iter.next();

				if (pair instanceof List && ((List)pair).size()==2)
					result.put(((List)pair).get(0), ((List)pair).get(1));
				else
					throw new ArgumentException(exceptionMessage);
			}
		}
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(item);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		item = (AST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(SeqItemASTBase.attributes, "item");

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
			case "item":
				return item;
			default:
				return super.getAttrUL4(context, key);
		}
	}
}
