/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;


public class OperatorAttrGetter extends Function
{
	private List<String[]> allAttrNames;

	public OperatorAttrGetter(String... attrNames)
	{
		allAttrNames = new ArrayList<String[]>();
		for (String attrName : attrNames)
			allAttrNames.add(StringUtils.splitByWholeSeparatorPreserveAllTokens(attrName, "."));
	}

	@Override
	public String getNameUL4()
	{
		return "attrgetter";
	}

	private static final Signature signature = new Signature().addPositionalOnly("obj");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0), allAttrNames);
	}

	private static Object call(Object obj, List<String[]> allAttrNames)
	{
		if (allAttrNames.size() == 1)
			return call(obj, allAttrNames.get(0));
		else
		{
			List<Object> result = new ArrayList<Object>(allAttrNames.size());
			for (String[] attrNames : allAttrNames)
				result.add(call(obj, attrNames));
			return result;
		}
	}

	private static Object call(Object obj, String attrName)
	{
		return UL4Type.getType(obj).getAttr(obj, attrName);
	}

	private static Object call(Object obj, String[] attrNames)
	{
		for (String attrName : attrNames)
			obj = call(obj, attrName);
		return obj;
	}
}
