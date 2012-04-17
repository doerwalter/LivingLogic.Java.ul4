/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;

public class ForUnpack extends Block
{
	protected LinkedList<String> iternames;
	protected AST container;

	public ForUnpack(AST container)
	{
		this.iternames = new LinkedList<String>();
		this.container = container;
	}

	public ForUnpack()
	{
		this.iternames = new LinkedList<String>();
		this.container = null;
	}

	public void appendName(String itername)
	{
		iternames.add(itername);
	}

	public void setContainer(AST container)
	{
		this.container = container;
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

	public String name()
	{
		return "foru";
	}

	public void finish(String name)
	{
		if (name != null && name.length() != 0 && !name.equals("for"))
			throw new BlockException("for ended by end" + name);
	}

	private void unpackVariable(EvaluationContext context, Object item)
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

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Object container = this.container.evaluate(context);

		Iterator iterContainer = Utils.iterator(container);

		while (iterContainer.hasNext())
		{
			unpackVariable(context, iterContainer.next());

			try
			{
				super.evaluate(context);
			}
			catch (BreakException ex)
			{
				break; // breaking this while loop breaks the evaluated for loop
			}
			catch (ContinueException ex)
			{
				// doing nothing here does exactly what we need ;)
			}
		}
		return null;
	}
}
