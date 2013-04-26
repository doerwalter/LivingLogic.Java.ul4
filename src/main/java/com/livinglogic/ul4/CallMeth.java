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
import java.util.LinkedHashMap;
import static java.util.Arrays.asList;

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

		Object[] realArgs;
		if (remainingArgs != null)
		{
			Object realRemainingArgs = remainingArgs.decoratedEvaluate(context);
			if (!(realRemainingArgs instanceof List))
				throw new RemainingArgumentsException(methodName);

			realArgs = new Object[args.size() + remainingArgs.size()];

			for (int i = 0; i < realArgs.length; ++i)
				realArgs[i] = args.get(i).decoratedEvaluate(context);

			for (int i = 0; i < ((List)realRemainingArgs).size(); ++i)
				realArgs[args.size() + i] = ((List)realRemainingArgs).get(i);
		}
		else
		{
			realArgs = new Object[args.size()];

			for (int i = 0; i < realArgs.length; ++i)
				realArgs[i] = args.get(i).decoratedEvaluate(context);
		}

		Map<String, Object> realKWArgs = new LinkedHashMap<String, Object>();

		for (KeywordArgument arg : kwargs)
			realKWArgs.put(arg.getName(), arg.getArg().decoratedEvaluate(context));

		if (remainingKWArgs != null)
		{
			Object realRemainingKWArgs = remainingKWArgs.decoratedEvaluate(context);
			if (!(realRemainingKWArgs instanceof Map))
				throw new RemainingKeywordArgumentsException(methodName);
			for (Map.Entry<Object, Object> entry : ((Map<Object, Object>)realRemainingKWArgs).entrySet())
			{
				Object argumentName = entry.getKey();
				if (!(argumentName instanceof String))
					throw new RemainingKeywordArgumentsException(methodName);
				if (realKWArgs.containsKey(argumentName))
					throw new DuplicateArgumentException(methodName, (String)argumentName);
				realKWArgs.put((String)argumentName, entry.getValue());
			}
		}

		if (obj instanceof UL4MethodCall)
			return ((UL4MethodCall)obj).callMethodUL4(methodName, realArgs, realKWArgs);
		else if (obj instanceof UL4MethodCallWithContext)
			return ((UL4MethodCallWithContext)obj).callMethodUL4(context, methodName, realArgs, realKWArgs);
		else
			return getBuiltinMethod(methodName).evaluate(context, obj, realArgs, realKWArgs);
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
		encoder.dump(args);
		List kwargList = new LinkedList();
		for (KeywordArgument arg : kwargs)
			kwargList.add(asList(arg.getName(), arg.getArg()));
		encoder.dump(kwargList);
		encoder.dump(remainingArgs);
		encoder.dump(remainingKWArgs);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		methodName = (String)decoder.load();
		obj = (AST)decoder.load();
		args = (List<AST>)decoder.load();
		List<List> kwargList = (List<List>)decoder.load();
		for (List arg : kwargList)
			append((String)arg.get(0), (AST)arg.get(1));
		remainingArgs = (AST)decoder.load();
		remainingKWArgs = (AST)decoder.load();
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("obj", new ValueMaker(){public Object getValue(Object object){return ((CallMeth)object).obj;}});
			v.put("methname", new ValueMaker(){public Object getValue(Object object){return ((CallMeth)object).methodName;}});
			v.put("args", new ValueMaker(){public Object getValue(Object object){return ((CallMeth)object).args;}});
			v.put("kwargs", new ValueMaker(){public Object getValue(Object object){return ((CallMeth)object).kwargs;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
