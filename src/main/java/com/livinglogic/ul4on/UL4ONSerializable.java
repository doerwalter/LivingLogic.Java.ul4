/*
** Copyright 2012-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4on;

import java.io.IOException;

/**
 * Classes whose instances should be serializable with the UL4ON infrastructure
 * must implement this interface.
 *
 * If their {@link #getUL4ONID} implementation does return an identifier that is
 * not {@code null}, the object is considered to be "persistent".
 *
 * Persistent objects will survive multiple calls to
 * {@link Decoder#load(Reader)} or {@link Decoder@loads(String)}.
 * When such an object gets deserialized, and it already exists in the
 * {@code Decoder} object, the object will not be created again, but
 * {@link UL4ONSerializable#loadUL4ON(Decoder} will be called for it.
 */
public interface UL4ONSerializable
{
	/**
	 * Return a unique name for the implementing class. This string should follow
	 * Java's class naming conventions (i.e. inverted DNS), but doesn't have to
	 * be the exact class name (as this might expose the internal layout of the
	 * classes which might change over time).
	 */
	public String getUL4ONName();

	/**
	 * Return a unique identifier for the calling instance. If this string
	 * is not {@code null} it must be unique among all objects of the same type.
	 */
	default public String getUL4ONID()
	{
		return null;
	}

	/**
	 * Serialize the calling instance by writing instance data to the
	 * {@link Encoder} object. The method must use {@link Encoder#dump} to
	 * write all data to the {@link Encoder} that is required to recreate the
	 * object.
	 */
	public void dumpUL4ON(Encoder encoder) throws IOException;

	/**
	 * Initialize the calling instance by deserializing instance data from the
	 * {@link Decoder} object. This method usually uses {@link Decoder#load} to
	 * read the data that has been written by {@link #dumpUL4ON}.
	 */
	public void loadUL4ON(Decoder decoder) throws IOException;
}
