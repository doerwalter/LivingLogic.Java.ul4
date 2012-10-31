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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.livinglogic.ul4.Undefined;
import com.livinglogic.ul4.Color;
import com.livinglogic.ul4.TimeDelta;
import com.livinglogic.ul4.MonthDelta;

/**
 * An {@code Encoder} object wraps a {@code Writer} object and can dump any object
 * to this {@code Writer} in the UL4ON serialization format.
 */
public class Encoder
{
	/**
	 * The {@code Writer} instance where the final output will be written.
	 */
	private Writer writer = null;

	/**
	 * A {@code Map} that maps certain objects that have been output before to an
	 * index that specifies at which position in the list of unique objects that
	 * have been output before this object is.
	 */
	private Map<Object, Integer> object2id = new HashMap<Object, Integer>();

	/**
	 * Create an {@code Encoder} object for writing serialized UL4ON output
	 * to the {@code Writer} {@code writer}
	 */
	public Encoder(Writer writer)
	{
		this.writer = writer;
	}

	/**
	 * Record that the object {@code obj} has been output and should be available
	 * to output backreferences to this object later.
	 */
	private void record(Object obj)
	{
		object2id.put(obj, object2id.size());
	}

	/**
	 * Writes the object {@code obj} to the writer in the UL4ON object serialization format.
	 * @param obj the object to be dumped.
	 * @throws IOException if writing to the stream fails
	 */
	public void dump(Object obj) throws IOException
	{
		// Have we serialized this object before?
		Integer index = object2id.get(obj);
		if (index != null)
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
			else if (obj == Undefined.undefined)
				writer.write("u");
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
				writer.write("Z" + Utils.dateFormat.format((Date)obj) + "000");
			}
			else if (obj instanceof TimeDelta)
			{
				record(obj);
				TimeDelta td = (TimeDelta)obj;
				writer.write("T" + td.getDays() + "|" + td.getSeconds() + "|" + td.getMicroseconds() + "|");
			}
			else if (obj instanceof MonthDelta)
			{
				record(obj);
				writer.write("M" + ((MonthDelta)obj).getMonths() + "|");
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
				writer.write("]");
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
				writer.write("}");
			}
			else
			{
				throw new RuntimeException("unknown type " + obj.getClass());
			}
		}
	}
}
