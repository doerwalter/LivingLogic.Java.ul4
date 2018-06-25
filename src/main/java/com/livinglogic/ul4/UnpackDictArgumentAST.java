/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.io.IOException;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class UnpackDictArgumentAST extends ArgumentASTBase
{
	AST value;

	public UnpackDictArgumentAST(Tag tag, Slice pos, AST value)
	{
		super(tag, pos);
		this.value = value;
	}

	public String getType()
	{
		return "unpackdictarg";
	}

	public void addToCall(CallRenderAST call)
	{
		call.addArgument(this);
	}

	public void evaluateCall(EvaluationContext context, List<Object> arguments, Map<String, Object> keywordArguments)
	{
		Object item = value.decoratedEvaluate(context);

		if (item instanceof Map)
		{
			Set<Map.Entry> entrySet = ((Map)item).entrySet();
			for (Map.Entry entry : entrySet)
			{
				Object key = entry.getKey();
				Object value = entry.getValue();
				putKeywordArgument(keywordArguments, key, value);
			}
		}
		else
		{
			String exceptionMessage = "expressions for ** unpacking must evaluate to dicts or iterables of (key, value) pairs";

			for (Iterator iter = Utils.iterator(item); iter.hasNext();)
			{
				Object pair = iter.next();

				if (pair instanceof List && ((List)pair).size()==2)
				{
					Object key = ((List)pair).get(0);
					Object value = ((List)pair).get(1);
					putKeywordArgument(keywordArguments, key, value);
				}
				else
					throw new ArgumentException(exceptionMessage);
			}
		}
	}

	private void putKeywordArgument(Map<String, Object> keywordArguments, Object key, Object value)
	{
		String exceptionMessage = "expressions for ** unpacking must evaluate to dicts or iterables of (key, value) pairs";

		if (!(key instanceof String))
			throw new ArgumentException(exceptionMessage);

		Object oldValue = keywordArguments.get(key);
		if (oldValue != null && keywordArguments.containsKey(key))
			throw new DuplicateArgumentException((String)key);

		keywordArguments.put((String)key, value);
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

	protected static Set<String> attributes = makeExtendedSet(ArgumentASTBase.attributes, "value");

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

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" value=");
		formatter.visit(value);
		formatter.append(">");
	}
}
