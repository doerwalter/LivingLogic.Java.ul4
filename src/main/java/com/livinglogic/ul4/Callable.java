/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeSet;
import static com.livinglogic.utils.SetUtils.union;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class Callable extends AST
{
	protected List<AST> arguments = new LinkedList<AST>();
	protected List<KeywordArgument> keywordArguments = new LinkedList<KeywordArgument>();
	protected AST remainingArguments = null;
	protected AST remainingKeywordArguments = null;

	public Callable(Location location, int start, int end)
	{
		super(location, start, end);
	}

	public void append(AST arg)
	{
		arguments.add(arg);
	}

	public void append(String name, AST arg)
	{
		keywordArguments.add(new KeywordArgument(name, arg));
	}

	public void setRemainingArguments(AST arguments)
	{
		remainingArguments = arguments;
	}

	public void setRemainingKeywordArguments(AST arguments)
	{
		remainingKeywordArguments = arguments;
	}

	protected static Set<String> attributes = union(AST.attributes, makeSet("args", "kwargs", "remargs", "remkwargs"));

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("args".equals(key))
			return arguments;
		else if ("kwargs".equals(key))
			return keywordArguments;
		else if ("remargs".equals(key))
			return remainingArguments;
		else if ("remkwargs".equals(key))
			return remainingKeywordArguments;
		else
			return super.getItemStringUL4(key);
	}
}
