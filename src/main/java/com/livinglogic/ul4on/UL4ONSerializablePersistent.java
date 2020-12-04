/*
** Copyright 2012-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4on;

import java.io.IOException;

/**
 * Objects that get serialized by UL4ON may be persistent by implementing this
 * interface. This means that they can be uniquely identified through their
 * UL4ON type and an additional unique identifier.
 * Persistent objects will survive multiple calls to
 * {@link Decoder#load(Reader)} or {@link Decoder@loads(String)}.
 * When such an object gets deserialized, and it already exists in the
 * {@code Decoder} object, the object will not be created again, but
 * {@link UL4ONSerializable#loadUL4ON(Decoder} will be called for it.
 */
public interface UL4ONSerializablePersistent extends UL4ONSerializable
{
	/**
	 * Return a unique identifier for the calling instance. This string must be
	 * unique among all objects of the same type.
	 */
	public String getUL4ONID();
}
