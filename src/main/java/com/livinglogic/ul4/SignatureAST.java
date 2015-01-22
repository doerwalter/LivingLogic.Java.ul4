/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
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
	protected String remainingParametersName;
	protected String remainingKeywordParametersName;

	public SignatureAST(Tag tag, int start, int end)
	{
		super(tag, start, end);
		parameters = new LinkedList<Parameter>();
		remainingParametersName = null;
		remainingKeywordParametersName = null;
	}

	public String getType()
	{
		return "signature";
	}

	public void add(String name)
	{
		parameters.add(new Parameter(name));
	}

	public void add(String name, AST defaultValue)
	{
		parameters.add(new Parameter(name, defaultValue));
	}

	public void setRemainingArguments(String name)
	{
		remainingParametersName = name;
	}

	public void setRemainingKeywordArguments(String name)
	{
		remainingKeywordParametersName = name;
	}

	public Signature evaluate(EvaluationContext context)
	{
		Signature signature = new Signature();

		for (Parameter param : parameters)
		{
			AST defaultValue = param.getDefaultValue();
			if (defaultValue != null)
				signature.add(param.getName(), defaultValue.decoratedEvaluate(context));
			else
				signature.add(param.getName());
		}
		if (remainingParametersName != null)
			signature.setRemainingParameters(remainingParametersName);
		if (remainingKeywordParametersName != null)
			signature.setRemainingKeywordParameters(remainingKeywordParametersName);

		return signature;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		List paramsDump = new LinkedList();
		for (Parameter param : parameters)
		{
			Object defaultValue = param.getDefaultValue();
			if (defaultValue == null)
				paramsDump.add(param.getName());
			else
				paramsDump.add(asList(param.getName(), defaultValue));
		}
		if (remainingParametersName != null)
			paramsDump.add("*" + remainingParametersName);
		if (remainingKeywordParametersName != null)
			paramsDump.add("**" + remainingKeywordParametersName);
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
					remainingKeywordParametersName = paramString.substring(2);
				else if (paramString.startsWith("*"))
					remainingParametersName = paramString.substring(1);
				else
					add(paramString);
			}
			else
			{
				String name = (String)((List)paramDump).get(0);
				AST defaultValue = (AST)((List)paramDump).get(1);
				add(name, defaultValue);
			}
		}
	}

	protected static Set<String> attributes = makeExtendedSet(AST.attributes, "params");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("params".equals(key))
			return parameters;
		else
			return super.getItemStringUL4(key);
	}
}
