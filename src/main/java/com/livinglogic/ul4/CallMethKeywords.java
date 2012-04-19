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

public class CallMethKeywords extends AST
{
	protected AST obj;
	protected KeywordMethod method;
	protected LinkedList<CallArg> args = new LinkedList<CallArg>();

	static Map<String, KeywordMethod> methods = new HashMap<String, KeywordMethod>();

	static
	{
		MapUtils.putMap(
			methods,
			"render", new KeywordMethodRender(),
			"renders", new KeywordMethodRenderS()
		);
	}

	public CallMethKeywords(AST obj, KeywordMethod method)
	{
		this.obj = obj;
		this.method = method;
	}

	public CallMethKeywords(AST obj, String methname)
	{
		this.obj = obj;
		method = methods.get(methname);
		if (method == null)
			throw new UnknownMethodException(methname);
	}

	public void append(String argname, AST value)
	{
		args.add(new CallArgNamed(argname, value));
	}

	public void append(AST value)
	{
		args.add(new CallArgDict(value));
	}

	public void append(CallArg arg)
	{
		args.add(arg);
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("callmethkw(");
		buffer.append(obj.toString(indent));
		buffer.append(", ");
		buffer.append(Utils.repr(method.getName()));
		for (CallArg arg : args)
		{
			buffer.append(", ");
			buffer.append(arg);
		}
		buffer.append(")");
		return buffer.toString();
	}

	public String getType()
	{
		return "callmethkw";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Object obj = this.obj.evaluate(context);

		// make argument dictionary
		HashMap<String, Object> args = new HashMap<String, Object>();

		for (CallArg arg : this.args)
			arg.addTo(context, args);
		return method.call(context, obj, args);
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("obj", new ValueMaker(){public Object getValue(Object object){return ((CallMethKeywords)object).obj;}});
			v.put("methname", new ValueMaker(){public Object getValue(Object object){return ((CallMethKeywords)object).method.getName();}});
			v.put("args", new ValueMaker(){public Object getValue(Object object){return ((CallMethKeywords)object).args;}});
			valueMakers = v;
		}
		return valueMakers;
	}}
