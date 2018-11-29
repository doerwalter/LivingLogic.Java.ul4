/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.ul4.Utils.getInnerException;


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
			case "context":
				return getInnerException(object);
			default:
				throw new AttributeException(object, key);
		}
	}
}
