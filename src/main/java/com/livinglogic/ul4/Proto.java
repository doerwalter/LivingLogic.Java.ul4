/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Map;

import static com.livinglogic.utils.SetUtils.makeSet;


/**
 * Prototype class that implements methods for a class where we can't add those
 * methods to the class itself (because it's a builtin Java class).
 */
public abstract class Proto implements UL4GetItemString, UL4Attributes
{
	public abstract String name();

	public abstract boolean bool(Object object);

	public int len(Object object)
	{
		throw new ArgumentTypeMismatchException("len({!t}) not supported", object);
	}

	public Object getAttr(EvaluationContext context, Object object, String key)
	{
		return getAttr(object, key);
	}

	public Object getAttr(Object object, String key)
	{
		throw new AttributeException(object, key);
	}

	public static Proto get(Object object)
	{
		if (object == null)
			return NoneProto.proto;
		else if (object instanceof Boolean)
			return BoolProto.proto;
		else if (object instanceof Integer || object instanceof Long || object instanceof BigInteger)
			return IntProto.proto;
		else if (object instanceof Float || object instanceof Double || object instanceof BigDecimal)
			return FloatProto.proto;
		else if (object instanceof String)
			return StrProto.proto;
		else if (object instanceof Date)
			return DateProto.proto;
		else if (object instanceof List || object instanceof Object[])
			return ListProto.proto;
		else if (object instanceof Map)
			return DictProto.proto;
		else if (object instanceof Set)
			return SetProto.proto;
		else if (object instanceof Throwable)
			return new ExceptionProto(object.getClass());
		else
			return new GenericProto(object.getClass());
	}

	protected static Set<String> attributes = makeSet("name");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "name":
				return name();
			default:
				throw new AttributeException(this, key);
		}
	}
}
