/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class UnpackDictItemAST extends DictItemASTBase
{
	protected AST item;

	public UnpackDictItemAST(Tag tag, int start, int end, AST item)
	{
		super(tag, start, end);
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
}
