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

public class CallFunc extends Callable
{
	protected AST obj;

	public CallFunc(Location location, int start, int end, AST obj)
	{
		super(location, start, end);
		this.obj = obj;
	}

	public String getType()
	{
		return "callfunc";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Object realobj = obj.decoratedEvaluate(context);

		Object[] realArgs;
		if (remainingArgs != null)
		{
			Object realRemainingArgs = remainingArgs.decoratedEvaluate(context);
			if (!(realRemainingArgs instanceof List))
				throw new RemainingArgumentsException(realobj);

			int argsSize = args.size();
			realArgs = new Object[argsSize + ((List)realRemainingArgs).size()];

			for (int i = 0; i < argsSize; ++i)
				realArgs[i] = args.get(i).decoratedEvaluate(context);

			for (int i = 0; i < ((List)realRemainingArgs).size(); ++i)
				realArgs[argsSize + i] = ((List)realRemainingArgs).get(i);
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

	public Object call(UL4Callable obj, Object[] args, Map<String, Object> kwargs)
	{
		return obj.callUL4(args, kwargs);
	}

	public Object call(EvaluationContext context, UL4CallableWithContext obj, Object[] args, Map<String, Object> kwargs)
	{
		return obj.callUL4(context, args, kwargs);
	}

	public Object call(EvaluationContext context, Object obj, Object[] args, Map<String, Object> kwargs)
	{
		if (obj instanceof UL4Callable)
			return call((UL4Callable)obj, args, kwargs);
		else if (obj instanceof UL4CallableWithContext)
			return call(context, (UL4CallableWithContext)obj, args, kwargs);
		throw new NotCallableException(obj);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
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
			v.put("obj", new ValueMaker(){public Object getValue(Object object){return ((CallFunc)object).obj;}});
			v.put("args", new ValueMaker(){public Object getValue(Object object){return ((CallFunc)object).args;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
