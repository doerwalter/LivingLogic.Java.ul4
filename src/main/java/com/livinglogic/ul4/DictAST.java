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

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class DictAST extends CodeAST
{
	protected static class Type extends AbstractInstanceType
	{
		public Type()
		{
			super("ul4", "DictAST", "de.livinglogic.ul4.dict", "A dictionary \"literal\"..");
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

	public static UL4Type type = new Type();

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

	public Object evaluate(EvaluationContext context)
	{
		Map result = new LinkedHashMap();

		for (DictItemASTBase item : items)
			item.decoratedEvaluateDict(context, result);
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
		items = (List<DictItemASTBase>)decoder.load();
	}
}
