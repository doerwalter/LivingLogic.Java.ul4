/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class GenericProto extends Proto
{
	private Class clazz;

	public GenericProto(Class clazz)
	{
		this.clazz = clazz;
	}

	public String name()
	{
		return clazz.getName();
	}

	public boolean bool(Object object)
	{
		if (object == null)
			return false;
		else if (object instanceof UL4Bool)
			return bool((UL4Bool)object);
		else
			return true;
	}

	public static boolean bool(UL4Bool object)
	{
		return object.boolUL4();
	}

	public int len(Object object)
	{
		if (object instanceof UL4Len)
			return len((UL4Len)object);
		else
			return super.len(object);
	}

	public static int len(UL4Len object)
	{
		return object.lenUL4();
	}
}
