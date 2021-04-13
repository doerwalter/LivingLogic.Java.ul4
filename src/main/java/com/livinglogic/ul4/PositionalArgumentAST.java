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

public class PositionalArgumentAST extends ArgumentASTBase
{
	protected static class Type extends ArgumentASTBase.Type
	{
		@Override
		public String getNameUL4()
		{
			return "PositionalArgumentAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.posarg";
		}

		@Override
		public String getDoc()
		{
			return "A positional argument.";
		}

		@Override
		public PositionalArgumentAST create(String id)
		{
			return new PositionalArgumentAST(null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof PositionalArgumentAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	AST value;

	public PositionalArgumentAST(Template template, Slice pos, AST value)
	{
		super(template, pos);
		this.value = value;
	}

	public String getType()
	{
		return "posarg";
	}

	public void addToCall(CallRenderAST call)
	{
		for (ArgumentASTBase argument : call.arguments)
		{
			if (argument instanceof KeywordArgumentAST)
				throw new SyntaxException("positional argument follows keyword argument");
			else if (argument instanceof UnpackDictArgumentAST)
				throw new SyntaxException("positional argument follows keyword argument unpacking");
		}
		call.addArgument(this);
	}

	public void evaluateCall(EvaluationContext context, List<Object> arguments, Map<String, Object> keywordArguments)
	{
		arguments.add(value.decoratedEvaluate(context));
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
		reprPosLineCol(formatter);
		formatter.append(" value=");
		formatter.visit(value);
		formatter.append(">");
	}
}
