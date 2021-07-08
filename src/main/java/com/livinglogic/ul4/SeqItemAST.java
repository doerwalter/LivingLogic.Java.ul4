/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class SeqItemAST extends SeqItemASTBase
{
	protected static class Type extends SeqItemASTBase.Type
	{
		@Override
		public String getNameUL4()
		{
			return "SeqItemAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.seqitem";
		}

		@Override
		public String getDoc()
		{
			return "AST node for an item in a list/set \"literal\" (e.g. ``{x, y}`` or ``[x, y]``)";
		}

		@Override
		public SeqItemAST create(String id)
		{
			return new SeqItemAST(null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof SeqItemAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected AST value;

	public SeqItemAST(Template template, Slice pos, AST value)
	{
		super(template, pos);
		this.value = value;
	}

	public String getType()
	{
		return "seqitem";
	}

	public void evaluateList(EvaluationContext context, List result)
	{
		result.add(value.decoratedEvaluate(context));
	}

	public void evaluateSet(EvaluationContext context, Set result)
	{
		result.add(value.decoratedEvaluate(context));
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(value);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		value = (AST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(SeqItemASTBase.attributes, "value");

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
			case "value":
				return value;
			default:
				return super.getAttrUL4(context, key);
		}
	}
}
