/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import static java.util.Arrays.asList;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public abstract class Callable extends AST
{
	protected List<AST> args = new LinkedList<AST>();
	protected List<KeywordArgument> kwargs = new LinkedList<KeywordArgument>();
	protected AST remainingArgs = null;
	protected AST remainingKWArgs = null;

	public Callable(Location location, int start, int end)
	{
		super(location, start, end);
	}

	public void append(AST arg)
	{
		args.add(arg);
	}

	public void append(String name, AST arg)
	{
		kwargs.add(new KeywordArgument(name, arg));
	}

	public void setRemainingArguments(AST arguments)
	{
		remainingArgs = arguments;
	}

	public void setRemainingKeywordArguments(AST arguments)
	{
		remainingKWArgs = arguments;
	}
}
