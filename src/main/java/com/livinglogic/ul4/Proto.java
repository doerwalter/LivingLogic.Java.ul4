/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.Map;

import static com.livinglogic.utils.SetUtils.makeSet;


/**
 * Prototype class that implements methods for a class where we can't add those
 * methods to the class itself (because it's a builtin Java class).
 */
public abstract class Proto implements UL4GetAttr, UL4Dir
{
	public abstract String name();

	public abstract boolean bool(Object object);

	public int len(Object object)
	{
		throw new ArgumentTypeMismatchException("len({!t}) not supported", object);
	}

	public Set<String> getAttrNames(EvaluationContext context, Object object)
	{
		return getAttrNames(object);
	}

	public Set<String> getAttrNames(Object object)
	{
		return makeSet();
	}

	public Object getAttr(EvaluationContext context, Object object, String attrname)
	{
		return getAttr(object, attrname);
	}

	public Object getAttr(Object object, String attrname)
	{
		throw new AttributeException(object, attrname);
	}

	public void setAttr(EvaluationContext context, Object object, String attrname, Object value)
	{
		setAttr(object, attrname, value);
	}

	public void setAttr(Object object, String attrname, Object value)
	{
		throw new AttributeException(object, attrname);
	}

	public boolean hasAttr(EvaluationContext context, Object object, String attrname)
	{
		return Proto.get(object).getAttrNames(context, object).contains(attrname);
	}

	public boolean hasAttr(Object object, String attrname)
	{
		return Proto.get(object).getAttrNames(object).contains(attrname);
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
		else if (object instanceof LocalDateTime)
			return LocalDateTimeProto.proto;
		else if (object instanceof LocalDate)
			return LocalDateProto.proto;
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

	public Set<String> dirUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
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
