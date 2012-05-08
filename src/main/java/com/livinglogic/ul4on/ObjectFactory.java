/*
** Copyright 2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4on;


/**
 * Utility class for reading and writing the UL4ON object serialization format.
 *
 * The UL4ON object serialization format is a simple (text-based) serialization format
 * the supports all objects supported by UL4, i.e. it supports the same type of objects
 * as JSON does (plus colors, dates and templates)
 *
 * @author W. Dörwald, A. Gaßner
 */
public interface ObjectFactory
{
	public UL4ONSerializable create();
}
