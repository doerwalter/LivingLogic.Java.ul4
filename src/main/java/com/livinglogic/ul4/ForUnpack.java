/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;

public class ForUnpack extends For
{
	protected LinkedList<String> iternames;

	public ForUnpack(AST container)
	{
		super(container);
		this.iternames = new LinkedList<String>();
	}

	public ForUnpack()
	{
		super(null);
		this.iternames = new LinkedList<String>();
	}

	public void appendName(String itername)
	{
		iternames.add(itername);
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
			buffer.append(Utils.repr(itername));
			if (count == 1 || count != iternames.size())
				buffer.append(", ");
		}
		buffer.append(") in ");
		buffer.append(container);
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

	public String getType()
	{
		return "foru";
	}

	protected void unpackLoopVariable(EvaluationContext context, Object item)
	{
		Iterator<Object> itemIter = Utils.iterator(item);
		Iterator<String> nameIter = iternames.iterator();

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
}
