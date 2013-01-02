/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Thrown when a key can't be found in a map. This exception is also used when
 * a variable can't be found in the top-level variables map.
 */
public class KeyException extends RuntimeException
{
	public KeyException(Object key)
	{
		super("Key '" + key + "' not found!");
	}
}
