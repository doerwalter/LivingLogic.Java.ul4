/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
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

/**
 * Common base class of {@see CallAST} and {@see RenderAST}
 */
public abstract class CallRenderAST extends CodeAST
{
	protected AST obj;
	protected List<Argument> arguments = new LinkedList<Argument>();

	public CallRenderAST(Tag tag, int start, int end, AST obj)
	{
		super(tag, start, end);
		this.obj = obj;
	}

	public void appendArgument(AST arg)
	{
		arguments.add(new Argument(arg));
	}

	public void appendKeywordArgument(String name, AST arg)
	{
		arguments.add(new KeywordArgument(name, arg));
	}

	public void appendRemainingArguments(AST arg)
	{
		arguments.add(new RemainingArguments(arg));
	}

	public void appendRemainingKeywordArguments(AST arg)
	{
		arguments.add(new RemainingKeywordArguments(arg));
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj);
		List argumentList = new LinkedList();
		for (Argument arg : arguments)
			argumentList.add(asList(arg.getName(), arg.getArg()));
		encoder.dump(argumentList);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj = (AST)decoder.load();
		List<List> argumentList = (List<List>)decoder.load();
		for (List namearg : argumentList)
		{
			String name = (String)namearg.get(0);
			AST arg = (AST)namearg.get(1);
			if (name == null)
				appendArgument(arg);
			else if (name.equals("*"))
				appendRemainingArguments(arg);
			else if (name.equals("**"))
				appendRemainingKeywordArguments(arg);
			else
				appendKeywordArgument(name, arg);
		}
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "obj", "args");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("obj".equals(key))
			return obj;
		else if ("args".equals(key))
			return arguments;
		else
			return super.getItemStringUL4(key);
	}
}
