/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;


public class GenericProto extends Proto
{
	private Class clazz;

	public GenericProto(Class clazz)
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

	@Override
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

	private Set<String> emptySet = Collections.unmodifiableSet(new HashSet<String>());

	public Set<String> getAttrNames(EvaluationContext context, UL4Dir object)
	{
		return object.dirUL4();
	}

	@Override
	public Set<String> getAttrNames(EvaluationContext context, Object object)
	{
		if (object instanceof UL4Dir)
			return getAttrNames(context, (UL4Dir)object);
		else
			return emptySet;
	}

	@Override
	public Object getAttr(EvaluationContext context, Object object, String key)
	{
		if (object instanceof UL4GetAttrWithContext)
			return getAttr(context, (UL4GetAttrWithContext)object, key);
		else if (object instanceof UL4GetAttr)
			return getAttr((UL4GetAttr)object, key);
		else
			return getAttr(object, key);
	}

	@Override
	public Object getAttr(Object object, String key)
	{
		if (object instanceof UL4GetAttr)
			return getAttr((UL4GetAttr)object, key);
		else
			throw new AttributeException(object, key);
	}

	public static Object getAttr(EvaluationContext context, UL4GetAttrWithContext object, String key)
	{
		return object.getAttrWithContextUL4(context, key);
	}

	public static Object getAttr(UL4GetAttr object, String key)
	{
		return object.getAttrUL4(key);
	}

	@Override
	public void setAttr(EvaluationContext context, Object object, String key, Object value)
	{
		if (object instanceof UL4SetAttrWithContext)
			setAttr(context, (UL4SetAttrWithContext)object, key, value);
		else if (object instanceof UL4SetAttr)
			setAttr((UL4SetAttr)object, key, value);
		else
			setAttr(object, key, value);
	}

	@Override
	public void setAttr(Object object, String key, Object value)
	{
		if (object instanceof UL4SetAttr)
			setAttr((UL4SetAttr)object, key, value);
		else
			throw new AttributeException(object, key);
	}

	public static void setAttr(EvaluationContext context, UL4SetAttrWithContext object, String key, Object value)
	{
		object.setAttrWithContextUL4(context, key, value);
	}

	public static void setAttr(UL4SetAttr object, String key, Object value)
	{
		object.setAttrUL4(key, value);
	}
}
