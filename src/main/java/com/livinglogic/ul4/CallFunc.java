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

public class CallFunc extends AST
{
	protected Function function;
	protected List<AST> args = new LinkedList<AST>();
	protected List<KeywordArgument> kwargs = new LinkedList<KeywordArgument>();
	protected AST remainingArgs = null;
	protected AST remainingKWArgs = null;

	private static Map<String, Function> functions = new HashMap<String, Function>();

	static
	{
		MapUtils.putMap(
			functions,
			"now", new FunctionNow(),
			"utcnow", new FunctionUTCNow(),
			"date", new FunctionDate(),
			"timedelta", new FunctionTimeDelta(),
			"monthdelta", new FunctionMonthDelta(),
			"vars", new FunctionVars(),
			"allvars", new FunctionAllVars(),
			"random", new FunctionRandom(),
			"xmlescape", new FunctionXMLEscape(),
			"csv", new FunctionCSV(),
			"str", new FunctionStr(),
			"repr", new FunctionRepr(),
			"int", new FunctionInt(),
			"float", new FunctionFloat(),
			"bool", new FunctionBool(),
			"len", new FunctionLen(),
			"any", new FunctionAny(),
			"all", new FunctionAll(),
			"enumerate", new FunctionEnumerate(),
			"enumfl", new FunctionEnumFL(),
			"isfirstlast", new FunctionIsFirstLast(),
			"isfirst", new FunctionIsFirst(),
			"islast", new FunctionIsLast(),
			"isundefined", new FunctionIsUndefined(),
			"isdefined", new FunctionIsDefined(),
			"isnone", new FunctionIsNone(),
			"isstr", new FunctionIsStr(),
			"isint", new FunctionIsInt(),
			"isfloat", new FunctionIsFloat(),
			"isbool", new FunctionIsBool(),
			"isdate", new FunctionIsDate(),
			"islist", new FunctionIsList(),
			"isdict", new FunctionIsDict(),
			"istemplate", new FunctionIsTemplate(),
			"iscolor", new FunctionIsColor(),
			"istimedelta", new FunctionIsTimeDelta(),
			"ismonthdelta", new FunctionIsMonthDelta(),
			"chr", new FunctionChr(),
			"ord", new FunctionOrd(),
			"hex", new FunctionHex(),
			"oct", new FunctionOct(),
			"bin", new FunctionBin(),
			"abs", new FunctionAbs(),
			"range", new FunctionRange(),
			"min", new FunctionMin(),
			"max", new FunctionMax(),
			"sorted", new FunctionSorted(),
			"type", new FunctionType(),
			"get", new FunctionGet(),
			"asjson", new FunctionAsJSON(),
			"fromjson", new FunctionFromJSON(),
			"asul4on", new FunctionAsUL4ON(),
			"fromul4on", new FunctionFromUL4ON(),
			"reversed", new FunctionReversed(),
			"randrange", new FunctionRandRange(),
			"randchoice", new FunctionRandChoice(),
			"format", new FunctionFormat(),
			"urlquote", new FunctionURLQuote(),
			"urlunquote", new FunctionURLUnquote(),
			"zip", new FunctionZip(),
			"rgb", new FunctionRGB(),
			"hls", new FunctionHLS(),
			"hsv", new FunctionHSV()
		);
	}

	public CallFunc(Function function)
	{
		super();
		this.function = function;
	}

	public CallFunc(String funcname)
	{
		super();
		function = getFunction(funcname);
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

	public String toString(int indent)
	{
		StringBuilder buffer = new StringBuilder();

		buffer.append("callfunc(");
		buffer.append(FunctionRepr.call(function.getName()));
		for (AST arg : args)
		{
			buffer.append(", ");
			buffer.append(arg);
		}
		for (KeywordArgument arg : kwargs)
		{
			buffer.append(", ");
			buffer.append(arg.getName());
			buffer.append("=");
			buffer.append(arg.getArg());
		}
		if (remainingArgs != null)
		{
			buffer.append(", *");
			buffer.append(remainingArgs);
		}
		if (remainingKWArgs != null)
		{
			buffer.append(", **");
			buffer.append(remainingKWArgs);
		}
		buffer.append(")");
		return buffer.toString();
	}

	public String getType()
	{
		return "callfunc";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Object[] realArgs;
		if (remainingArgs != null)
		{
			Object realRemainingArgs = remainingArgs.decoratedEvaluate(context);
			if (!(realRemainingArgs instanceof List))
				throw new RemainingArgumentsException(function.getName());

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
				throw new RemainingKeywordArgumentsException(function.getName());
			for (Map.Entry<Object, Object> entry : ((Map<Object, Object>)realRemainingKWArgs).entrySet())
			{
				Object argumentName = entry.getKey();
				if (!(argumentName instanceof String))
					throw new RemainingKeywordArgumentsException(function.getName());
				if (realKWArgs.containsKey(argumentName))
					throw new DuplicateArgumentException(function.getName(), (String)argumentName);
				realKWArgs.put((String)argumentName, entry.getValue());
			}
		}

		return function.evaluate(context, realArgs, realKWArgs);
	}

	private static Function getFunction(String funcname)
	{
		Function function = functions.get(funcname);
		if (function == null)
			throw new UnknownFunctionException(funcname);
		return function;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(function.getName());
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
		function = getFunction((String)decoder.load());
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
			v.put("funcname", new ValueMaker(){public Object getValue(Object object){return ((CallFunc)object).function.getName();}});
			v.put("args", new ValueMaker(){public Object getValue(Object object){return ((CallFunc)object).args;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
