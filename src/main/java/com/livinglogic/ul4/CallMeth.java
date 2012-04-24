/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import com.livinglogic.utils.MapUtils;

public class CallMeth extends AST
{
	protected AST obj;
	protected Method method;
	protected LinkedList<AST> args = new LinkedList<AST>();

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
			"weekday", new MethodWeekday(),
			"yearday", new MethodYearday(),
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
			"replace", new MethodReplace()
		);
	}

	public CallMeth(Location location, AST obj, Method method)
	{
		super(location);
		this.obj = obj;
		this.method = method;
	}

	public CallMeth(Location location, AST obj, String methname)
	{
		super(location);
		this.obj = obj;
		method = methods.get(methname);
		if (method == null)
			throw new UnknownMethodException(methname);
	}

	public void append(AST arg)
	{
		args.add(arg);
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("callmeth(");
		buffer.append(obj);
		buffer.append(", ");
		buffer.append(Utils.repr(method.getName()));
		for (AST arg : args)
		{
			buffer.append(", ");
			buffer.append(arg);
		}
		buffer.append(")");
		return buffer.toString();
	}

	public String getType()
	{
		return "callmeth";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Object obj = this.obj.evaluate(context);

		Object[] realArgs = new Object[args.size()];

		for (int i = 0; i < realArgs.length; ++i)
			realArgs[i] = args.get(i).evaluate(context);
		return method.call(context, obj, realArgs);
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("obj", new ValueMaker(){public Object getValue(Object object){return ((CallMeth)object).obj;}});
			v.put("methname", new ValueMaker(){public Object getValue(Object object){return ((CallMeth)object).method.getName();}});
			v.put("args", new ValueMaker(){public Object getValue(Object object){return ((CallMeth)object).args;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
