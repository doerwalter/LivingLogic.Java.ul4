/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeSet;

import java.util.Set;


public class ExceptionType extends GenericType
{
	public ExceptionType(Class clazz)
	{
		super(clazz);
	}

	@Override
	public String toStr(Object object)
	{
		String message = ((Throwable)object).getLocalizedMessage();
		return message != null ? message : "";
	}
}
