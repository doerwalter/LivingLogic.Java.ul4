/*
** Copyright 2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4on;

import java.io.IOException;

/**
 * Utility class for reading and writing the UL4ON object serialization format.
 *
 * The UL4ON object serialization format is a simple (text-based) serialization format
 * the supports all objects supported by UL4, i.e. it supports the same type of objects
 * as JSON does (plus colors, dates and templates)
 *
 * @author W. Dörwald, A. Gaßner
 */
public interface UL4ONSerializable
{
	public String getUL4ONName();

	public void dumpUL4ON(Encoder encoder) throws IOException;

	public void loadUL4ON(Decoder decoder) throws IOException;
}
