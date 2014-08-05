/*
** Copyright 2012-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4on;

import java.io.IOException;

/**
 * Classes whose instances should be serializable with the UL4ON infrastructure
 * must implement this interface.
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
