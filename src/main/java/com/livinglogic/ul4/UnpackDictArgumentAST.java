/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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
	protected static class Type extends ArgumentASTBase.Type
	{
		@Override
		public String getNameUL4()
		{
			return "UnpackDictArgumentAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.unpackdictarg";
		}

		@Override
		public String getDoc()
		{
			return "AST node for an ``**`` unpacking expressions in a :class:`CallAST`\n(e.g. the ``**x`` in ``f(**x)``).";
		}

		@Override
		public UnpackDictArgumentAST create(String id)
		{
			return new UnpackDictArgumentAST(null, -1, -1, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof UnpackDictArgumentAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	AST value;

	public UnpackDictArgumentAST(Template template, int posStart, int posStop, AST value)
	{
		super(template, posStart, posStop);
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
		if (oldValue != null || keywordArguments.containsKey(key))
			throw new DuplicateArgumentException((String)key);

		keywordArguments.put((String)key, value);
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

	protected static Set<String> attributes = makeExtendedSet(ArgumentASTBase.attributes, "value");

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

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		reprPosLineCol(formatter);
		formatter.append(" value=");
		formatter.visit(value);
		formatter.append(">");
	}
}
