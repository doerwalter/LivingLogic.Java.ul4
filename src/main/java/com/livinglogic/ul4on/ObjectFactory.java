/*
** Copyright 2012-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4on;


/**
 * An {@code ObjectFactory} object is responsible for creating an object that
 * implements the {@link UL4ONSerializable} interface. It is used by
 * {@link Decoder} to create an object of the appropriate type when deserializing
 * objects.
 */
public interface ObjectFactory
{
	/**
	 * Create an object of the appropriate type. The content of the object will
	 * we recreated after this call by {@link UL4ONSerializable#loadUL4ON}.
	 */
	public UL4ONSerializable create();
}
