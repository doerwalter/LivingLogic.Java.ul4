/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


import java.util.Set;
import java.util.Collections;


public class GenericType extends AbstractType
{
	private Class clazz;

	public GenericType(Class clazz)
	{
		this.clazz = clazz;
	}

	@Override
	public String getUL4ONName()
	{
		return null;
	}

	@Override
	public String getModuleName()
	{
		return clazz.getPackage().getName();
	}

	@Override
	public String getNameUL4()
	{
		return clazz.getSimpleName();
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return clazz.isInstance(object);
	}

	@Override
	public boolean boolInstance(EvaluationContext context, Object instance)
	{
		if (instance instanceof UL4Bool)
			return ((UL4Bool)instance).boolUL4(context);
		return true;
	}

	@Override
	public int lenInstance(EvaluationContext context, Object instance)
	{
		if (instance instanceof UL4Len)
			return ((UL4Len)instance).lenUL4(context);
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
	public Object getAttr(EvaluationContext context, Object object, String key)
	{
		if (object instanceof UL4GetAttr)
			return ((UL4GetAttr)object).getAttrUL4(context, key);
		else
			return new UndefinedAttribute(object, key);
	}

	@Override
	public void setAttr(EvaluationContext context, Object object, String key, Object value)
	{
		if (object instanceof UL4SetAttr)
			((UL4SetAttr)object).setAttrUL4(context, key, value);
		else
			throw new ReadonlyException(object, key);
	}
}
