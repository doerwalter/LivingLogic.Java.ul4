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
	protected Method method;
	protected AST obj;

	static Map<String, Method> methods = new HashMap<String, Method>();

	static
	{
		MapUtils.putMap(
			methods,
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
			"r", new MethodR(),
			"g", new MethodG(),
			"b", new MethodB(),
			"a", new MethodA(),
			"hls", new MethodHLS(),
			"hlsa", new MethodHLSA(),
			"hsv", new MethodHSV(),
			"hsva", new MethodHSVA(),
			"lum", new MethodLum(),
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
			"days", new MethodDays(),
			"seconds", new MethodSeconds(),
			"microseconds", new MethodMicroseconds(),
			"months", new MethodMonths(),
			"render", new MethodRender(),
			"renders", new MethodRenderS(),
			"startswith", new MethodStartsWith(),
			"endswith", new MethodEndsWith(),
			"find", new MethodFind(),
			"rfind", new MethodRFind(),
			"get", new MethodGet(),
			"withlum", new MethodWithLum(),
			"witha", new MethodWithA(),
			"join", new MethodJoin(),
			"replace", new MethodReplace(),
			"append", new MethodAppend(),
			"insert", new MethodInsert(),
			"pop", new MethodPop(),
			"update", new MethodUpdate()
		);
	}

	public CallMeth(Location location, int start, int end, AST obj, Method method)
	{
		super(location, start, end);
		this.obj = obj;
		this.method = method;
	}

	public CallMeth(Location location, int start, int end, AST obj, String methname)
	{
		super(location, start, end);
		this.obj = obj;
		method = getMethod(methname);
	}

	public String getType()
	{
		return "callmeth";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Object obj = this.obj.decoratedEvaluate(context);

		Object[] realArgs;
		if (remainingArgs != null)
		{
			Object realRemainingArgs = remainingArgs.decoratedEvaluate(context);
			if (!(realRemainingArgs instanceof List))
				throw new RemainingArgumentsException(method.nameUL4());

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
				throw new RemainingKeywordArgumentsException(method.nameUL4());
			for (Map.Entry<Object, Object> entry : ((Map<Object, Object>)realRemainingKWArgs).entrySet())
			{
				Object argumentName = entry.getKey();
				if (!(argumentName instanceof String))
					throw new RemainingKeywordArgumentsException(method.nameUL4());
				if (realKWArgs.containsKey(argumentName))
					throw new DuplicateArgumentException(method.nameUL4(), (String)argumentName);
				realKWArgs.put((String)argumentName, entry.getValue());
			}
		}

		return method.evaluate(context, obj, realArgs, realKWArgs);
	}

	private static Method getMethod(String methname)
	{
		Method method = methods.get(methname);
		if (method == null)
			throw new UnknownMethodException(methname);
		return method;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(method.nameUL4());
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
		method = getMethod((String)decoder.load());
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
			v.put("methname", new ValueMaker(){public Object getValue(Object object){return ((CallMeth)object).method.nameUL4();}});
			v.put("args", new ValueMaker(){public Object getValue(Object object){return ((CallMeth)object).args;}});
			v.put("kwargs", new ValueMaker(){public Object getValue(Object object){return ((CallMeth)object).kwargs;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
