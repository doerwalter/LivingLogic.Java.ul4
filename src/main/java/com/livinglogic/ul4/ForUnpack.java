/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;

import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;

public class ForUnpack extends For
{
	protected List<String> iternames = new LinkedList<String>();

	public ForUnpack(Location location, AST container)
	{
		super(location, container);
	}

	public ForUnpack(Location location)
	{
		super(location, null);
	}

	public void appendName(String itername)
	{
		iternames.add(itername);
	}

	public String getType()
	{
		return "foru";
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("for (");
		int count = 0;
		for (String itername : iternames)
		{
			++count;
			buffer.append(FunctionRepr.call(itername));
			if (count == 1 || count != iternames.size())
				buffer.append(", ");
		}
		buffer.append(") in ");
		buffer.append(container.toString(indent));
		buffer.append("\n");
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("{\n");
		++indent;
		for (AST item : content)
			buffer.append(item.toString(indent));
		--indent;
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("}\n");
		return buffer.toString();
	}

	public static void unpackLoopVariable(EvaluationContext context, Object item, List<String> varnames)
	{
		Iterator<Object> itemIter = Utils.iterator(item);
		Iterator<String> nameIter = varnames.iterator();

		int count = 0;

		for (;;)
		{
			if (itemIter.hasNext())
			{
				if (nameIter.hasNext())
				{
					context.put(nameIter.next(), itemIter.next());
					++count;
				}
				else
				{
					throw new UnpackingException("mismatched for loop unpacking: " + count + " varnames, " + count + "+ items");
				}
			}
			else
			{
				if (nameIter.hasNext())
				{
					throw new UnpackingException("mismatched for loop unpacking: " + count + "+ varnames, " + count + " items");
				}
				else
				{
					break;
				}
			}
		}
	}

	protected void unpackLoopVariable(EvaluationContext context, Object item)
	{
		unpackLoopVariable(context, item, iternames);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(iternames);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		iternames = (List<String>)decoder.load();
	}
}
