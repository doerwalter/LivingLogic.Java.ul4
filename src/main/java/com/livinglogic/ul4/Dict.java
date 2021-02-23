/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;


public class Dict extends AbstractType
{
	protected Dict()
	{
		super(null, "dict", null, "An object that maps keys to values.");
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof Map;
	}

	@Override
	public boolean toBool(Object object)
	{
		return !((Map)object).isEmpty();
	}

	public static UL4Type type = new Dict();
}
