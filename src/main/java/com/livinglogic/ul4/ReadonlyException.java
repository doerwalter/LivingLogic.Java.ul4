/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Thrown when an attribute is read only
 */
public class ReadonlyException extends RuntimeException
{
	public ReadonlyException(Object key)
	{
		super("Attribute '" + key + "' is readonly!");
	}
}
