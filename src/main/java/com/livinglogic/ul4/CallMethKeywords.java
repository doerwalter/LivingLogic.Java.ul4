/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import com.livinglogic.utils.MapUtils;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;

public class CallMethKeywords extends AST
{
	protected AST obj;
	protected KeywordMethod method;
	protected List<CallArg> args = new LinkedList<CallArg>();

	static Map<String, KeywordMethod> methods = new HashMap<String, KeywordMethod>();

	static
	{
		MapUtils.putMap(
			methods,
			"render", new KeywordMethodRender(),
			"renders", new KeywordMethodRenderS()
		);
	}

	public CallMethKeywords(Location location, AST obj, KeywordMethod method)
	{
		super(location);
		this.obj = obj;
		this.method = method;
	}

	public CallMethKeywords(Location location, AST obj, String methname)
	{
		super(location);
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
		buffer.append(FunctionRepr.call(method.getName()));
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
		Object obj = this.obj.decoratedEvaluate(context);

		// make argument dictionary
		HashMap<String, Object> args = new HashMap<String, Object>();

		for (CallArg arg : this.args)
			arg.addTo(context, args);
		return method.evaluate(context, obj, args);
	}

	KeywordMethod getMethod(String methname)
	{
		KeywordMethod method = methods.get(methname);
		if (method == null)
			throw new UnknownMethodException(methname);
		return method;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(method.getName());
		encoder.dump(obj);
		LinkedList argList = new LinkedList();
		for (CallArg arg : args)
			argList.add(arg.object4UL4ON());
		encoder.dump(argList);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		method = getMethod((String)decoder.load());
		obj = (AST)decoder.load();
		List<List> argList = (List<List>)decoder.load();
		args = new LinkedList<CallArg>();
		for (List arg : argList)
		{
			if (arg.size() == 2)
				args.add(new CallArgNamed((String)arg.get(0), (AST)arg.get(1)));
			else
				args.add(new CallArgDict((AST)arg.get(0)));
		}
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
