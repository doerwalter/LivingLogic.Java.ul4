/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class BoundStringMethodReplace extends BoundMethod<String>
{
	public BoundStringMethodReplace(String object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "replace";
	}

	private static final Signature signature = new Signature().addPositionalOnly("old").addPositionalOnly("new").addPositionalOnly("count", -1);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static String call(String object, String search, String replace)
	{
		return object.replace(search, replace);
	}

	public static String call(String object, String search, String replace, int count)
	{
		if (count == -1)
			return object.replace(search, replace);
		return StringUtils.replace(object, search, replace, count);
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		Object oldObj = args.get(0);
		Object newObj = args.get(1);
		Object countObj = args.get(2);

		if (!(oldObj instanceof String) || !(newObj instanceof String))
			throw new ArgumentTypeMismatchException("{!t}.replace({!t}, {!t}) not supported", object, oldObj, newObj);

		return call(object, (String)oldObj, (String)newObj, countObj != null ? Utils.toInt(countObj) : -1);
	}
}
