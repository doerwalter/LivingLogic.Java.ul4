/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.LinkedList;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.livinglogic.utils.MapUtils;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;

public class CallFunc extends AST
{
	protected Function function;
	protected List<AST> args = new LinkedList<AST>();

	private static Map<String, Function> functions = new HashMap<String, Function>();

	static
	{
		MapUtils.putMap(
			functions,
			"now", new FunctionNow(),
			"utcnow", new FunctionUTCNow(),
			"vars", new FunctionVars(),
			"random", new FunctionRandom(),
			"xmlescape", new FunctionXMLEscape(),
			"csv", new FunctionCSV(),
			"str", new FunctionStr(),
			"repr", new FunctionRepr(),
			"int", new FunctionInt(),
			"float", new FunctionFloat(),
			"bool", new FunctionBool(),
			"len", new FunctionLen(),
			"enumerate", new FunctionEnumerate(),
			"enumfl", new FunctionEnumFL(),
			"isfirstlast", new FunctionIsFirstLast(),
			"isfirst", new FunctionIsFirst(),
			"islast", new FunctionIsLast(),
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

	public CallFunc(Location location, Function function)
	{
		super(location);
		this.function = function;
	}

	public CallFunc(Location location, String funcname)
	{
		super(location);
		function = getFunction(funcname);
	}

	public void append(AST arg)
	{
		args.add(arg);
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("callfunc(");
		buffer.append(FunctionRepr.call(function.getName()));
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
		return "callfunc";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		if (args.size() == 0)
			return function.evaluate(context); // otherwise evaluate() would be called with one argument (being an empty array)

		Object[] realArgs = new Object[args.size()];

		for (int i = 0; i < realArgs.length; ++i)
			realArgs[i] = args.get(i).decoratedEvaluate(context);
		return function.evaluate(context, realArgs);
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
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		function = getFunction((String)decoder.load());
		args = (List<AST>)decoder.load();
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
