/*
** Copyright 2012-2014 by LivingLogic AG, Bayreuth/Germany
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
 * The UL4ON object serialization format is a simple (text-based) extensible
 * object serialization format the supports all objects supported by UL4, i.e.
 * it supports the same type of objects as JSON does (plus colors, dates and
 * templates).
 *
 * Furthermore it is extensible by implementing the {@link UL4ONSerializable}
 * interface and registering your class via {@link #register}.
 *
 * @author W. Dörwald, A. Gaßner
 */
public class Utils
{
	/**
	 * Date format for serializing/deserializing {@code Date} objects.
	 */
	public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	/**
	 * Registry where all {@link ObjectFactory} objects registered via
	 * {@link #register} are stored.
	 */
	public static Map<String, ObjectFactory> registry = new HashMap<String, ObjectFactory>();

	/**
	 * Register a class for the UL4ON serialization machinery.
	 *
	 * @param name the name of the class as returned by its
	 *             {@link UL4ONSerializable#getUL4ONName}.
	 * @param factory An {@link ObjectFactory} object that will be used to create
	 *                an "empty" instance of the class.
	 */
	public static void register(String name, ObjectFactory factory)
	{
		registry.put(name, factory);
	}

	/**
	 * Return the serialized UL4ON output of the object {@code data}.
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
	 * Load an object by reading in the UL4ON object serialization format from {@code reader}.
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
	 * Load an object by reading in the UL4ON object serialization format from the string {@code s}.
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
	 * Load an object by reading in the UL4ON object serialization format from the CLOB {@code clob}.
	 * @param clob The CLOB that contains the object in serialized form
	 * @return the deserialized object
	 */
	public static Object load(Clob clob) throws IOException, SQLException
	{
		return load(clob.getCharacterStream());
	}
}
