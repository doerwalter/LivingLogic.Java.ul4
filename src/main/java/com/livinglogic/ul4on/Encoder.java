/*
** Copyright 2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4on;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.livinglogic.ul4.Color;

/**
 * Utility class for reading and writing the UL4ON object serialization format.
 *
 * The UL4ON object serialization format is a simple (text-based) serialization format
 * the supports all objects supported by UL4, i.e. it supports the same type of objects
 * as JSON does (plus colors, dates and templates)
 *
 * @author W. Dörwald, A. Gaßner
 */
public class Encoder
{
	private Writer writer = null;
	private List<Object> objects = new ArrayList<Object>();
	private Map<Object, Integer> object2id = new HashMap<Object, Integer>();

	public Encoder(Writer writer)
	{
		this.writer = writer;
	}

	private void record(Object obj)
	{
		object2id.put(obj, objects.size());
		objects.add(obj);
	}

	/**
	 * Writes the object <code>obj</code> to the writer in the UL4ON object serialization format.
	 * @param obj the object to be dumped.
	 * @throws IOException if writing to the stream fails
	 */
	public void dump(Object obj) throws IOException
	{
		// Have we serialized this object before?
		Integer index = object2id.get(obj);
		if (index != null || object2id.containsKey(obj))
		{
			// Yes -> output a backreference
			String indexStr = index.toString();
			writer.write("^" + indexStr + "|");
		}
		else
		{
			// No -> write the real object
			if (obj == null)
				writer.write("n");
			else if (obj instanceof Boolean)
				writer.write(((Boolean)obj).booleanValue() ? "bT" : "bF");
			else if (obj instanceof Integer || obj instanceof Long || obj instanceof Byte || obj instanceof Short || obj instanceof BigInteger)
				writer.write("i" + obj.toString() + "|");
			else if (obj instanceof Float || obj instanceof Double || obj instanceof BigDecimal)
				writer.write("f" + obj.toString() + "|");
			else if (obj instanceof String)
			{
				record(obj);
				writer.write("S" + ((String)obj).length() + "|");
				writer.write((String)obj);
			}
			else if (obj instanceof Date)
			{
				record(obj);
				writer.write("T" + Utils.dateFormat.format((Date)obj) + "000");
			}
			else if (obj instanceof Color)
			{
				record(obj);
				writer.write("C" + ((Color)obj).dump());
			}
			else if (obj instanceof UL4ONSerializable) // check this before Collection and Map
			{
				record(obj);
				writer.write("O");
				dump(((UL4ONSerializable)obj).getUL4ONName());
				((UL4ONSerializable)obj).dumpUL4ON(this);
			}
			else if (obj instanceof Collection)
			{
				record(obj);
				writer.write("L");
				for (Object o: (Collection)obj)
					dump(o);
				writer.write(".");
			}
			else if (obj instanceof Map)
			{
				record(obj);
				writer.write("D");
				for (Map.Entry entry: ((Map<Object, Object>)obj).entrySet())
				{
					dump(entry.getKey());
					dump(entry.getValue());
				}
				writer.write(".");
			}
			else
			{
				throw new RuntimeException("unknown type " + obj.getClass());
			}
		}
	}
}
