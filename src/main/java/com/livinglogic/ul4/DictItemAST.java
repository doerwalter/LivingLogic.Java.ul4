/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class DictItemAST extends DictItemASTBase
{
	protected static class Type extends DictItemASTBase.Type
	{
		@Override
		public String getNameUL4()
		{
			return "DictItemAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.dictitem";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a dictionary entry in a dict expression (:class:`DictAST`).";
		}

		@Override
		public DictItemAST create(String id)
		{
			return new DictItemAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof DictItemAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected AST key;
	protected AST value;

	public DictItemAST(Template template, int posStart, int posStop, AST key, AST value)
	{
		super(template, posStart, posStop);
		this.key = key;
		this.value = value;
	}

	public String getType()
	{
		return "dictitem";
	}

	public void evaluateDict(EvaluationContext context, Map result)
	{
		result.put(key.decoratedEvaluate(context), value.decoratedEvaluate(context));
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(key);
		encoder.dump(value);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		key = (AST)decoder.load();
		value = (AST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(SeqItemASTBase.attributes, "key", "value");

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
			case "key":
				return key;
			case "value":
				return value;
			default:
				return super.getAttrUL4(context, key);
		}
	}
}
