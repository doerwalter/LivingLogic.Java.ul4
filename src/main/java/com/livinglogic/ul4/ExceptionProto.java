/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
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

public class ExceptionProto extends Proto
{
	private Class clazz;

	public ExceptionProto(Class clazz)
	{
		this.clazz = clazz;
	}

	@Override
	public String name()
	{
		return clazz.getName();
	}

	@Override
	public boolean bool(Object object)
	{
		return object != null;
	}

	@Override
	public Object getAttr(Object object, String key)
	{
		return getAttr((Throwable)object, key);
	}

	public static Object getAttr(Throwable object, String key)
	{
		switch (key)
		{
			case "cause":
				return object.getCause();
			default:
				throw new AttributeException(object, key);
		}
	}
}
