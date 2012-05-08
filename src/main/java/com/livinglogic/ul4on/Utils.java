/*
** Copyright 2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4on;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for reading and writing the UL4ON object serialization format.
 *
 * The UL4ON object serialization format is a simple (text-based) serialization format
 * the supports all objects supported by UL4, i.e. it supports the same type of objects
 * as JSON does (plus colors, dates and templates)
 *
 * @author W. Dörwald, A. Gaßner
 */
public class Utils
{
	public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	public static Map<String, ObjectFactory> registry = new HashMap<String, ObjectFactory>();

	public static void register(String name, ObjectFactory factory)
	{
		registry.put(name, factory);
	}

	/**
	 * Return the serialized output of the object <code>data</code>.
	 * @param data the object to be dumped.
	 * @return the serialized object
	 */
	public static String dumps(Object data)
	{
		StringWriter writer = new StringWriter();
		Encoder encoder = new Encoder(writer);
		try
		{
			encoder.dump(data);
		}
		catch (IOException ioe)
		{
			// can't happen with StringWriter
		}
		
		return writer.toString();
	}

	/**
	 * Load an object by reading in the UL4ON object serialization format reader <code>reader</code>.
	 * @param reader The Reader from which to read the object
	 * @return the deserialized object
	 */
	public static Object load(Reader reader) throws IOException
	{
		try
		{
			return new Decoder(reader).load();
		}
		catch (IOException e)
		{
			// can't happen
			return null; // keeps the compiler happy
		}
	}

	/**
	 * Load an object by reading in the UL4ON object serialization format from the string <code>s</code>.
	 * @param s The object in serialized form
	 * @return the deserialized object
	 */
	public static Object loads(String s)
	{
		try
		{
			return load(new StringReader(s));
		}
		catch (IOException e)
		{
			// can only happen on short reads
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Load an object by reading in the UL4ON object serialization format from the CLOB <code>clob</code>.
	 * @param clob The CLOB that contains the object in serialized form
	 * @return the deserialized object
	 */
	public static Object load(Clob clob) throws IOException, SQLException
	{
		return load(clob.getCharacterStream());
	}
}
