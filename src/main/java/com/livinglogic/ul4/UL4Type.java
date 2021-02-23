/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.livinglogic.ul4on.ObjectFactory;
import com.livinglogic.ul4on.UL4ONSerializable;


public interface UL4Type extends UL4Name, UL4Repr, UL4Call, ObjectFactory
{
	String getUL4ONName();

	boolean toBool(Object object);

	default Number toInt(Object object)
	{
		throw new ArgumentTypeMismatchException("Can't convert {!t} to int!", object);
	}

	default Number toFloat(Object object)
	{
		throw new ArgumentTypeMismatchException("Can't convert {!t} to float!", object);
	}

	default String toStr(Object object)
	{
		return object.toString();
	}

	default int len(Object object)
	{
		throw new ArgumentTypeMismatchException("len({!t}) not supported!", object);
	}

	public static Map<Class, UL4Type> genericTypes = new HashMap<Class, UL4Type>();

	public static UL4Type getType(Object object)
	{
		if (object == null)
			return None.type;
		else if (object instanceof UL4Instance)
			return ((UL4Instance)object).getTypeUL4();
		else if (object instanceof Boolean)
			return Bool.type;
		else if (object instanceof Byte || object instanceof Short || object instanceof Integer || object instanceof Long || object instanceof BigInteger)
			return Int.type;
		else if (object instanceof Float || object instanceof Double || object instanceof BigDecimal)
			return Float_.type;
		else if (object instanceof String)
			return Str.type;
		else if (object instanceof LocalDate)
			return Date_.type;
		else if (object instanceof Date)
			return DateTime.type;
		else if (object instanceof LocalDateTime)
			return DateTime.type;
		else if (object instanceof List || object instanceof Object[])
			return List_.type;
		else if (object instanceof Map)
			return Dict.type;
		else if (object instanceof Set)
			return Set_.type;
		else
		{
			Class clazz = object.getClass();
			UL4Type type = genericTypes.get(clazz);
			if (type == null)
			{
				type = object instanceof Throwable ? new ExceptionType(clazz) : new GenericType(clazz);
				genericTypes.put(clazz, type);
			}
			return type;
		}
	}

	default UL4ONSerializable create(String id)
	{
		throw new UnsupportedOperationException(Utils.formatMessage("Can't create {!r} instances from UL4ON dump", this));
	}

	/**
	 * <p>Return a signature for this type.</p>
	 * <p>This can be used to create an instance of this type.</p>
	 *
	 * <p>The default returns a signature without any arguments.</p>
	 */
	Signature getSignature();

	default Object create(BoundArguments arguments)
	{
		throw new UnsupportedOperationException(Utils.formatMessage("Can't create {!r} instances", this));
	}

	/**
	 * Check whether {@code object} is an instance of this type
	 */
	boolean instanceCheck(Object object);

	@Override
	default Object callUL4(List<Object> args, Map<String, Object> kwargs)
	{
		return create(new BoundArguments(getSignature(), this, args, kwargs));
	}

	@Override
	default void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<type ");
		formatter.append(getFullNameUL4());
		formatter.append(">");
	}
}
