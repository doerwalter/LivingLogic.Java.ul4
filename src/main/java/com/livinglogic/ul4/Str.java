/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;
import java.math.BigInteger;

import static com.livinglogic.utils.SetUtils.makeSet;


public class Str extends AbstractType
{
	@Override
	public String getNameUL4()
	{
		return "str";
	}

	@Override
	public String getDoc()
	{
		return "A string";
	}

	private static final Signature signature = new Signature().addPositionalOnly("obj", "");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object create(EvaluationContext context, BoundArguments arguments)
	{
		return call(context, arguments.get(0));
	}

	public static String call(EvaluationContext context, Object object)
	{
		UL4Type type = UL4Type.getType(object);
		return type.strInstance(context, object);
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof String;
	}

	@Override
	public boolean boolInstance(EvaluationContext context, Object instance)
	{
		return !((String)instance).isEmpty();
	}

	@Override
	public Number intInstance(EvaluationContext context, Object instance)
	{
		try
		{
			return Integer.valueOf((String)instance);
		}
		catch (NumberFormatException ex1)
		{
			try
			{
				return Long.valueOf((String)instance);
			}
			catch (NumberFormatException ex2)
			{
				return new BigInteger((String)instance);
			}
		}
	}

	@Override
	public Number floatInstance(EvaluationContext context, Object instance)
	{
		return Double.valueOf((String)instance);
	}

	@Override
	public int lenInstance(EvaluationContext context, Object instance)
	{
		return ((String)instance).length();
	}

	@Override
	public String strInstance(EvaluationContext context, Object instance)
	{
		return (String)instance;
	}

	protected static Set<String> attributes = makeSet(
		"split",
		"rsplit",
		"splitlines",
		"strip",
		"lstrip",
		"rstrip",
		"upper",
		"lower",
		"capitalize",
		"startswith",
		"endswith",
		"replace",
		"count",
		"find",
		"rfind",
		"join"
	);

	@Override
	public Set<String> dirInstance(EvaluationContext context, Object instance)
	{
		return attributes;
	}

	@Override
	public Object getAttr(EvaluationContext context, Object object, String key)
	{
		String string = (String)object;

		switch (key)
		{
			case "split":
				return new BoundStringMethodSplit(string);
			case "rsplit":
				return new BoundStringMethodRSplit(string);
			case "splitlines":
				return new BoundStringMethodSplitlines(string);
			case "strip":
				return new BoundStringMethodStrip(string);
			case "lstrip":
				return new BoundStringMethodLStrip(string);
			case "rstrip":
				return new BoundStringMethodRStrip(string);
			case "upper":
				return new BoundStringMethodUpper(string);
			case "lower":
				return new BoundStringMethodLower(string);
			case "capitalize":
				return new BoundStringMethodCapitalize(string);
			case "startswith":
				return new BoundStringMethodStartsWith(string);
			case "endswith":
				return new BoundStringMethodEndsWith(string);
			case "replace":
				return new BoundStringMethodReplace(string);
			case "count":
				return new BoundStringMethodCount(string);
			case "find":
				return new BoundStringMethodFind(string);
			case "rfind":
				return new BoundStringMethodRFind(string);
			case "join":
				return new BoundStringMethodJoin(string);
			default:
				return super.getAttr(context, object, key);
		}
	}

	public static final Str type = new Str();
}
