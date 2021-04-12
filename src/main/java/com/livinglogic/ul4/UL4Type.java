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
import java.util.Collections;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.livinglogic.ul4on.ObjectFactory;
import com.livinglogic.ul4on.UL4ONSerializable;


/**
<p>An instance of {@code UL4Type} is returned when an object is asked for
its type via {@link UL4Type#getType(Object)}.</p>

<p>There are several implementations of {@code UL4Type}:</p>

<ol>
<li>{@link AbstractInstanceType} this is used for our own classes that
should be exposed to UL4. In this case the {@link AbstractInstanceType}
type object can forward most operations to the instance itself (which should
be a subclass of {@link UL4Instance}).</li>

<li>{@link AbstractType} this is used for all builtin and third party classes
because we can't</li>
</ol>
**/
public interface UL4Type extends UL4Name, UL4Repr, UL4Call, ObjectFactory
{
	default String getUL4ONName()
	{
		return null;
	}

	/**
	Convert an instance of this type to the UL4 type {@code bool}.

	@param instance The instance to be converted.
	@return the result of the conversion
	**/
	boolean boolInstance(Object instance);

	/**
	Convert an instance of this type to the UL4 type {@code int}.

	@param instance The instance to be converted.
	@return the result of the conversion
	**/
	default Number intInstance(Object instance)
	{
		throw new ArgumentTypeMismatchException("Can't convert {!t} to int!", instance);
	}

	/**
	Convert an instance of this type to the UL4 type {@code float}.

	@param instance The instance to be converted.
	@return the result of the conversion
	**/
	default Number floatInstance(Object instance)
	{
		throw new ArgumentTypeMismatchException("Can't convert {!t} to float!", instance);
	}

	/**
	Convert an instance of this type to the UL4 type {@code str}.

	@param instance The instance to be converted.
	@return the result of the conversion
	**/
	default String strInstance(Object instance)
	{
		return instance.toString();
	}

	/**
	Return the length of an instance of this type.

	@param instance The instance whose length should be returned.
	@return the length of the instance.
	**/
	default int lenInstance(Object instance)
	{
		throw new ArgumentTypeMismatchException("len({!t}) not supported!", instance);
	}

	/**
	Return the set of attribute names of an instance of this type.

	@param context The evaluation context.
	@param instance The instance whose attribute names should be.
	@return The set of attribute names.
	**/
	default Set<String> dirInstance(EvaluationContext context, Object instance)
	{
		return dirInstance(instance);
	}

	/**
	Return the set of attribute names of an instance of this type.
	This is a version that doesn't required an evaluation context.

	@param instance The instance whose attribute names should be returned.
	@return The set of attribute names.
	**/
	default Set<String> dirInstance(Object instance)
	{
		return Collections.emptySet();
	}

	/**
	Return whether an instance of this type has an attribute with the specified name.

	@param context The evaluation context.
	@param instance The instance that should be checked for the specified attribute.
	@param key The name of the attribute to be checked.
	@return Whether the instance has the specified attribute.
	**/
	default boolean hasAttr(EvaluationContext context, Object instance, String key)
	{
		return dirInstance(context, instance).contains(key);
	}

	/**
	Return whether an instance of this type has an attribute with the specified name.
	This is a version that doesn't required an evaluation context.

	@param instance The instance that should be checked for the specified attribute.
	@param key The name of the attribute to be checked.
	@return Whether the instance has the specified attribute.
	**/
	default boolean hasAttr(Object instance, String key)
	{
		return dirInstance(instance).contains(key);
	}

	default Object getAttr(EvaluationContext context, Object object, String key)
	{
		return getAttr(object, key);
	}

	default Object getAttr(Object object, String key)
	{
		throw new AttributeException(object, key);
	}

	default void setAttr(EvaluationContext context, Object object, String key, Object value)
	{
		setAttr(object, key, value);
	}

	default void setAttr(Object object, String key, Object value)
	{
		throw new ReadonlyException(object, key);
	}

	public static Map<Class, UL4Type> genericTypes = new HashMap<Class, UL4Type>();

	/**
	<p>Return the type object for the passed in object.</p>

	@param object The object whose type object should be returned.
	@return The type object for {@code object}.
	**/
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

	@Override
	default UL4ONSerializable create(String id)
	{
		throw new UnsupportedOperationException(Utils.formatMessage("Can't create {!r} instances from UL4ON dump", this));
	}

	/**
	<p>Return a signature for this type.</p>

	<p>This can be used to create an instance of this type.</p>

	<p>The default returns a signature without any arguments.</p>

	@return The signature
	**/
	
	Signature getSignature();

	default Object create(BoundArguments arguments)
	{
		throw new UnsupportedOperationException(Utils.formatMessage("Can't create {!r} instances", this));
	}

	/**
	Check whether {@code object} is an instance of this type

	@param object the object to be checked

	@return {@code true} if {@code object} is an instance of this type, {@code false otherwise}
	**/
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
