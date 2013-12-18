/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import static java.util.Arrays.asList;
import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class CallAST extends AST
{
	protected AST obj;
	protected List<AST> arguments = new LinkedList<AST>();
	protected List<KeywordArgument> keywordArguments = new LinkedList<KeywordArgument>();
	protected AST remainingArguments = null;
	protected AST remainingKeywordArguments = null;

	public CallAST(Location location, int start, int end, AST obj)
	{
		super(location, start, end);
		this.obj = obj;
	}

	public String getType()
	{
		return "call";
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

	public Object evaluate(EvaluationContext context)
	{
		Object realobj = obj.decoratedEvaluate(context);

		Object[] realArgs;
		if (remainingArguments != null)
		{
			Object realRemainingArguments = remainingArguments.decoratedEvaluate(context);
			if (!(realRemainingArguments instanceof List))
				throw new RemainingArgumentsException(realobj);

			int argsSize = arguments.size();
			realArgs = new Object[argsSize + ((List)realRemainingArguments).size()];

			for (int i = 0; i < argsSize; ++i)
				realArgs[i] = arguments.get(i).decoratedEvaluate(context);

			for (int i = 0; i < ((List)realRemainingArguments).size(); ++i)
				realArgs[argsSize + i] = ((List)realRemainingArguments).get(i);
		}
		else
		{
			realArgs = new Object[arguments.size()];

			for (int i = 0; i < realArgs.length; ++i)
				realArgs[i] = arguments.get(i).decoratedEvaluate(context);
		}

		Map<String, Object> realKWArgs = new LinkedHashMap<String, Object>();

		for (KeywordArgument arg : keywordArguments)
			realKWArgs.put(arg.getName(), arg.getArg().decoratedEvaluate(context));

		if (remainingKeywordArguments != null)
		{
			Object realRemainingKWArgs = remainingKeywordArguments.decoratedEvaluate(context);
			if (!(realRemainingKWArgs instanceof Map))
				throw new RemainingKeywordArgumentsException(realobj);
			for (Map.Entry<Object, Object> entry : ((Map<Object, Object>)realRemainingKWArgs).entrySet())
			{
				Object argumentName = entry.getKey();
				if (!(argumentName instanceof String))
					throw new RemainingKeywordArgumentsException(realobj);
				if (realKWArgs.containsKey(argumentName))
					throw new DuplicateArgumentException(realobj, (String)argumentName);
				realKWArgs.put((String)argumentName, entry.getValue());
			}
		}

		return call(context, realobj, realArgs, realKWArgs);
	}

	public Object call(UL4Call obj, Object[] args, Map<String, Object> kwargs)
	{
		return obj.callUL4(args, kwargs);
	}

	public Object call(EvaluationContext context, UL4CallWithContext obj, Object[] args, Map<String, Object> kwargs)
	{
		return obj.callUL4(context, args, kwargs);
	}

	public Object call(EvaluationContext context, Object obj, Object[] args, Map<String, Object> kwargs)
	{
		if (obj instanceof UL4Call)
			return call((UL4Call)obj, args, kwargs);
		else if (obj instanceof UL4CallWithContext)
			return call(context, (UL4CallWithContext)obj, args, kwargs);
		throw new NotCallableException(obj);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj);
		encoder.dump(arguments);
		List keywordArgumentList = new LinkedList();
		for (KeywordArgument arg : keywordArguments)
			keywordArgumentList.add(asList(arg.getName(), arg.getArg()));
		encoder.dump(keywordArgumentList);
		encoder.dump(remainingArguments);
		encoder.dump(remainingKeywordArguments);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj = (AST)decoder.load();
		arguments = (List<AST>)decoder.load();
		List<List> keywordArgumentList = (List<List>)decoder.load();
		for (List arg : keywordArgumentList)
			append((String)arg.get(0), (AST)arg.get(1));
		remainingArguments = (AST)decoder.load();
		remainingKeywordArguments = (AST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(AST.attributes, "obj", "args", "kwargs", "remargs", "remkwargs");

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
