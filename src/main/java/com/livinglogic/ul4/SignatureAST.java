/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Set;
import static java.util.Arrays.asList;
import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class SignatureAST extends CodeAST
{
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "SignatureAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.signature";
		}

		@Override
		public String getDoc()
		{
			return "AST node for the signature of a locally defined subtemplate.";
		}

		@Override
		public SignatureAST create(String id)
		{
			return new SignatureAST(null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof SignatureAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected List<Parameter> parameters;

	public SignatureAST(Template template, Slice pos)
	{
		super(template, pos);
		parameters = new LinkedList<Parameter>();
	}

	public String getType()
	{
		return "signature";
	}

	public void add(String name, ParameterDescription.Type type, AST defaultValue)
	{
		parameters.add(new Parameter(name, type, defaultValue));
	}

	public void toString(Formatter formatter)
	{
		ParameterDescription.Type lastType = null;
		ParameterDescription.Type type = null;
		formatter.write("(");
		for (Parameter param : parameters)
		{
			type = param.getType();
			String sep = ParameterDescription.Type.separator(lastType, type);

			if (sep != null)
				formatter.write(sep);

			if (type == ParameterDescription.Type.VAR_POSITIONAL)
				formatter.write("*");
			else if (type == ParameterDescription.Type.VAR_KEYWORD)
				formatter.write("**");
			formatter.write(param.getName());
			if (type.hasDefault())
			{
				formatter.write("=");
				param.getDefaultValue().toString(formatter);
			}
			lastType = type;
		}
		formatter.write(")");
	}

	public Signature evaluate(EvaluationContext context)
	{
		Signature signature = new Signature();

		for (Parameter param : parameters)
		{
			AST defaultValue = param.getDefaultValue();
			signature.add(param.getName(), param.getType(), defaultValue != null ? defaultValue.decoratedEvaluate(context) : null);
		}

		return signature;
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		List dump = new LinkedList();
		for (Parameter param : parameters)
		{
			dump.add(param.getName());
			ParameterDescription.Type type = param.getType();
			dump.add(type.getUL4ONString());
			if (type.hasDefault())
				dump.add(param.getDefaultValue());
		}
		encoder.dump(dump);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		List dump = (List)decoder.load();

		int state = 0;
		String name = null;
		ParameterDescription.Type type = null;
		for (Object item : dump)
		{
			if (state == 0)
			{
				name = (String)item;
				state = 1;
			}
			else if (state == 1)
			{
				type = ParameterDescription.Type.fromUL4ONString((String)item);
				if (type.hasDefault())
					state = 2;
				else
				{
					add(name, type, null);
					state = 0;
				}
			}
			else
			{
				add(name, type, (AST)item);
				state = 0;
			}
		}
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "params");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "params":
				return parameters;
			default:
				return super.getAttrUL4(key);
		}
	}
}
