/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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
	protected static class Type extends AbstractInstanceType
	{
		public Type()
		{
			super("ul4", "UnpackDictItemAST", "de.livinglogic.ul4.unpackdictitem", "An item in a dictionary unpacking another dictionary.");
		}

		@Override
		public UnpackDictItemAST create(String id)
		{
			return new UnpackDictItemAST(null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof UnpackDictItemAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected AST item;

	public UnpackDictItemAST(Template template, Slice pos, AST item)
	{
		super(template, pos);
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

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(item);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		item = (AST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(SeqItemASTBase.attributes, "item");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "item":
				return item;
			default:
				return super.getAttrUL4(key);
		}
	}
}
