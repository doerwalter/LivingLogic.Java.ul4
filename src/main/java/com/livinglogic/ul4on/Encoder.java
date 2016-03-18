/*
** Copyright 2012-2016 by LivingLogic AG, Bayreuth/Germany
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
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import com.livinglogic.ul4.Color;
import com.livinglogic.ul4.MonthDelta;
import com.livinglogic.ul4.TimeDelta;
import com.livinglogic.ul4.Slice;
import com.livinglogic.ul4.FunctionRepr;
import com.livinglogic.ul4.BoundDateMethodYear;
import com.livinglogic.ul4.BoundDateMethodMonth;
import com.livinglogic.ul4.BoundDateMethodDay;
import com.livinglogic.ul4.BoundDateMethodHour;
import com.livinglogic.ul4.BoundDateMethodMinute;
import com.livinglogic.ul4.BoundDateMethodSecond;
import com.livinglogic.ul4.BoundDateMethodMicrosecond;

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
	 * {@code indent} specifies which string should be used for indentation
	 * when pretty printing (<code>null</code> means no pretty printing).
	 */
	private String indent = null;

	/**
	 * {@code level} specifies the indentation level when pretty printing.
	 */
	private int level = 0;

	/**
	 * {@code first} specifies wether any output has been written or not.
	 */
	private boolean first = true;


	/**
	 * A {@code Map} that maps certain objects that have been output before to an
	 * index that specifies at which position in the list of unique objects that
	 * have been output before this object is.
	 */
	private Map<Object, Integer> object2id = new IdentityHashMap<Object, Integer>();

	/**
	 * Create an {@code Encoder} object for writing serialized UL4ON output
	 * to the {@code Writer} {@code writer}
	 */
	public Encoder(Writer writer, String indent)
	{
		this.writer = writer;
		this.indent = indent;
		this.level = 0;
		this.first = true;
	}

	/**
	 * Create an {@code Encoder} object for writing serialized UL4ON output
	 * to the {@code Writer} {@code writer}
	 */
	public Encoder(Writer writer)
	{
		this(writer, null);
	}

	/**
	 * Record that the object {@code obj} has been output and should be available
	 * to output backreferences to this object later.
	 */
	private void record(Object obj)
	{
		object2id.put(obj, object2id.size());
	}

	private void line(String line, Object... additionalObjs) throws IOException
	{
		// Write indentation/separator
		if (indent != null)
		{
			for (int i = 0; i < level; ++i)
				writer.write(indent);
		}
		else
		{
			if (!first)
				writer.write(" ");
		}
		first = false;

		writer.write(line);

		String oldindent = indent;
		try
		{
			indent = null;

			for (Object obj : additionalObjs)
				dump(obj);
		}
		finally
		{
			indent = oldindent;
		}

		if (indent != null)
			writer.write("\n");
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
			line("^" + indexStr);
		}
		else
		{
			// No -> write the real object
			if (obj == null)
				line("n");
			else if (obj instanceof Boolean)
				line(((Boolean)obj).booleanValue() ? "bT" : "bF");
			else if (obj instanceof Integer || obj instanceof Long || obj instanceof Byte || obj instanceof Short || obj instanceof BigInteger)
				line("i" + obj.toString());
			else if (obj instanceof Float || obj instanceof Double || obj instanceof BigDecimal)
				line("f" + obj.toString());
			else if (obj instanceof String)
			{
				record(obj);
				line("S" + FunctionRepr.call((String)obj));
			}
			else if (obj instanceof Date)
			{
				record(obj);
				Date date = (Date)obj;
				line("Z", BoundDateMethodYear.call(date), BoundDateMethodMonth.call(date), BoundDateMethodDay.call(date), BoundDateMethodHour.call(date), BoundDateMethodMinute.call(date), BoundDateMethodSecond.call(date), BoundDateMethodMicrosecond.call(date));
			}
			else if (obj instanceof TimeDelta)
			{
				record(obj);
				TimeDelta td = (TimeDelta)obj;
				line("T", td.getDays(), td.getSeconds(), td.getMicroseconds());
			}
			else if (obj instanceof MonthDelta)
			{
				record(obj);
				line("M", ((MonthDelta)obj).getMonths());
			}
			else if (obj instanceof Color)
			{
				record(obj);
				Color color = (Color)obj;
				line("C", color.getR(), color.getG(), color.getB(), color.getA());
			}
			else if (obj instanceof Slice)
			{
				line("r", ((Slice)obj).getStart(), ((Slice)obj).getStop());
			}
			else if (obj instanceof UL4ONSerializable) // check this before Collection and Map
			{
				record(obj);
				line("O", ((UL4ONSerializable)obj).getUL4ONName());
				++level;
				((UL4ONSerializable)obj).dumpUL4ON(this);
				--level;
				line(")");
			}
			else if (obj instanceof Set)
			{
				record(obj);
				line("Y");
				++level;
				for (Object item: (Set<Object>)obj)
				{
					dump(item);
				}
				--level;
				line("}");
			}
			else if (obj instanceof Collection)
			{
				record(obj);
				line("L");
				++level;
				for (Object o: (Collection)obj)
					dump(o);
				--level;
				line("]");
			}
			else if (obj instanceof Map)
			{
				record(obj);
				line("D");
				++level;
				for (Map.Entry entry: ((Map<Object, Object>)obj).entrySet())
				{
					dump(entry.getKey());
					dump(entry.getValue());
				}
				--level;
				line("}");
			}
			else
			{
				throw new RuntimeException("unknown type " + obj.getClass());
			}
		}
	}
}
