/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
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
	protected List<Parameter> parameters;

	public SignatureAST(Tag tag, Slice pos)
	{
		super(tag, pos);
		parameters = new LinkedList<Parameter>();
	}

	public String getType()
	{
		return "signature";
	}

	public void add(String name, ArgumentDescription.Type type, AST defaultValue)
	{
		parameters.add(new Parameter(name, type, defaultValue));
	}

	public void toString(Formatter formatter)
	{
		boolean first = true;
		formatter.write("(");
		for (Parameter param : parameters)
		{
			if (first)
				first = false;
			else
				formatter.write(", ");

			String name = param.getName();
			switch (param.getType())
			{
				case REQUIRED:
					formatter.write(name);
					break;
				case DEFAULT:
					formatter.write(name);
					formatter.write("=");
					param.getDefaultValue().toString(formatter);
					break;
				case VAR_POSITIONAL:
					formatter.write("*");
					formatter.write(name);
					break;
				case VAR_KEYWORD:
					formatter.write("**");
					formatter.write(name);
					break;
			}
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

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		List paramsDump = new LinkedList();
		for (Parameter param : parameters)
		{
			String name = param.getName();
			switch (param.getType())
			{
				case REQUIRED:
					paramsDump.add(name);
					break;
				case DEFAULT:
					paramsDump.add(asList(name, param.getDefaultValue()));
					break;
				case VAR_POSITIONAL:
					paramsDump.add("*" + name);
					break;
				case VAR_KEYWORD:
					paramsDump.add("**" + name);
					break;
			}
		}
		encoder.dump(paramsDump);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		List paramsDump = (List)decoder.load();
		for (Object paramDump : paramsDump)
		{
			if (paramDump instanceof String)
			{
				String paramString = (String)paramDump;

				if (paramString.startsWith("**"))
					add(paramString.substring(2), ArgumentDescription.Type.VAR_KEYWORD, null);
				else if (paramString.startsWith("*"))
					add(paramString.substring(1), ArgumentDescription.Type.VAR_POSITIONAL, null);
				else
					add(paramString, ArgumentDescription.Type.REQUIRED, null);
			}
			else
			{
				String name = (String)((List)paramDump).get(0);
				AST defaultValue = (AST)((List)paramDump).get(1);
				add(name, ArgumentDescription.Type.DEFAULT, defaultValue);
			}
		}
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "params");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "params":
				return parameters;
			default:
				return super.getItemStringUL4(key);
		}
	}
}
