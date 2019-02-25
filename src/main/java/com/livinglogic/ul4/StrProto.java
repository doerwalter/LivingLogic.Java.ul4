/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.math.BigDecimal;
import java.math.BigInteger;

import static com.livinglogic.utils.SetUtils.makeSet;

public class StrProto extends Proto
{
	public static Proto proto = new StrProto();

	public static String name = "str";

	@Override
	public String name()
	{
		return name;
	}

	@Override
	public boolean bool(Object object)
	{
		return bool((String)object);
	}

	public static boolean bool(String object)
	{
		return object != null && object.length() > 0;
	}

	@Override
	public int len(Object object)
	{
		return len((String)object);
	}

	public static int len(String object)
	{
		return object.length();
	}

	protected static Set<String> attrNames = makeSet("split", "rsplit", "splitlines", "strip", "lstrip", "rstrip", "upper", "lower", "capitalize", "startswith", "endswith", "replace", "count", "find", "rfind", "join");

	@Override
	public Set<String> getAttrNames(Object object)
	{
		return attrNames;
	}

	@Override
	public Object getAttr(Object object, String key)
	{
		return getAttr((String)object, key);
	}

	public static Object getAttr(String object, String key)
	{
		switch (key)
		{
			case "split":
				return new BoundStringMethodSplit(object);
			case "rsplit":
				return new BoundStringMethodRSplit(object);
			case "splitlines":
				return new BoundStringMethodSplitlines(object);
			case "strip":
				return new BoundStringMethodStrip(object);
			case "lstrip":
				return new BoundStringMethodLStrip(object);
			case "rstrip":
				return new BoundStringMethodRStrip(object);
			case "upper":
				return new BoundStringMethodUpper(object);
			case "lower":
				return new BoundStringMethodLower(object);
			case "capitalize":
				return new BoundStringMethodCapitalize(object);
			case "startswith":
				return new BoundStringMethodStartsWith(object);
			case "endswith":
				return new BoundStringMethodEndsWith(object);
			case "replace":
				return new BoundStringMethodReplace(object);
			case "count":
				return new BoundStringMethodCount(object);
			case "find":
				return new BoundStringMethodFind(object);
			case "rfind":
				return new BoundStringMethodRFind(object);
			case "join":
				return new BoundStringMethodJoin(object);
			default:
				throw new AttributeException(object, key);
		}
	}
}
