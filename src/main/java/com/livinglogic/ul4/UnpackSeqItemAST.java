/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class UnpackSeqItemAST extends SeqItemASTBase
{
	protected static class Type extends SeqItemASTBase.Type
	{
		@Override
		public String getNameUL4()
		{
			return "UnpackSeqItemAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.unpackseqitem";
		}

		@Override
		public String getDoc()
		{
			return "An item in a sequence that unpacks another sequence.";
		}

		@Override
		public UnpackSeqItemAST create(String id)
		{
			return new UnpackSeqItemAST(null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof UnpackSeqItemAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected AST value;

	public UnpackSeqItemAST(Template template, Slice pos, AST value)
	{
		super(template, pos);
		this.value = value;
	}

	public String getType()
	{
		return "unpackseqitem";
	}

	public void evaluateList(EvaluationContext context, List result)
	{
		for (Iterator iter = Utils.iterator(value.decoratedEvaluate(context)); iter.hasNext();)
			result.add(iter.next());
	}

	public void evaluateSet(EvaluationContext context, Set result)
	{
		for (Iterator iter = Utils.iterator(value.decoratedEvaluate(context)); iter.hasNext();)
			result.add(iter.next());
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(value);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		value = (AST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(SeqItemASTBase.attributes, "value");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "value":
				return value;
			default:
				return super.getAttrUL4(key);
		}
	}
}
