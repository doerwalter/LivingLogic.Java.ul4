/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
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

public class ExceptionProto extends GenericProto
{
	public ExceptionProto(Class clazz)
	{
		super(clazz);
	}

	@Override
	public Object getAttr(Object object, String key)
	{
		if (object instanceof UL4GetAttr)
			return getAttr((UL4GetAttr)object, key);
		else
			return getAttr((Throwable)object, key);
	}

	public static Object getAttr(UL4GetAttr object, String key)
	{
		return object.getAttrUL4(key);
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
