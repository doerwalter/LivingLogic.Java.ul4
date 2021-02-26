/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeSet;

import java.util.Set;
import java.util.Collections;


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
	public boolean boolInstance(Object instance)
	{
		if (instance instanceof UL4Bool)
			return ((UL4Bool)instance).boolUL4();
		return true;
	}

	@Override
	public int lenInstance(Object instance)
	{
		if (instance instanceof UL4Len)
			return ((UL4Len)instance).lenUL4();
		throw new ArgumentTypeMismatchException("len({!t}) not supported", instance);
	}

	@Override
	public Set<String> dirInstance(EvaluationContext context, Object instance)
	{
		if (instance instanceof UL4Dir)
			return ((UL4Dir)instance).dirUL4(context);
		else
			return Collections.EMPTY_SET;
	}

	@Override
	public Set<String> dirInstance(Object instance)
	{
		if (instance instanceof UL4Dir)
			return ((UL4Dir)instance).dirUL4();
		else
			return Collections.EMPTY_SET;
	}

	@Override
	public Object getAttr(EvaluationContext context, Object object, String key)
	{
		if (object instanceof UL4GetAttr)
			return ((UL4GetAttr)object).getAttrUL4(context, key);
		else
			return getAttr(object, key);
	}

	@Override
	public Object getAttr(Object object, String key)
	{
		if (object instanceof UL4GetAttr)
			return ((UL4GetAttr)object).getAttrUL4(key);
		else
			throw new AttributeException(object, key);
	}

	@Override
	public void setAttr(EvaluationContext context, Object object, String key, Object value)
	{
		if (object instanceof UL4SetAttr)
			((UL4SetAttr)object).setAttrUL4(context, key, value);
		else
			setAttr(object, key, value);
	}

	@Override
	public void setAttr(Object object, String key, Object value)
	{
		if (object instanceof UL4SetAttr)
			((UL4SetAttr)object).setAttrUL4(key, value);
		else
			throw new ReadonlyException(object, key);
	}
}
