/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


public class None extends AbstractType
{
	protected None()
	{
		super(null, "None", null, "The constant `None`.");
	}

	@Override
	public Object create(BoundArguments arguments)
	{
		return null;
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object == null;
	}

	@Override
	public boolean toBool(Object instance)
	{
		return false;
	}

	@Override
	public String toStr(Object instance)
	{
		return "";
	}

	public static UL4Type type = new None();
}
