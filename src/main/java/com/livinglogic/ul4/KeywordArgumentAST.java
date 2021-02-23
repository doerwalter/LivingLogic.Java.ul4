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
			return "A keyword argument.";
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

	public static UL4Type type = new Type();

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

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(name);
		encoder.dump(value);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		name = (String)decoder.load();
		value = (AST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(ArgumentASTBase.attributes, "name", "value");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "name":
				return name;
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
		reprPosLineCol(formatter);
		formatter.append(" name=");
		formatter.visit(name);
		formatter.append(" value=");
		formatter.visit(value);
		formatter.append(">");
	}
}
