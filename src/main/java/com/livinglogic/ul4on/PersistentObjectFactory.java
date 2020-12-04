/*
** Copyright 2012-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4on;


/**
 * A {@code PersistentObjectFactory} object is responsible for creating a
 * persistent object that implements the {@link UL4ONSerializablePersistent}
 * interface. It is used by * {@link Decoder} to create an object of the
 * appropriate type when deserializing objects.
 */
public interface PersistentObjectFactory
{
	/**
	 * Create an object of the appropriate type. The content of the object will
	 * we recreated after this call by {@link UL4ONSerializable#loadUL4ON}.
	 */
	public UL4ONSerializablePersistent create(String id);
}
