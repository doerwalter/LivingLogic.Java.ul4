/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.IOException;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class KeywordArgumentAST extends ArgumentASTBase
{
	protected static class Type extends ArgumentASTBase.Type
	{
		@Override
		public String getNameUL4()
		{
			return "KeywordArgumentAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.keywordarg";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a keyword argument in a :class:`CallAST` (e.g. the ``x=y``\nin the function call ``f(x=y)``).";
		}

		@Override
		public KeywordArgumentAST create(String id)
		{
			return new KeywordArgumentAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof KeywordArgumentAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	String name;
	AST value;

	public KeywordArgumentAST(Template template, Slice pos, String name, AST value)
	{
		super(template, pos);
		this.name = name;
		this.value = value;
	}

	public String getType()
	{
		return "keywordarg";
	}

	public void addToCall(CallRenderAST call)
	{
		call.addArgument(this);
	}

	public void evaluateCall(EvaluationContext context, List<Object> arguments, Map<String, Object> keywordArguments)
	{
		Object oldValue = keywordArguments.get(name);
		if (oldValue != null || keywordArguments.containsKey(name))
			throw new DuplicateArgumentException(name);

		keywordArguments.put(name, value.decoratedEvaluate(context));
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(name);
		encoder.dump(value);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		name = ((String)decoder.load()).intern();
		value = (AST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(ArgumentASTBase.attributes, "name", "value");

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
			case "name":
				return name;
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
		formatter.append(" name=");
		formatter.visit(name);
		formatter.append(" value=");
		formatter.visit(value);
		formatter.append(">");
	}
}
