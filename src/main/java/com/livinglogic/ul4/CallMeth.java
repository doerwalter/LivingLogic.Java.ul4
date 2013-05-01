/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;
import static java.util.Arrays.asList;

import static com.livinglogic.utils.SetUtils.makeSet;
import static com.livinglogic.utils.SetUtils.union;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.utils.MapUtils;

public class CallMeth extends Callable
{
	protected String methodName;
	protected AST obj;

	static Map<String, Method> builtinMethods = new HashMap<String, Method>();

	static
	{
		MapUtils.putMap(
			builtinMethods,
			"split", new MethodSplit(),
			"rsplit", new MethodRSplit(),
			"strip", new MethodStrip(),
			"lstrip", new MethodLStrip(),
			"rstrip", new MethodRStrip(),
			"upper", new MethodUpper(),
			"lower", new MethodLower(),
			"capitalize", new MethodCapitalize(),
			"items", new MethodItems(),
			"values", new MethodValues(),
			"isoformat", new MethodISOFormat(),
			"mimeformat", new MethodMIMEFormat(),
			"day", new MethodDay(),
			"month", new MethodMonth(),
			"year", new MethodYear(),
			"hour", new MethodHour(),
			"minute", new MethodMinute(),
			"second", new MethodSecond(),
			"microsecond", new MethodMicrosecond(),
			"week", new MethodWeek(),
			"weekday", new MethodWeekday(),
			"yearday", new MethodYearday(),
			"startswith", new MethodStartsWith(),
			"endswith", new MethodEndsWith(),
			"find", new MethodFind(),
			"rfind", new MethodRFind(),
			"get", new MethodGet(),
			"join", new MethodJoin(),
			"replace", new MethodReplace(),
			"append", new MethodAppend(),
			"insert", new MethodInsert(),
			"pop", new MethodPop(),
			"update", new MethodUpdate()
		);
	}

	public CallMeth(Location location, int start, int end, AST obj, String methodName)
	{
		super(location, start, end);
		this.obj = obj;
		this.methodName = methodName;
	}

	public String getType()
	{
		return "callmeth";
	}

	public Object evaluate(EvaluationContext context)
	{
		Object obj = this.obj.decoratedEvaluate(context);

		Object[] realArguments;
		if (remainingArguments != null)
		{
			Object realRemainingArguments = remainingArguments.decoratedEvaluate(context);
			if (!(realRemainingArguments instanceof List))
				throw new RemainingArgumentsException(methodName);

			realArguments = new Object[arguments.size() + ((List)realRemainingArguments).size()];

			for (int i = 0; i < realArguments.length; ++i)
				realArguments[i] = arguments.get(i).decoratedEvaluate(context);

			for (int i = 0; i < ((List)realRemainingArguments).size(); ++i)
				realArguments[arguments.size() + i] = ((List)realRemainingArguments).get(i);
		}
		else
		{
			realArguments = new Object[arguments.size()];

			for (int i = 0; i < realArguments.length; ++i)
				realArguments[i] = arguments.get(i).decoratedEvaluate(context);
		}

		Map<String, Object> realKeywordArguments = new LinkedHashMap<String, Object>();

		for (KeywordArgument arg : keywordArguments)
			realKeywordArguments.put(arg.getName(), arg.getArg().decoratedEvaluate(context));

		if (remainingKeywordArguments != null)
		{
			Object realRemainingKWArgs = remainingKeywordArguments.decoratedEvaluate(context);
			if (!(realRemainingKWArgs instanceof Map))
				throw new RemainingKeywordArgumentsException(methodName);
			for (Map.Entry<Object, Object> entry : ((Map<Object, Object>)realRemainingKWArgs).entrySet())
			{
				Object argumentName = entry.getKey();
				if (!(argumentName instanceof String))
					throw new RemainingKeywordArgumentsException(methodName);
				if (realKeywordArguments.containsKey(argumentName))
					throw new DuplicateArgumentException(methodName, (String)argumentName);
				realKeywordArguments.put((String)argumentName, entry.getValue());
			}
		}

		if (obj instanceof UL4MethodCall)
			return ((UL4MethodCall)obj).callMethodUL4(methodName, realArguments, realKeywordArguments);
		else if (obj instanceof UL4MethodCallWithContext)
			return ((UL4MethodCallWithContext)obj).callMethodUL4(context, methodName, realArguments, realKeywordArguments);
		else
			return getBuiltinMethod(methodName).evaluate(context, obj, realArguments, realKeywordArguments);
	}

	private static Method getBuiltinMethod(String methodName)
	{
		Method method = builtinMethods.get(methodName);
		if (method == null)
			throw new UnknownMethodException(methodName);
		return method;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(methodName);
		encoder.dump(obj);
		encoder.dump(arguments);
		List kwargList = new LinkedList();
		for (KeywordArgument arg : keywordArguments)
			kwargList.add(asList(arg.getName(), arg.getArg()));
		encoder.dump(kwargList);
		encoder.dump(remainingArguments);
		encoder.dump(remainingKeywordArguments);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		methodName = (String)decoder.load();
		obj = (AST)decoder.load();
		arguments = (List<AST>)decoder.load();
		List<List> keywordArgumentList = (List<List>)decoder.load();
		for (List arg : keywordArgumentList)
			append((String)arg.get(0), (AST)arg.get(1));
		remainingArguments = (AST)decoder.load();
		remainingKeywordArguments = (AST)decoder.load();
	}

	protected static Set<String> attributes = union(Callable.attributes, makeSet("obj", "methname"));

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("obj".equals(key))
			return obj;
		else if ("methname".equals(key))
			return methodName;
		else
			return super.getItemStringUL4(key);
	}
}
