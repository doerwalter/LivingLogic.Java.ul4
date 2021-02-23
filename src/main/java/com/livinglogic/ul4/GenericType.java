/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeSet;

import java.util.Set;


public class GenericType implements UL4Type
{
	private Class clazz;

	public GenericType(Class clazz)
	{
		this.clazz = clazz;
	}

	private Signature signature = new Signature();

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public String getUL4ONName()
	{
		return null;
	}

	@Override
	public String getNameUL4()
	{
		return clazz.getSimpleName();
	}

	@Override
	public String getFullNameUL4()
	{
		return clazz.getPackage().getName() + "." + getNameUL4();
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return clazz.isInstance(object);
	}

	@Override
	public boolean toBool(Object object)
	{
		if (object instanceof UL4Bool)
			return ((UL4Bool)object).boolUL4();
		return true;
	}

	@Override
	public int len(Object object)
	{
		if (object instanceof UL4Len)
			return ((UL4Len)object).lenUL4();
		throw new ArgumentTypeMismatchException("len({!t}) not supported", object);
	}
}
